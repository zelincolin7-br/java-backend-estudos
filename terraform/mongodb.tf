# Imagem oficial do MongoDB
resource "docker_image" "mongodb" {
  name         = "mongo:7.0"
  keep_locally = true
}

# Volume persistente para os dados do MongoDB
resource "docker_volume" "mongodb_data" {
  name = "mongodb_data"
}

# Container do MongoDB
resource "docker_container" "mongodb" {
  name  = "mongodb-local"
  image = docker_image.mongodb.image_id

  ports {
    internal = 27017
    external = 27017
  }

  env = [
    "MONGO_INITDB_ROOT_USERNAME=admin",
    "MONGO_INITDB_ROOT_PASSWORD=adminpass"
  ]

  volumes {
    volume_name    = docker_volume.mongodb_data.name
    container_path = "/data/db"
  }

  restart = "unless-stopped"
}