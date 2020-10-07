output "db_id" {
  value = local.is_user_training ? aws_db_instance.chatloop_public.*.id : aws_db_instance.chatloop.*.id
}

output "db_name" {
  value = local.is_user_training ? element(concat(aws_db_instance.chatloop_public.*.name, list("")), 0) : element(concat(aws_db_instance.chatloop.*.name, list("")), 0)
}

output "db_username" {
  value = local.is_user_training ? aws_db_instance.chatloop_public.*.username : aws_db_instance.chatloop.*.username
}

output "db_address" {
  value = local.is_user_training ? element(concat(aws_db_instance.chatloop_public.*.address, list("")), 0) : element(concat(aws_db_instance.chatloop.*.address, list("")), 0)
}
