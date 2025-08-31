// делаем websocket соединение
let stompClient = null;
let isConnected = false;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
}