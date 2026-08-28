data "aws_ami" "ecs" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }

  filter {
    name   = "state"
    values = ["available"]
  }
}


resource "aws_launch_template" "ecs" {
  name = "terraform-msp-ecs-launch-template"

  image_id      = data.aws_ami.ecs.id
  instance_type = "t3.small"
  key_name      = "testcase4"

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_instance.name
  }

  vpc_security_group_ids = [
    aws_security_group.ecs.id
  ]

  user_data = base64encode(<<-EOF
    #!/bin/bash

    echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
  EOF
  )

  tag_specifications {
    resource_type = "instance"

    tags = {
      Name = "terraform-msp-ecs-instance"
    }
  }
}


resource "aws_autoscaling_group" "ecs" {
  name = "terraform-msp-ecs-asg"

  min_size         = 0
  max_size         = 1
  desired_capacity = 0

  vpc_zone_identifier = [
    aws_subnet.public.id
  ]

  launch_template {
    id      = aws_launch_template.ecs.id
    version = "$Latest"
  }

  tag {
    key                 = "Name"
    value               = "terraform-msp-ecs-instance"
    propagate_at_launch = true
  }

  lifecycle {
    ignore_changes = [
      desired_capacity,
      tag
    ]
  }
}


resource "aws_ecs_capacity_provider" "ecs" {
  name = "terraform-msp-capacity-provider"

  auto_scaling_group_provider {
    auto_scaling_group_arn = aws_autoscaling_group.ecs.arn

    managed_scaling {
      status = "ENABLED"
    }

    managed_termination_protection = "DISABLED"
  }
}


resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name = aws_ecs_cluster.main.name

  capacity_providers = [
    aws_ecs_capacity_provider.ecs.name
  ]

  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.ecs.name
    weight            = 1
    base              = 1
  }
}