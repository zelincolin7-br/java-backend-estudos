# main.tf

terraform {
  required_version = ">= 1.5.0"
  required_providers {
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "~> 1.22.0"
    }
    rabbitmq = {
      source  = "cyrilgdn/rabbitmq"
      version = "~> 1.8.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.2"
    }
  }
}

# Provider do Docker local
provider "docker" {}

# --- RECURSOS DOCKER: POSTGRESQL ---
resource "docker_image" "postgres" {
  name         = "postgres:latest"
  keep_locally = true
}

resource "docker_container" "postgres" {
  name         = "postgres-local"
  image        = docker_image.postgres.image_id
  network_mode = "bridge"

  ports {
    internal = 5432
    external = var.db_port
  }

  env = [
    "POSTGRES_USER=${var.db_admin_user}",
    "POSTGRES_PASSWORD=${var.db_admin_password}"
  ]

  restart = "unless-stopped"
}

# --- RECURSOS DOCKER: RABBITMQ ---
resource "docker_image" "rabbitmq" {
  name         = "rabbitmq:3-management"
  keep_locally = true
}

resource "docker_container" "rabbitmq" {
  name         = "rabbitmq-local"
  image        = docker_image.rabbitmq.image_id
  network_mode = "bridge"

  ports {
    internal = 5672
    external = 5672
  }

  ports {
    internal = 15672
    external = var.rabbitmq_port
  }

  env = [
    "RABBITMQ_DEFAULT_USER=${var.rabbitmq_user}",
    "RABBITMQ_DEFAULT_PASS=${var.rabbitmq_password}"
  ]

  restart = "unless-stopped"
}

# --- PROVIDERS DE CONEXÃO ---
provider "postgresql" {
  host            = var.db_host
  port            = var.db_port
  username        = var.db_admin_user
  password        = var.db_admin_password
  sslmode         = "disable"
  connect_timeout = 15
}

provider "rabbitmq" {
  # Garantindo a formatação sem duplicar o protocolo http://
  endpoint = startswith(var.rabbitmq_host, "http") ? "${var.rabbitmq_host}:${var.rabbitmq_port}" : "http://${var.rabbitmq_host}:${var.rabbitmq_port}"
  username = var.rabbitmq_user
  password = var.rabbitmq_password
}