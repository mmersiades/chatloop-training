output "db_id" {
  value = aws_db_instance.chatloop.id
}

output "db_name" {
  value = aws_db_instance.chatloop.name
}

output "db_username" {
  value = aws_db_instance.chatloop.username
}

output "db_address" {
  value = element(concat(aws_db_instance.chatloop.*.address, list("")), 0)
}
