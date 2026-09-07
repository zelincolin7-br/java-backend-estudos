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
  }
}

# Conexão com o PostgreSQL local (Docker)
provider "postgresql" {
  host            = var.db_host
  port            = var.db_port
  username        = var.db_admin_user
  password        = var.db_admin_password
  sslmode         = "disable"
  connect_timeout = 15
}

provider "rabbitmq" {
  endpoint = "${var.rabbitmq_host}:${var.rabbitmq_port}"
  username = var.rabbitmq_user
  password = var.rabbitmq_password
}
