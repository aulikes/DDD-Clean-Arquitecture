#!/bin/bash

NAMESPACE="ecommerce-dev"

#echo "Eliminando recursos anteriores (si existen)..."
#kubectl delete deployment rabbitmq-deployment -n "$NAMESPACE" --ignore-not-found
#kubectl delete service rabbitmq-service -n "$NAMESPACE" --ignore-not-found
#kubectl delete pvc rabbitmq-pvc -n "$NAMESPACE" --ignore-not-found
#kubectl delete secret rabbitmq-secret -n "$NAMESPACE" --ignore-not-found
#kubectl delete configmap rabbitmq-config -n "$NAMESPACE" --ignore-not-found

echo ""
echo "Verificando si el namespace '$NAMESPACE' existe..."
if ! kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
  echo "Namespace no existe. Creando..."
  kubectl create namespace "$NAMESPACE"
else
  echo "Namespace '$NAMESPACE' ya existe."
fi

echo ""
echo "Aplicando archivos YAML..."
kubectl apply -f 3-secret-rabbit.yaml -n "$NAMESPACE"
kubectl apply -f 4-configmap-rabbit.yaml -n "$NAMESPACE"
kubectl apply -f 5-headless-rabbitmq.yaml -n "$NAMESPACE"
kubectl apply -f 6-service-rabbit.yaml -n "$NAMESPACE"
kubectl apply -f 7-statefulset-rabbitmq.yaml -n "$NAMESPACE"
#kubectl rollout status sts/rabbitmq-ecommerce -n "$NAMESPACE" --timeout=180s

echo ""
echo "Verificando usuario y contraseña (decodificados):"
USER=$(kubectl get secret rabbitmq-secret-ecommerce -n "$NAMESPACE" -o jsonpath="{.data.rabbitmq-username}" | base64 --decode)
PASS=$(kubectl get secret rabbitmq-secret-ecommerce -n "$NAMESPACE" -o jsonpath="{.data.rabbitmq-password}" | base64 --decode)

echo "Usuario: $USER"
echo "Contraseña: $PASS"

echo ""
echo "RabbitMQ desplegado correctamente en el namespace '$NAMESPACE'."
