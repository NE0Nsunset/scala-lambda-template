resource "random_string" "app_uuid" {
  length = 7
  upper = false
  lower = true
  special = false
  number = true
}