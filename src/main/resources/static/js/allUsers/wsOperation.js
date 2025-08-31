// делаем websocket соединение
let stompClient = null;
let isConnected = false;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({},  () => {
        isConnected = true;

        // подписка для получения статусов онлайн/оффлайн
        stompClient.subscribe('/user/queue/statuses', function(message) {
            const statuses = JSON.parse(message.body);
            Object.entries(statuses).forEach(([username, isOnline]) => {
                updateUserStatus(username, isOnline);
            });
        });

        const currentUser = getCurrentUser();
        stompClient.send("/app/register", {}, currentUser);
        stompClient.send("/app/requestStatuses", {}, currentUser);

    });

    // обновление статуса
    function updateUserStatus(username, isOnline) {
        // статус списка пользователей
        const userElements = document.querySelectorAll(`.listItem[data-user-id="${username}"] .userImage`);
        userElements.forEach(element => {
            element.classList.remove('online', 'offline');
            element.classList.add(isOnline ? 'online' : 'offline');
        });

    }
}