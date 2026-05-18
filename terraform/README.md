# WorkHub Terraform (Kubernetes Track)

Provisions namespace, ConfigMap, Secret, Deployment, and Service for WorkHub on an existing cluster (minikube/kind).

## Prerequisites

- Terraform >= 1.5
- kubectl configured
- Postgres and RabbitMQ already deployed in the cluster (see `k8s/` manifests)

## Usage

```bash
cd terraform
terraform init
terraform plan -var="db_password=workhub123" -var="jwt_secret=mySecretKey123456789012345678901234567890"
terraform apply -var="db_password=workhub123" -var="jwt_secret=mySecretKey123456789012345678901234567890"
```

Copy `terraform.tfvars.example` to `terraform.tfvars` and adjust values locally (do not commit secrets).
