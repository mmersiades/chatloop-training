output "vpc_id" {
  value = aws_default_vpc.default.id
}

output "vpc_subnet_ids" {
  value = aws_default_subnet.default.*.id
}

output "vpc_cidr_block" {
  value = aws_default_vpc.default.cidr_block
}

