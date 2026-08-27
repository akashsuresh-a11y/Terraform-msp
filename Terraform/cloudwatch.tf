resource "aws_cloudwatch_log_group" "order_api" {
  name              = "/ecs/order-api"
  retention_in_days = 7

  tags = {
    Name = "order-api-logs"
  }
}

resource "aws_cloudwatch_log_group" "order_worker" {
  name              = "/ecs/order-worker"
  retention_in_days = 7

  tags = {
    Name = "order-worker-logs"
  }
}