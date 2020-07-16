resource "aws_s3_bucket" "frontend_ui" {
  bucket = "${var.application-identity}-frontend-bucket"
  acl = "public-read"
  region = "${var.aws_region}"

  force_destroy = true
}

data "template_file" "frontend_ui_public_policy" {
  template = "${file("${path.module}/templates/s3_public_policy.json")}"

  vars {
    bucket_arn = "${aws_s3_bucket.frontend_ui.arn}"
  }
}

resource "aws_s3_bucket_policy" "frontend_ui_s3_public_policy" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  policy = "${data.template_file.frontend_ui_public_policy.rendered}"
}


// static files
data "template_file" "client_config_script" {
  template = "${file("${path.module}/templates/client_config_script.js")}"

  vars {
    api_backend_url = "${local.backend_api_or_override}"
    static_url = "https://${aws_s3_bucket.frontend_ui.bucket_domain_name}"
    stage_prefix = "${local.stagename_or_override}"
  }
}


resource "aws_s3_bucket_object" "client-opt" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key    = "client-opt.js"
  source = "${path.module}/../../client/target/scala-2.13/client-opt.js"
  content_type = "text/html"
  acl    = "public-read"


  # The filemd5() function is available in Terraform 0.11.12 and later
  # For Terraform 0.11.11 and earlier, use the md5() function and the file() function:
  etag = "${md5(file("${path.module}/../../client/target/scala-2.13/client-opt.js"))}"
}

resource "aws_s3_bucket_object" "client-jsdeps" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "client-jsdeps.js"
  source = "${path.module}/../../client/target/scala-2.13/client-jsdeps.js"
  content_type = "text/html"
  acl    = "public-read"
  etag = "${md5(file("${path.module}/../../client/target/scala-2.13/client-jsdeps.js"))}"
}

resource "aws_s3_bucket_object" "client_config_js" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "js/client_config.js"
  content = "${data.template_file.client_config_script.rendered}"
  content_type = "text/javascripts"
  acl    = "public-read"
}