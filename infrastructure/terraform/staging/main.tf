locals {
  profile         = "chatloop"
  resource_prefix = "${var.project}-${var.environment}-${var.namespace}"
}

provider "aws" {
  version = "~> 2.0"
  region  = "ap-southeast-2"
  profile = local.profile
}

module "vpc" {
  source = "../modules/vpc"
}

module "sgs" {
  source = "../modules/sgs"

  resource_prefix = local.resource_prefix

  vpc_id          = module.vpc.vpc_id
  vpc_cidr_block  = module.vpc.vpc_cidr_block

  tags = {
    Terraform   = "true"
    Namespace   = var.namespace
    Environment = var.environment
    Project     = var.project
  }
}

module "rds" {
  source = "../modules/rds"

  resource_prefix            = local.resource_prefix
  source_snapshot_identifier = "staging-api-encrypted-202009290900"
  vpc_subnet_ids             = module.vpc.vpc_subnet_ids
  database_security_group_id = module.sgs.sg_database_id
  db_name                    = "ebdb"
  db_username                = "neonkingkong"

  tags = {
    Terraform   = "true"
    Namespace   = var.namespace
    Environment = var.environment
    Project     = var.project
  }
}