resource "aws_ecs_task_definition" "order_api" {
  family                   = "order-api"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]

  cpu    = "512"
  memory = "512"

  execution_role_arn = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "order-api"
      image     = "${aws_ecr_repository.order_api.repository_url}:v1"
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"

        options = {
          awslogs-group         = aws_cloudwatch_log_group.order_api.name
          awslogs-region        = "ap-south-1"
          awslogs-stream-prefix = "order-api"
        }
      }
    }
  ])

  tags = {
    Name = "order-api-task"
  }
}


resource "aws_ecs_task_definition" "order_worker" {
  family                   = "order-worker"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]

  cpu    = "512"
  memory = "512"

  execution_role_arn = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "order-worker"
      image     = "${aws_ecr_repository.order_worker.repository_url}:v1"
      essential = true

      portMappings = [
        {
          containerPort = 8081
          hostPort      = 8081
          protocol      = "tcp"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"

        options = {
          awslogs-group         = aws_cloudwatch_log_group.order_worker.name
          awslogs-region        = "ap-south-1"
          awslogs-stream-prefix = "order-worker"
        }
      }
    }
  ])

  tags = {
    Name = "order-worker-task"
  }
}