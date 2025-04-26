provider "kubernetes" {
  config_path = "C:/Program Files/Jenkins/.kube/config"
}

# Boot Deployment
resource "kubernetes_deployment" "healthtart_boot_deploy" {
  metadata {
    name = "healthtart-boot-deploy"
  }
  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "healthtart-boot"
      }
    }
    template {
      metadata {
        labels = {
          app = "healthtart-boot"
        }
      }
      spec {
        container {
          name  = "boot-container"
          image = "kyeongseok/healthtart-boot:latest"
          image_pull_policy = "Always"
          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

# Boot Service
resource "kubernetes_service" "healthtart_boot_service" {
  metadata {
    name = "healthtart-boot-service"
  }
  spec {
    selector = {
      app = "healthtart-boot"
    }
    port {
      port        = 8001
      target_port = 8080
    }
    type = "ClusterIP"
  }
}

# Vue Deployment
resource "kubernetes_deployment" "healthtart_vue_deploy" {
  metadata {
    name = "healthtart-vue-deploy"
  }
  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "healthtart-vue"
      }
    }
    template {
      metadata {
        labels = {
          app = "healthtart-vue"
        }
      }
      spec {
        container {
          name  = "vue-container"
          image = "kyeongseok/healthtart-vue:latest"
          image_pull_policy = "Always"
          port {
            container_port = 80
          }
        }
      }
    }
  }
}

# Vue Service
resource "kubernetes_service" "healthtart_vue_service" {
  metadata {
    name = "healthtart-vue-service"
  }
  spec {
    selector = {
      app = "healthtart-vue"
    }
    port {
      port        = 8000
      target_port = 80
    }
    type = "ClusterIP"
  }
}

# Ingress 설정
resource "kubernetes_ingress_v1" "healthtart_ingress" {
  metadata {
    name = "healthtart-ingress"
    annotations = {
      "nginx.ingress.kubernetes.io/ssl-redirect" = "false"
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$2"
    }
  }
  spec {
    ingress_class_name = "nginx"

    rule {
      http {
        path {
          path = "/(/|$)(.*)$"
          path_type = "ImplementationSpecific"
          backend {
            service {
              name = "healthtart-vue-service"
              port {
                number = 8000
              }
            }
          }
        }

        path {
          path = "/boot(/|$)(.*)$"
          path_type = "ImplementationSpecific"
          backend {
            service {
              name = "healthtart-boot-service"
              port {
                number = 8001
              }
            }
          }
        }
      }
    }
  }
  depends_on = [
    kubernetes_service.healthtart_vue_service,
    kubernetes_service.healthtart_boot_service
  ]
}
