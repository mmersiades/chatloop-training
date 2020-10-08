terraform {
  backend "s3" {
    bucket = "chatloop-terraform"
    key    = "production-user-training/state"
    region = "ap-southeast-2"
  }
}
