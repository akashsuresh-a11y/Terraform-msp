resource "aws_sqs_queue" "orders" {
  name = "terraform-msp-orders"

  visibility_timeout_seconds = 60

  message_retention_seconds = 86400

  tags = {
    Name = "terraform-msp-orders"
  }
}