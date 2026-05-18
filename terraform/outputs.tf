output "namespace" {
  value = kubernetes_namespace.workhub.metadata[0].name
}

output "service_name" {
  value = kubernetes_service.workhub_service.metadata[0].name
}

output "deployment_name" {
  value = kubernetes_deployment.workhub_app.metadata[0].name
}
