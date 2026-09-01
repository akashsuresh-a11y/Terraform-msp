# SNS Topic

resource "aws_sns_topic" "order_notifications" {
  name = "order-notifications"

  tags = {
    Name = "order-notifications-topic"
  }
}


# SQS Queue

resource "aws_sqs_queue" "order_notifications" {
  name = "order-notifications-queue"

  visibility_timeout_seconds = 30

  tags = {
    Name = "order-notifications-queue"
  }
}


# SNS → SQS Subscription

resource "aws_sns_topic_subscription" "order_notifications" {
  topic_arn = aws_sns_topic.order_notifications.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.order_notifications.arn
}


# Allow SNS to send messages to SQS

resource "aws_sqs_queue_policy" "order_notifications" {
  queue_url = aws_sqs_queue.order_notifications.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "AllowSNSToSendMessage"
        Effect = "Allow"

        Principal = {
          Service = "sns.amazonaws.com"
        }

        Action = "sqs:SendMessage"

        Resource = aws_sqs_queue.order_notifications.arn

        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.order_notifications.arn
          }
        }
      }
    ]
  })
}