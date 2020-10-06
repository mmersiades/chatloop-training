# secrets
data "aws_secretsmanager_secret_version" "db_password" {
  secret_id = "${var.resource_prefix}-db-password"
}

# rds
resource "aws_db_subnet_group" "chatloop" {
  name        = "${var.resource_prefix}-subnet-group"
  subnet_ids  = var.vpc_subnet_ids
}

resource "aws_db_instance" "chatloop" {
  allocated_storage         = 10
  backup_retention_period   = 10
  backup_window             = "04:00-05:00"
  db_subnet_group_name      = aws_db_subnet_group.chatloop.id
  engine                    = "postgres"
  engine_version            = "11.4"
  final_snapshot_identifier = "snapshot-${var.resource_prefix}-${md5(timestamp())}"
  instance_class            = "db.t3.micro"
  identifier                = var.resource_prefix
  max_allocated_storage     = 100
  maintenance_window        = "Mon:00:00-Mon:03:00"
  multi_az                  = false
  name                      = var.db_name
  password                  = data.aws_secretsmanager_secret_version.db_password.secret_string
  parameter_group_name      = "default.postgres11"
  port                      = 5432
  snapshot_identifier       = var.source_snapshot_identifier
  skip_final_snapshot       = false
  storage_type              = "gp2"
  storage_encrypted         = true
  username                  = var.db_username
  vpc_security_group_ids    = [var.database_security_group_id]

  lifecycle {
    ignore_changes = [snapshot_identifier, storage_encrypted]
  }


  tags = var.tags
}

# rds
resource "aws_db_instance" "chatloop_public" {
  allocated_storage         = 10
  backup_retention_period   = 10
  backup_window             = "04:00-05:00"
  db_subnet_group_name      = "default"
  engine                    = "postgres"
  engine_version            = "11.4"
  final_snapshot_identifier = "snapshot-${var.resource_prefix}-db-${md5(timestamp())}"
  instance_class            = "db.t3.micro"
  identifier                = "${var.resource_prefix}-db"
  max_allocated_storage     = 100
  maintenance_window        = "Mon:00:00-Mon:03:00"
  multi_az                  = false
  name                      = var.db_name
  password                  = data.aws_secretsmanager_secret_version.db_password.secret_string
  parameter_group_name      = "default.postgres11"
  port                      = 5432
  publicly_accessible       = true
  snapshot_identifier       = var.source_snapshot_identifier
  skip_final_snapshot       = false
  storage_type              = "gp2"
  storage_encrypted         = true
  username                  = var.db_username
  vpc_security_group_ids    = [var.database_security_group_id]

  tags = var.tags
}
