var stompClient = null;
var orderOfInterest = -1;

function webSocketConnect(orderId) {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    //console.log("webSocketConnect called with orderId " + orderId);

    orderOfInterest = orderId;

    stompClient.connect({}, function(frame) {
        // setConnected(true);
        //console.log('Connected: ' + frame);

        console.log("listening for updates about orderId " + orderOfInterest);

        // disable debug messages
        stompClient.debug = null;

        stompClient.subscribe('/orders/' + orderOfInterest, function(payload) {

            //console.log(payload.body);

            $("<p id=\"orderDetail\">" + JSON.stringify(JSON.parse(payload.body), null, 4) + "</p>").insertAfter($('#content'));

        });

    });
}