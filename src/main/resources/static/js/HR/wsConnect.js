// делаем websocket соединение
let stompClient = null;
let isConnected = false;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () =>{
       isConnected = true;

       stompClient.subscribe("/user/queue/actionRequest", function (message) {
          console.log(message.body);
          if (message.body === "Запрос успешно создан") {
              alert(message.body);
              document.getElementById('createUser').style.display = "none";
              document.getElementById('deleteUser').style.display = "none";
              document.getElementById('menu').style.display = "flex";
          } else {
              alert(message.body);
          }
       });
    });
}