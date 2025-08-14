#!/bin/bash

# Script de despliegue de Zookeeper en Kubernetes (entorno local)

NAMESPACE="ecommerce-dev"

# Verificar si el namespace existe
echo "Verificando si el namespace '$NAMESPACE' existe..."
if ! kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
  echo "Namespace no existe. Creando..."
  kubectl create namespace "$NAMESPACE"
else
  echo "Namespace '$NAMESPACE' ya existe."
fi

# Aplicar todos los archivos YAML
echo "Aplicando manifiestos YAML para Zookeeper..."
kubectl apply -f 2-configmap-zookeeper.yaml -n "$NAMESPACE"
kubectl apply -f 3-zookeeper-headless.yaml -n "$NAMESPACE"
kubectl apply -f 4-service-zookeeper.yaml -n "$NAMESPACE"
kubectl apply -f 5-zookeeper-statefulset.yaml -n "$NAMESPACE"

echo "Esperando que Zookeeper esté listo..."
kubectl wait --for=condition=ready pod -l app=zookeeper -n "$NAMESPACE" --timeout=90s

kubectl apply -f 2-configmap-kafka.yaml -n "$NAMESPACE"
kubectl apply -f 3-kafka-headless.yaml -n "$NAMESPACE"
kubectl apply -f 4-service-kafka.yaml -n "$NAMESPACE"
kubectl apply -f 5-kafka-statefulset.yaml -n "$NAMESPACE"

echo ""
echo "Zookeeper y Kafka desplegados correctamente en el namespace '$NAMESPACE'."
echo "Verifica el estado con: kubectl get pods -n $NAMESPACE -l app=zookeeper"
