var stompClient = null;

function webSocketConnect(orderId) {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        // setConnected(true);
        console.log('Connected: ' + frame);

        // disable debug messages
        stompClient.debug = null;

        stompClient.subscribe('/orders/' + orderId, function(payload) {
            //console.log(payload.body);
            dataTable.row.add(JSON.parse(payload.body));
            dataTable.draw();
            //$('#results').visibility = "visibile";
            // powerGauge.update(parseFloat(payload.body));
        });

    });
}