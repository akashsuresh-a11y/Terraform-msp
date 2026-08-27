resource "aws_ecs_cluster" "main" {
  name = "terraform-msp-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "terraform-msp-cluster"
  }
}