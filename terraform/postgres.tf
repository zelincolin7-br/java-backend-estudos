# Criação do banco de dados do order-service
resource "postgresql_database" "order_db" {
  name  = "order_db"
  owner = var.db_admin_user
}

# Criação do banco de dados do auth-service
resource "postgresql_database" "auth_db" {
  name  = "auth_db"
  owner = var.db_admin_user
}

# Criação do banco de dados do inventory-service
resource "postgresql_database" "inventory_db" {
  name  = "inventory_db"
  owner = var.db_admin_user
}
