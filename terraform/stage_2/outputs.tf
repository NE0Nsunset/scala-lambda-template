output "s3_website_url" {
  value = "Frontend Url: ${aws_api_gateway_deployment.a.invoke_url}"
}

output "api_gateway_url" {
  value = "Backend Url: ${aws_api_gateway_deployment.a.invoke_url}/api/"
}

output "static_url"{
  value = "Static Url: ${local.static_s3}"
}