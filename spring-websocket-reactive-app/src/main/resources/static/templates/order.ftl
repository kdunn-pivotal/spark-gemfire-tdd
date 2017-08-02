<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Reactive App Example with Pivotal GemFire, Spring Boot, and WebSockets</title>
        <link href="css/bootstrap.css" rel="stylesheet" />
        <link href="css/jumbotron-narrow.css" rel="stylesheet" />
        <link href="css/jquery.dataTables.min.css" rel="stylesheet" >
        <link href="css/custom.css" rel="stylesheet" />

        <!-- TODO: move all of these to webjars -->
        <script type="text/javascript" src="js/jquery-1.12.4.js" ></script>

        <script src="/webjars/sockjs-client/sockjs.min.js"></script>
        <script src="/webjars/stomp-websocket/stomp.min.js"></script>

    </head>

    <body>

        <div class="jumbotron">
            <h2>Details for Order ${orderId}</h2>
        </div>

        <div id="content">
        </div>

    </body>

    <script>

        function onDocumentReady() {
            webSocketConnect(${orderId});
        }

        if ( !window.isLoaded ) {
            window.addEventListener("load", function() {
                onDocumentReady();
            }, false);
        }
        else {
            onDocumentReady();
        }

    </script>

    <script type="text/javascript" src="js/websocketListener.js" ></script>

</html>