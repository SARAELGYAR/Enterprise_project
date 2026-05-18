variable "kubeconfig_path" {
  description = "Path to kubeconfig (minikube: ~/.kube/config)"
  type        = string
  default     = "~/.kube/config"
}

variable "namespace" {
  type    = string
  default = "workhub"
}

variable "app_image" {
  type    = string
  default = "workhub-app:latest"
}

variable "app_replicas" {
  type    = number
  default = 1
}

variable "db_host" {
  type    = string
  default = "postgres-service"
}

variable "db_port" {
  type    = string
  default = "5432"
}

variable "db_name" {
  type    = string
  default = "workhub"
}

variable "db_user" {
  type    = string
  default = "workhub"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "jwt_secret" {
  type      = string
  sensitive = true
}

variable "rabbitmq_host" {
  type    = string
  default = "rabbitmq-service"
}

variable "rabbitmq_port" {
  type    = string
  default = "5672"
}

variable "rabbitmq_user" {
  type    = string
  default = "guest"
}

variable "rabbitmq_password" {
  type      = string
  sensitive = true
  default   = "guest"
}
