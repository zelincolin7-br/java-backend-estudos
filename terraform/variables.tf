variable "db_host" {
  type    = string
  default = "localhost"
}

variable "db_port" {
  type    = number
  default = 5432
}

variable "db_admin_user" {
  type    = string
  default = "postgres"
}

variable "db_admin_password" {
  type      = string
  default   = "postgres"
  sensitive = true
}

variable "rabbitmq_host" {
  type    = string
  default = "localhost"
}

variable "rabbitmq_port" {
  type    = number
  default = 15672 # Porta HTTP da API/Management do RabbitMQ
}

variable "rabbitmq_user" {
  type    = string
  default = "guest"
}

variable "rabbitmq_password" {
  type      = string
  default   = "guest"
  sensitive = true
}
