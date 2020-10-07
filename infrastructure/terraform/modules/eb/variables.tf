variable "resource_prefix" {
  description = "prefix to be used when naming resources"
  type = string
}

variable "beanstalk_application" {
  description = "the name of the beanstalk application"
  type = string
}

variable "beanstalk_environment" {
  description = "the name of the beanstalk environment"
  type = string
}

variable "tags" {
  description = "tags to set on resources"
  type = map(string)
  default = {}
}
