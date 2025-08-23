#!/bin/bash
# Despliega RabbitMQ (Secret + ConfigMap + Services + StatefulSet) en un namespace dado.
# Archivos esperados en el directorio actual:
#   3-secret-rabbit.yaml
#   4-configmap-rabbit.yaml
#   5-headless-rabbitmq.yaml
#   6-service-rabbit.yaml
#   7-statefulset-rabbitmq.yaml
#
# set -euo pipefail:
#   -e  → si un comando falla, el script se detiene
#   -u  → si se usa una variable no definida, error
#   -o pipefail → cualquier fallo en un pipe hace fallar al script
set -euo pipefail

# Parámetros
NAMESPACE="ecommerce-dev"
RABBIT_STS="rabbitmq-ecommerce"            # Debe coincidir con metadata.name del StatefulSet
RABBIT_SECRET="rabbitmq-secret-ecommerce"  # Debe coincidir con metadata.name del Secret

echo "Verificando si el namespace '${NAMESPACE}' existe..."
if ! kubectl get namespace "${NAMESPACE}" >/dev/null 2>&1; then
  echo "Creando namespace '${NAMESPACE}'..."
  kubectl create namespace "${NAMESPACE}"
else
  echo "El namespace '${NAMESPACE}' ya existe."
fi

echo ""
echo "Aplicando manifiestos de RabbitMQ..."
kubectl apply -f 3-secret-rabbit.yaml        -n "${NAMESPACE}"
kubectl apply -f 4-configmap-rabbit.yaml     -n "${NAMESPACE}"
kubectl apply -f 5-headless-rabbitmq.yaml    -n "${NAMESPACE}"
kubectl apply -f 6-service-rabbit.yaml       -n "${NAMESPACE}"
kubectl apply -f 7-statefulset-rabbitmq.yaml -n "${NAMESPACE}"

echo ""
echo "Esperando recursos de almacenamiento y Pods..."

# Espera a que el StatefulSet exista para poder leer su spec
until kubectl -n "${NAMESPACE}" get sts "${RABBIT_STS}" >/dev/null 2>&1; do
  sleep 2
done

# Determina el nombre del PVC: <volumeClaimTemplates.name>-<stsName>-<ordinal>
VCT="$(kubectl -n "${NAMESPACE}" get sts "${RABBIT_STS}" -o jsonpath='{.spec.volumeClaimTemplates[0].metadata.name}')"
PVC_NAME="${VCT}-${RABBIT_STS}-0"
echo "PVC esperado: ${PVC_NAME}"

# Espera a que el PVC exista
echo "Esperando a que el PVC '${PVC_NAME}' exista..."
until kubectl -n "${NAMESPACE}" get pvc "${PVC_NAME}" >/dev/null 2>&1; do
  sleep 2
done

# Espera a que el PVC llegue a phase=Bound (PVC no tiene condition=Bound)
echo "Esperando a que el PVC '${PVC_NAME}' llegue a phase=Bound..."
PHASE=""
for i in {1..120}; do  # 120 * 5s = 10 minutos
  PHASE="$(kubectl -n "${NAMESPACE}" get pvc "${PVC_NAME}" -o jsonpath='{.status.phase}' 2>/dev/null || true)"
  echo "  PVC phase: ${PHASE:-desconocido}"
  if [[ "${PHASE}" == "Bound" ]]; then
    echo "PVC '${PVC_NAME}' está Bound."
    break
  fi
  sleep 5
done
if [[ "${PHASE:-}" != "Bound" ]]; then
  echo "El PVC '${PVC_NAME}' no llegó a Bound. Últimos eventos para diagnóstico:"
  kubectl -n "${NAMESPACE}" get events --sort-by=.metadata.creationTimestamp | tail -n 50 || true
  exit 1
fi

echo ""
echo "Esperando a que el StatefulSet '${RABBIT_STS}' complete el rollout..."
kubectl -n "${NAMESPACE}" rollout status "statefulset/${RABBIT_STS}" --timeout=300s

echo ""
echo "Decodificando credenciales (solo para verificación en dev):"
ENC_USER="$(kubectl get secret "${RABBIT_SECRET}" -n "${NAMESPACE}" -o jsonpath='{.data.rabbitmq-username}')"
ENC_PASS="$(kubectl get secret "${RABBIT_SECRET}" -n "${NAMESPACE}" -o jsonpath='{.data.rabbitmq-password}')"
# Soporta 'base64 -d' (BusyBox) y 'base64 --decode' (coreutils)
USER="$( (printf "%s" "${ENC_USER}" | base64 -d 2>/dev/null) || (printf "%s" "${ENC_USER}" | base64 --decode 2>/dev/null) || true )"
PASS="$( (printf "%s" "${ENC_PASS}" | base64 -d 2>/dev/null) || (printf "%s" "${ENC_PASS}" | base64 --decode 2>/dev/null) || true )"
echo "Usuario: ${USER:-<no-disponible>}"
echo "Contraseña: ${PASS:-<no-disponible>}"

echo ""
echo "RabbitMQ desplegado correctamente en '${NAMESPACE}'. Resumen:
