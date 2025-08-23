#!/bin/bash
# Despliega PostgreSQL usando StatefulSet y Services (headless + externo) en un namespace dado.
# Se asume que los manifiestos existen en el mismo directorio:
#   2-secret-postgres.yaml
#   3-configmap-postgres.yaml
#   4-service-headless-postgres.yaml
#   6-statefulset-postgres.yaml
#   5-service-postgres-external.yaml  (opcional para acceso desde el host)
#
# Sobre `set -euo pipefail`:
#   - Se activa una "red de seguridad" para que el script falle rápido ante errores silenciosos.
#   - -e (errexit): si cualquier comando devuelve código != 0, el script se detiene.
#   - -u (nounset): si se usa una variable no definida, el script se detiene.
#   - -o pipefail: en cadenas con pipes (a | b | c), si falla cualquiera, el script lo refleja y se detiene.
#   Estas banderas evitan despliegues a medias o estados inconsistentes.

set -euo pipefail

# Namespace donde se desplegarán los recursos.
# Se define aquí para permitir reutilizar el script en distintos entornos.
NAMESPACE="ecommerce-dev"

echo "Verificando si el namespace '${NAMESPACE}' existe..."
if ! kubectl get namespace "${NAMESPACE}" >/dev/null 2>&1; then
  echo "Creando namespace '${NAMESPACE}'..."
  kubectl create namespace "${NAMESPACE}"
else
  echo "El namespace '${NAMESPACE}' ya existe."
fi

echo "Aplicando Secret y ConfigMap (credenciales y configuración no sensible)..."
kubectl apply -f 2-secret-postgres.yaml -n "${NAMESPACE}"
kubectl apply -f 3-configmap-postgres.yaml -n "${NAMESPACE}"

echo "Aplicando Service headless (DNS estable requerido por el StatefulSet)..."
# Nota: el Service headless no expone la BD hacia afuera; solo sirve para descubrimiento interno.
kubectl apply -f 4-service-headless-postgres.yaml -n "${NAMESPACE}"

echo "Aplicando StatefulSet (crea el Pod y el PVC dinámico vía volumeClaimTemplates)..."
# Importante: el StatefulSet reemplaza al Deployment para bases de datos y preserva el volumen por réplica.
kubectl apply -f 6-statefulset-postgres.yaml -n "${NAMESPACE}"

echo "Descubriendo el nombre del PVC generado por el StatefulSet..."
# El nombre del PVC sigue el patrón <volumeClaimTemplates.name>-<statefulsetName>-<ordinal> (p. ej., data-postgres-0).
# Se obtiene el prefijo (VCT) y el nombre del StatefulSet (STS) para construirlo de forma robusta.
VCT=""
STS=""
# Se espera a que el StatefulSet sea visible para poder leer sus campos.
until VCT=$(kubectl -n "${NAMESPACE}" get sts postgres -o jsonpath='{.spec.volumeClaimTemplates[0].metadata.name}' 2>/dev/null); do
  sleep 2
done
until STS=$(kubectl -n "${NAMESPACE}" get sts postgres -o jsonpath='{.metadata.name}' 2>/dev/null); do
  sleep 2
done
PVC_NAME="${VCT}-${STS}-0"
echo "PVC esperado: ${PVC_NAME}"

echo "Esperando a que el PVC '${PVC_NAME}' exista en el API..."
until kubectl -n "${NAMESPACE}" get pvc "${PVC_NAME}" >/dev/null 2>&1; do
  sleep 2
done

echo "Esperando a que el PVC '${PVC_NAME}' quede en phase=Bound..."
# Nota: 'kubectl wait --for=condition=Bound' no aplica a PVC, porque 'Bound' es phase, no una condition.
for i in {1..120}; do  # 120 * 5s = 10 minutos
  PHASE=$(kubectl -n "${NAMESPACE}" get pvc "${PVC_NAME}" -o jsonpath='{.status.phase}' 2>/dev/null || true)
  echo "  PVC phase: ${PHASE:-desconocido}"
  if [[ "${PHASE}" == "Bound" ]]; then
    echo "PVC '${PVC_NAME}' está Bound."
    break
  fi
  sleep 5
done
if [[ "${PHASE:-}" != "Bound" ]]; then
  echo "El PVC '${PVC_NAME}' no llegó a Bound. Mostrando últimos eventos para diagnóstico:"
  kubectl -n "${NAMESPACE}" get events --sort-by=.metadata.creationTimestamp | tail -n 30 || true
  exit 1
fi

echo "Esperando a que el StatefulSet 'postgres' esté listo (rollout status)..."
kubectl -n "${NAMESPACE}" rollout status statefulset/postgres --timeout=300s

echo "Aplicando Service externo (NodePort) para acceso desde fuera del clúster..."
# En on-prem (Minikube): NodePort permite conexión desde el host con IP de Minikube + puerto del nodo.
# En cloud, este Service se puede cambiar a type=LoadBalancer sin modificar el resto.
kubectl apply -f 5-service-postgres-external.yaml -n "${NAMESPACE}"

echo ""
echo "Recursos desplegados en '${NAMESPACE}':"
kubectl -n "${NAMESPACE}" get pods,svc,pvc

echo ""
echo "Prueba de conexión desde el host (NodePort):"
echo "  minikube ip"
echo "  psql -h <IP_DE_MINIKUBE> -p 31092 -U postgres -d ecommerce"
echo ""
echo "Alternativa con helper de Minikube:"
echo "  minikube service postgres-external-ecommerce -n ${NAMESPACE} --url"
echo "  # Usar el host:puerto que se imprima con el cliente psql (solo importan host y puerto)."
