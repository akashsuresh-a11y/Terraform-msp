resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "terraform-msp.local"
  description = "Service discovery namespace for Terraform MSP"

  vpc = aws_vpc.main.id

  tags = {
    Name = "terraform-msp-service-discovery"
  }
}


resource "aws_service_discovery_service" "order_api" {
  name = "order-api"

  dns_config {
    namespace_id   = aws_service_discovery_private_dns_namespace.main.id
    routing_policy = "MULTIVALUE"

    dns_records {
      ttl  = 10
      type = "SRV"
    }
  }

  tags = {
    Name = "terraform-msp-order-api-discovery"
  }
}


resource "aws_service_discovery_service" "order_worker" {
  name = "order-worker"

  dns_config {
    namespace_id   = aws_service_discovery_private_dns_namespace.main.id
    routing_policy = "MULTIVALUE"

    dns_records {
      ttl  = 10
      type = "SRV"
    }
  }

  tags = {
    Name = "terraform-msp-order-worker-discovery"
  }
}