terraform {
  required_version = ">= 1.5.0"
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  }
}

provider "kubernetes" {
  config_path = var.kubeconfig_path
}

resource "kubernetes_namespace" "workhub" {
  metadata {
    name = var.namespace
  }
}

resource "kubernetes_config_map" "workhub_config" {
  metadata {
    name      = "workhub-config"
    namespace = kubernetes_namespace.workhub.metadata[0].name
  }

  data = {
    DB_HOST       = var.db_host
    DB_PORT       = var.db_port
    DB_NAME       = var.db_name
    DB_USER       = var.db_user
    SHOW_SQL      = "false"
    RABBITMQ_HOST = var.rabbitmq_host
    RABBITMQ_PORT = var.rabbitmq_port
  }
}

resource "kubernetes_secret" "workhub_secret" {
  metadata {
    name      = "workhub-secret"
    namespace = kubernetes_namespace.workhub.metadata[0].name
  }

  data = {
    DB_PASSWORD       = var.db_password
    JWT_SECRET        = var.jwt_secret
    RABBITMQ_USER     = var.rabbitmq_user
    RABBITMQ_PASSWORD = var.rabbitmq_password
  }
}

resource "kubernetes_deployment" "workhub_app" {
  metadata {
    name      = "workhub-deployment"
    namespace = kubernetes_namespace.workhub.metadata[0].name
    labels = {
      app = "workhub"
    }
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = {
        app = "workhub"
      }
    }

    template {
      metadata {
        labels = {
          app = "workhub"
        }
      }

      spec {
        container {
          name  = "workhub"
          image = var.app_image

          port {
            container_port = 8080
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.workhub_config.metadata[0].name
            }
          }

          env {
            name = "DB_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.workhub_secret.metadata[0].name
                key  = "DB_PASSWORD"
              }
            }
          }

          env {
            name = "JWT_SECRET"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.workhub_secret.metadata[0].name
                key  = "JWT_SECRET"
              }
            }
          }

          readiness_probe {
            http_get {
              path = "/actuator/health/readiness"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }

          liveness_probe {
            http_get {
              path = "/actuator/health/liveness"
              port = 8080
            }
            initial_delay_seconds = 45
            period_seconds        = 15
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "workhub_service" {
  metadata {
    name      = "workhub-service"
    namespace = kubernetes_namespace.workhub.metadata[0].name
  }

  spec {
    selector = {
      app = "workhub"
    }

    port {
      port        = 80
      target_port = 8080
    }

    type = "NodePort"
  }
}
