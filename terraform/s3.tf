resource "aws_s3_bucket" "frontend_ui" {
  bucket = "scala-lambda-${local.app_uuid}"
  acl = "public-read"
  region = "${var.aws_region}"

  website {
    index_document = "index.html"
    error_document = "index.html" // redirects all subroutes to index.html, in production you should use cloudfront for this to avoid 404s
  }
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
    api_backend_url = "${aws_api_gateway_deployment.a.invoke_url}"
  }

  depends_on = [
    "aws_api_gateway_deployment.a",
  ]
}

resource "aws_s3_bucket_object" "client-opt" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key    = "client-opt.js"
  source = "../client/target/scala-2.12/client-opt.js"
  content_type = "text/html"
  acl    = "public-read"


  # The filemd5() function is available in Terraform 0.11.12 and later
  # For Terraform 0.11.11 and earlier, use the md5() function and the file() function:
  etag = "${md5(file("../client/target/scala-2.12/client-opt.js"))}"
}

resource "aws_s3_bucket_object" "client-jsdeps" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "client-jsdeps.js"
  source = "../client/target/scala-2.12/client-jsdeps.js"
  content_type = "text/html"
  acl    = "public-read"
  etag = "${md5(file("../client/target/scala-2.12/client-jsdeps.js"))}"
}

resource "aws_s3_bucket_object" "indexhtml" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "index.html"
  source = "../lambda-offline/src/main/public/index.html"
  content_type = "text/html"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/index.html"))}"
}

resource "aws_s3_bucket_object" "materializecss" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "css/materialize.min.css"
  source = "../lambda-offline/src/main/public/css/materialize.min.css"
  content_type = "text/css"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/css/materialize.min.css"))}"
}

resource "aws_s3_bucket_object" "stylecss" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "css/style.css"
  source = "../lambda-offline/src/main/public/css/style.css"
  content_type = "text/css"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/css/style.css"))}"
}

resource "aws_s3_bucket_object" "maincss" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "css/main.css"
  source = "../lambda-offline/src/main/public/css/main.css"
  content_type = "text/css"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/css/main.css"))}"
}

resource "aws_s3_bucket_object" "backgroundimg" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "static/img/traintracks.jpg"
  source = "../lambda-offline/src/main/public/img/traintracks.jpg"
  content_type = "text/css"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/img/traintracks.jpg"))}"
}

resource "aws_s3_bucket_object" "materializejs" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "js/materialize.min.js"
  source = "../lambda-offline/src/main/public/js/materialize.min.js"
  content_type = "text/javascript"
  acl    = "public-read"
  etag = "${md5(file("../lambda-offline/src/main/public/js/materialize.min.js"))}"
}

resource "aws_s3_bucket_object" "client_config_js" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "js/client_config.js"
  content = "${data.template_file.client_config_script.rendered}"
  content_type = "text/javascripts"
  acl    = "public-read"
}