# WorkHub Deployment Guide

##  Local Docker Compose (Required)

### Prerequisites
- Docker Desktop installed and running

### Steps
```bash
docker-compose up --build
```
App runs at: http://localhost:8080

### Verify
```bash
curl http://localhost:8080/actuator/health
```

---

## B Kubernetes (Local - Minikube)

### Prerequisites
- minikube installed
- kubectl installed

### Steps
```bash
# Start minikube
minikube start

# Build Docker image and load into minikube
docker build -t workhub-app:latest .
minikube image load workhub-app:latest

# Apply manifests (order matters)
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/rabbitmq-deployment.yaml
kubectl apply -f k8s/rabbitmq-service.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Access the app
kubectl port-forward service/workhub-service 8080:80
```

App runs at: http://localhost:8080

### Verify probes
```bash
curl http://localhost:8080/actuator/health/readiness
curl http://localhost:8080/actuator/health/liveness
```

---

## C Terraform (Kubernetes Track)

### Prerequisites
- Terraform installed
- minikube running

### Steps
```bash
cd terraform
terraform init
terraform plan -var="db_password=workhub123" -var="jwt_secret=mySecretKey123456789012345678901234567890"
terraform apply -var="db_password=workhub123" -var="jwt_secret=mySecretKey123456789012345678901234567890"
```

---

# CI/CD Pipeline

Runs automatically on every push to master:
1. Build + Tests via Gradle
2. Build Docker image