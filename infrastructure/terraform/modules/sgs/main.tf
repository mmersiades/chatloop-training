resource "aws_security_group" "database" {
  name        = "${var.resource_prefix}-db-sg"
  description = "allow traffic on the database port only"
  vpc_id      = var.vpc_id

  tags        = merge(var.tags, { Name="${var.resource_prefix}-db-sg" })

  ingress {
    from_port   = "5432"
    to_port     = "5432"
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}