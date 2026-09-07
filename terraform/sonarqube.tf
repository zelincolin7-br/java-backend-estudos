# Configuração do provedor Docker
provider "docker" {}

# Imagem oficial do SonarQube Community LTS
resource "docker_image" "sonarqube" {
  name         = "sonarqube:lts-community"
  keep_locally = true
}

# Container do SonarQube exposto na porta 9000
resource "docker_container" "sonarqube" {
  name  = "sonarqube-local"
  image = docker_image.sonarqube.image_id

  ports {
    internal = 9000
    external = 9000
  }

  env = [
    "SONAR_SEARCH_JAVAADDITIONALOPTS=-Dnode.store.allow_mmap=false"
  ]

  restart = "unless-stopped"
}