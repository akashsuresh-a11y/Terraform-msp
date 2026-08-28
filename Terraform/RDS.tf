resource "aws_db_subnet_group" "rds" {
  name = "terraform-msp-rds-subnet-group"

  subnet_ids = [
    aws_subnet.private.id,
    aws_subnet.private_b.id
  ]

  tags = {
    Name = "terraform-msp-rds-subnet-group"
  }
}


resource "aws_db_instance" "postgres" {
  identifier = "terraform-msp-postgres"

  engine         = "postgres"
  engine_version = "16"

  instance_class = "db.t3.micro"

  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = "orders"
  username = "postgres"
  password = "var.db_password"

  port = 5432

  db_subnet_group_name = aws_db_subnet_group.rds.name

  vpc_security_group_ids = [
    aws_security_group.rds.id
  ]

  publicly_accessible = false
  multi_az            = false
  skip_final_snapshot = true
  deletion_protection = false

  tags = {
    Name = "terraform-msp-postgres"
  }
}