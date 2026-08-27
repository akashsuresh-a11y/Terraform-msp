resource "aws_ecr_repository" "order_api" {
  name                 = "order-api"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "order-api"
  }
}

resource "aws_ecr_repository" "order_worker" {
  name                 = "order-worker"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "order-worker"
  }
}