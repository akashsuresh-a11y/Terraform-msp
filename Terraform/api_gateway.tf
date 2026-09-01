# Find the currently running ECS EC2 instance
data "aws_instances" "ecs" {
  instance_state_names = ["running"]

  filter {
    name   = "tag:Name"
    values = ["terraform-msp-ecs-instance"]
  }
}


# API Gateway REST API

resource "aws_api_gateway_rest_api" "order_api" {
  name        = "terraform-msp-order-api"
  description = "Public API for Terraform MSP Order API"

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Name = "terraform-msp-order-api"
  }
}


# /orders

resource "aws_api_gateway_resource" "orders" {
  rest_api_id = aws_api_gateway_rest_api.order_api.id
  parent_id   = aws_api_gateway_rest_api.order_api.root_resource_id
  path_part   = "orders"
}


# POST /orders

resource "aws_api_gateway_method" "orders_post" {
  rest_api_id   = aws_api_gateway_rest_api.order_api.id
  resource_id   = aws_api_gateway_resource.orders.id
  http_method   = "POST"
  authorization = "NONE"
}


# API Gateway -> EC2 Order API

resource "aws_api_gateway_integration" "orders_post" {
  rest_api_id = aws_api_gateway_rest_api.order_api.id
  resource_id = aws_api_gateway_resource.orders.id
  http_method = aws_api_gateway_method.orders_post.http_method

  type = "HTTP"

  integration_http_method = "POST"

  uri = "http://${data.aws_instances.ecs.public_ips[0]}:8080/orders"
}


# 200 response

resource "aws_api_gateway_method_response" "orders_post" {
  rest_api_id = aws_api_gateway_rest_api.order_api.id
  resource_id = aws_api_gateway_resource.orders.id
  http_method = aws_api_gateway_method.orders_post.http_method
  status_code = "200"
}


resource "aws_api_gateway_integration_response" "orders_post" {
  rest_api_id = aws_api_gateway_rest_api.order_api.id
  resource_id = aws_api_gateway_resource.orders.id
  http_method = aws_api_gateway_method.orders_post.http_method
  status_code = "200"

  depends_on = [
    aws_api_gateway_integration.orders_post
  ]
}


# Deployment

resource "aws_api_gateway_deployment" "order_api" {
  rest_api_id = aws_api_gateway_rest_api.order_api.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_resource.orders.id,
      aws_api_gateway_method.orders_post.id,
      aws_api_gateway_integration.orders_post.id,
      aws_api_gateway_integration.orders_post.uri
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    aws_api_gateway_integration.orders_post
  ]
}


# prod stage

resource "aws_api_gateway_stage" "prod" {
  rest_api_id   = aws_api_gateway_rest_api.order_api.id
  deployment_id = aws_api_gateway_deployment.order_api.id
  stage_name    = "prod"
}