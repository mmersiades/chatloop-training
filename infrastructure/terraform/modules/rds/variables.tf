variable "resource_prefix" {
  description = "prefix to be used when naming resources"
  type = string
}

variable "source_snapshot_identifier" {
  description = "an existing snapshot id to create the database from"
  default = null
  type = string
}

variable "db_name" {
  description = "the name of the database"
  type = string
}

variable "db_username" {
  description = "the name of the database user"
  type = string
}

variable "vpc_subnet_ids" {
  description = "list of vpc subnet ids to put the database in"
  type = list(string)
  default = []
}

variable "database_security_group_id" {
  description = "security group id which the database will use"
  type = string
}

variable "tags" {
  description = "tags to set on resources"
  type = map(string)
  default = {}
}
