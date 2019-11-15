// frontend.handler
exports.handler = (event, context, callback) => {
    const {STATIC_URL = ""} = process.env;

    //const RESPONSE_HEADERS = process.env.RESPONSE_HEADERS  // TODO handle additional headers like CORS, etc.

    const html =
        `<html>
            <head>
            <!--Import Google Icon Font-->
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
            <!--Import materialize.css-->
            <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/materialize.min.css"  media="screen,projection"/>
            <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/main.css"  media="screen,projection"/>
            <!--Let browser know website is optimized for mobile-->
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>   
            <title>Scala Lambda Template</title>
            </head>
            <body>
            <main id="lambda-app"></main>
            <footer id="lambda-app-footer"></footer>
            <script type="text/javascript" src="${STATIC_URL}/js/client_config.js"></script>
            <script type="text/javascript" src="${STATIC_URL}/js/materialize.min.js"></script>
            <script type="text/javascript" src="${STATIC_URL}/client-jsdeps.js"></script>
            <script type="text/javascript" src="${STATIC_URL}/client-opt.js"></script>
            <script>FrontendApp.main()</script>
            </body>
        </html>
      `;

    callback(null, {
        statusCode: "200",
        body: html,
        headers: {"content-type": "text/html"},
    });
};
