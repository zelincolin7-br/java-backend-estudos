# Configuração do provedor Docker

# Imagem oficial do SonarQube Community LTS
resource "docker_image" "sonarqube" {
  name         = "sonarqube:lts-community"
  keep_locally = true
}

# Volumes do Docker para persistência de dados, extensões e logs
resource "docker_volume" "sonarqube_data" {
  name = "sonarqube_data"
}

resource "docker_volume" "sonarqube_extensions" {
  name = "sonarqube_extensions"
}

resource "docker_volume" "sonarqube_logs" {
  name = "sonarqube_logs"
}

# Container do SonarQube exposto na porta 9000
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
    "SONAR_CE_JAVAOPTS=-Xms512m -Xmx1536m"
  ]

  # Mapeamento dos volumes para salvar as configurações
  volumes {
    volume_name    = docker_volume.sonarqube_data.name
    container_path = "/opt/sonarqube/data"
  }

  volumes {
    volume_name    = docker_volume.sonarqube_extensions.name
    container_path = "/opt/sonarqube/extensions"
  }

  volumes {
    volume_name    = docker_volume.sonarqube_logs.name
    container_path = "/opt/sonarqube/logs"
  }

  restart = "unless-stopped"
}