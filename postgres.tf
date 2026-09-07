# Banco do serviço de pedidos (order-service)
resource "postgresql_database" "order_db" {
  name  = "order_db"
  owner = var.db_admin_user
}

# Banco do serviço de autenticação e usuários (auth-user-service)
resource "postgresql_database" "auth_db" {
  name  = "auth_db"
  owner = var.db_admin_user
}

# Banco do serviço de estoque e insumos (inventory-service)
resource "postgresql_database" "inventory_db" {
  name  = "inventory_db"
  owner = var.db_admin_user
}
