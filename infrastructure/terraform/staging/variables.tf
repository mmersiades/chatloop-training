variable "region" {
  description = "the aws region to deploy to"
  default = "ap-southeast-2"
}

variable "namespace" {
  description = "name of a logical namespace, i.e. api"
  default = "api"
}

variable "environment" {
  description = "the logical environment i.e. development, staging"
  default = "staging"
}

variable "project" {
  description = "name of the project"
  default = "chatloop"
}