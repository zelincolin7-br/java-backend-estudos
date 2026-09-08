# Configuração do provedor Docker


# Imagem oficial do SonarQube Community LTS
resource "docker_image" "sonarqube" {
  name         = "sonarqube:lts-community"
  keep_locally = true
}

# Container do SonarQube exposto na porta 9001
resource "docker_container" "sonarqube" {
  name  = "sonarqube-app"
  image = docker_image.sonarqube.image_id

  ports {
    internal = 9000
    external = 9000
  }

  env = [
    "SONAR_SEARCH_JAVAADDITIONALOPTS=-Dnode.store.allow_mmap=false",
    "SONAR_WEB_JAVAOPTS=-Xms512m -Xmx1024m",
    "SONAR_CE_JAVAOPTS=-Xms512m -Xmx1024m"
  ]

  restart = "unless-stopped"
}