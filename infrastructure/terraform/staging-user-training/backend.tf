terraform {
  backend "s3" {
    bucket = "chatloop-terraform"
    key    = "staging-user-training/state"
    region = "ap-southeast-2"
  }
}
