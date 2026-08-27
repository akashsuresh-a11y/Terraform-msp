resource "aws_ecs_service" "order_worker" {
  name            = "order-worker"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.order_worker.arn

  desired_count = 1

  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs.name
    weight            = 1
    base              = 1
  }

  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  tags = {
    Name = "order-worker-service"
  }
}


resource "aws_ecs_service" "order_api" {
  name            = "order-api"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.order_api.arn

  desired_count = 1

  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs.name
    weight            = 1
    base              = 1
  }

  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  tags = {
    Name = "order-api-service"
  }
}