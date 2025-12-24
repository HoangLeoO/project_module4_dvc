var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Subscribe to public topic
        stompClient.subscribe('/topic/messages', function (message) {
            console.log("Public Message: " + message.body);
            // showNotification(JSON.parse(message.body).content);
        });

        // Subscribe to user specific queue
        stompClient.subscribe('/user/queue/notifications', function (notification) {
            console.log("Private Notification: " + notification.body);
            // handleNotification(JSON.parse(notification.body));
        });
    }, function (error) {
        console.log('STOMP error ' + error);
        // Reconnect logic here if needed
        setTimeout(connect, 5000);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

/*
function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}
*/

// Auto connect when page loads
$(document).ready(function () {
    connect();
});
