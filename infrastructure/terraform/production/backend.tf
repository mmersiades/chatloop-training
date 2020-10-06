terraform {
  backend "s3" {
    bucket = "chatloop-terraform"
    key    = "production/state"
    region = "ap-southeast-2"
  }
}
