variable "aws_region" {
  description = "AWS region for the microservices infrastructure"
  type        = string
  default     = "ap-south-1"
}

variable "db_password" {
  description = "Master password for PostgreSQL RDS"
  type        = string
  sensitive   = true
}