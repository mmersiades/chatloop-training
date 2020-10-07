terraform {
  backend "s3" {
    bucket = "chatloop-terraform"
    key    = "staging/state"
    region = "ap-southeast-2"
  }
}
