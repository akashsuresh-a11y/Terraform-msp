#ECR OUTPUT

output "order_api_repository_url" {
  description = "ECR repository URL for order-api"
  value       = aws_ecr_repository.order_api.repository_url
}

output "order_worker_repository_url" {
  description = "ECR repository URL for order-worker"
  value       = aws_ecr_repository.order_worker.repository_url
}