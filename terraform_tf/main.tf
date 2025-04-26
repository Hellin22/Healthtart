provider "kubernetes" {
  config_path = "C:/Program Files/Jenkins/.kube/config"
}

# Spring Boot Deployment
resource "kubernetes_deployment" "healthtart_boot_dep" {
  metadata {
    name = "healthtart-boot-dep"
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
          image = "kyeongseok/healthtart-boot:latest"  # 예시 버전
          name  = "boot-container"
          image_pull_policy = "Always"
          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

# Vue Deployment
resource "kubernetes_deployment" "healthtart_vue_dep" {
  metadata {
    name = "healthtart-vue-dep"
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
          image = "kyeongseok/healthtart-vue:latest"
          name  = "vue-container"
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
resource "kubernetes_service" "healthtart_vue_ser" {
  metadata {
    name = "healthtart-vue-ser"
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

# Boot Service
resource "kubernetes_service" "healthtart_boot_ser" {
  metadata {
    name = "healthtart-boot-ser"
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
          # vue ingress 수정
          path = "/(/|$)(.*)$"
          path_type = "ImplementationSpecific"
          backend {
            service {
              name = "healthtart-vue-ser"
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
              name = "healthtart-boot-ser"
              port {
                number = 8001
              }
            }
          }
        }
      }
    }
  }
}
