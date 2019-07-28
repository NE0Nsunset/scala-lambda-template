output "s3_website_url" {
  value = "Frontend Url: http://${aws_s3_bucket.frontend_ui.website_endpoint}"
}

output "api_gateway_url" {
  value = "Backend Url: ${aws_api_gateway_deployment.a.invoke_url}/api/"
}