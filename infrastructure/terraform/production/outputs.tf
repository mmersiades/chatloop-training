output "vpc_id" {
  value = module.vpc.vpc_id
}

output "database_security_group" {
  value = module.sgs.sg_database_name
}

output "database_name" {
  value = module.rds.db_name
}

output "database_endpoint" {
  value = module.rds.db_address
}
