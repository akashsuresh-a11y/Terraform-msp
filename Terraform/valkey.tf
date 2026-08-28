resource "aws_elasticache_subnet_group" "valkey" {
  name = "terraform-msp-valkey-subnet-group"

  subnet_ids = [
    aws_subnet.private.id,
    aws_subnet.private_b.id
  ]

  tags = {
    Name = "terraform-msp-valkey-subnet-group"
  }
}

resource "aws_elasticache_replication_group" "valkey" {
  replication_group_id = "terraform-msp-valkey"
  description          = "Valkey cache for Terraform MSP"

  engine         = "valkey"
  engine_version = "7.2"

  node_type            = "cache.t3.micro"
  num_cache_clusters   = 1

  port = 6379

  subnet_group_name  = aws_elasticache_subnet_group.valkey.name
  security_group_ids = [aws_security_group.valkey.id]

  automatic_failover_enabled = false
  multi_az_enabled            = false

  at_rest_encryption_enabled = true
  transit_encryption_enabled = false

  tags = {
    Name = "terraform-msp-valkey"
  }
}