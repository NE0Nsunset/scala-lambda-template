resource "aws_s3_bucket" "frontend_ui" {
  bucket = "scala-lambda-${local.app_uuid}"
  acl = "public-read"
  region = "${var.aws_region}"

  website {
    index_document = "index.html"
    error_document = "error.html"
  }
}

data "template_file" "frontend_ui_public_policy" {
  template = "${file("${path.module}/templates/s3_public_policy.json")}"

  vars {
    bucket_arn = "${aws_s3_bucket.frontend_ui.arn}"
    cf_access_iam_util_arn = "${var.s3_utility_user_arn}"
  }
}

resource "aws_s3_bucket_policy" "frontend_ui_s3_public_policy" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  policy = "${data.template_file.frontend_ui_public_policy.rendered}"
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
  //etag = "${filemd5("../client/target/scala-2.12/client-opt.js")}"
}

resource "aws_s3_bucket_object" "client-jsdeps" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "client-jsdeps.js"
  source = "../client/target/scala-2.12/client-jsdeps.js"
  content_type = "text/html"
  acl    = "public-read"

  # The filemd5() function is available in Terraform 0.11.12 and later
  # For Terraform 0.11.11 and earlier, use the md5() function and the file() function:
  etag = "${md5(file("../client/target/scala-2.12/client-jsdeps.js"))}"
}

resource "aws_s3_bucket_object" "indexhtml" {
  bucket = "${aws_s3_bucket.frontend_ui.id}"
  key = "index.html"
  source = "../lambda-offline/src/main/public/index.html"
  content_type = "text/html"

  acl    = "public-read"


  # The filemd5() function is available in Terraform 0.11.12 and later
  # For Terraform 0.11.11 and earlier, use the md5() function and the file() function:
  etag = "${md5(file("../lambda-offline/src/main/public/index.html"))}"
}