variable "resource_prefix" {
  description = "prefix for AWS resources"
  type = string
}

variable "vpc_cidr_block" {
  description = "the vpc cidr block"
  type = string
}

variable "vpc_id" {
  description = "id of the vpc"
  type = string
}

variable "tags" {
  description = "tags to set on resources."
  type = map(string)
  default = {}
}