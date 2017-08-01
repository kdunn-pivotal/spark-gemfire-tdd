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
    <script type="text/javascript" src="js/d3.v3.min.js"></script>

    <script type="text/javascript" src="js/jquery.dataTables.min.js" ></script>

    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>

</head>

<body >
<div class="jumbotron">
    <h1>Order detail lookup</h1>
    <p></p>
    <hr />
    <div></div>
    <div id="remote">
        <p><input type="text" id="searchText" class="inputbox" placeholder="Which order are you looking for?"></p>
    </div>
    <div>
        <p><input class="btn btn-success" type="submit" value="Search" onClick="webSocketConnect($('#searchText').val());"></p>
    </div>
</div>

<ul class="nav nav-tabs" role="tablist" id="typetabs">
    <li class="active">
        <a href="#tab-order" data-toggle="tab" aria-controls="order">Orders</a>
    </li>
    <li>
        <a href="#tab-servicecalls" data-toggle="tab" aria-controls="servicecalls">Service Calls</a>
    </li>
    <li>
        <a href="#tab-promos" data-toggle="tab" aria-controls="promo">Promotions</a>
    </li>
</ul>
<div class="tab-content" id="content">
</div>
</body>

<script type="text/javascript">

    // The field within the JSON to show
    var dataTable = null;

    function onDocumentReady() {
        $('a[data-toggle="tab"]').on( 'shown.bs.tab', function (e) {
            console.log(e);
            $.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();
        });

        dataTable = $('#results').DataTable({
            data: [],
            columns: [
                { "data": "order_item_id" },
                { "data": "product_id" },
                { "data": "product_name" },
                { "data": "customer_id" },
                { "data": "store_id" },
                { "data": "item_shipment_status_code" },
                { "data": "order_datetime" },
                { "data": "ship_datetime" },
                { "data": "item_return_datetime" },
                { "data": "item_refund_datetime" },
                { "data": "product_category_id" },
                { "data": "product_category_name" },
                { "data": "payment_method_code" },
                { "data": "tax_amount" },
                { "data": "item_quantity" },
                { "data": "item_price" },
                { "data": "discount_amount" },
                { "data": "coupon_code" },
                { "data": "coupon_amount" },
                { "data": "ship_address_line1" },
                { "data": "ship_address_line2" },
                { "data": "ship_address_line3" },
                { "data": "ship_address_city" },
                { "data": "ship_address_state" },
                { "data": "ship_address_postal_code" },
                { "data": "ship_address_country" },
                { "data": "ship_phone_number" },
                { "data": "ship_customer_name" },
                { "data": "ship_customer_email_address" },
                { "data": "ordering_session_id" },
                { "data": "website_url" }
            ]
        });

        // handle the case where the use hits the enter button
        $('#searchText').keypress(function(event) {
            if (event.keyCode == 13 || event.which == 13) {
                event.preventDefault();

                // connect to the websocket to receive updates for this order
                webSocketConnect($('#searchText').val());
            }
        });


    }

    if ( !window.isLoaded ) {
        window.addEventListener("load", function() {
            onDocumentReady();
        }, false);
    } else {
        onDocumentReady();
    }


</script>

<script type="text/javascript" src="js/websocketListener.js" ></script>


</html>