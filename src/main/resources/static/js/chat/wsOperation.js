// делаем websocket соединение
let stompClient = null;
let isConnected = false;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({},  () => {
        isConnected = true;

        stompClient.send("/app/requestAllLastMessages", {}, getCurrentUser());

        stompClient.subscribe("/user/queue/messageIsRead", function (message) {
           const msg = JSON.parse(message.body);
           updateMessageStatus(msg);
        });

        stompClient.subscribe("/user/queue/allMessagesIsRead", function (message) {
            const msgIds = JSON.parse(message.body);
            msgIds.forEach(id => {
                updateMessageStatus(id);
            });
        });

        stompClient.subscribe('/user/queue/allLastMessages', function(message) {
            const lastMessages = JSON.parse(message.body);

            Object.entries(lastMessages).forEach(([userId, messageData]) => {
                updateLastMessageInList(userId, messageData);

                const lastMessagesStorage = JSON.parse(localStorage.getItem('lastMessages') || '{}');
                lastMessagesStorage[userId] = messageData.timestamp;
                localStorage.setItem('lastMessages', JSON.stringify(lastMessagesStorage));
            });

            resetUserList();
        });

        // подписка для получения истории сообщений
        stompClient.subscribe('/user/queue/messages', function(message) {
            const msg = JSON.parse(message.body);

            const sender = msg[0].sender;
            const recipient = msg[0].recipient;
            const opponent = document.getElementById('opponent').classList.toString();
            const otherUser = sender === currentUser ? recipient : sender;

            console.log( recipient + ' === ' + currentUser);
            console.log( 'opponentName ' + otherUser + ' === ' + opponent);

            const lastMessages = JSON.parse(localStorage.getItem('lastMessages') || '{}');
            lastMessages[msg[0].sender] = new Date().getTime();
            localStorage.setItem('lastMessages', JSON.stringify(lastMessages));

            if ('opponentName ' + otherUser === opponent) {
                displayMessages(msg);
            }
            resetUserList();
        });

        stompClient.subscribe('/user/queue/newDialog', function (message) {
            const msg = JSON.parse(message.body);
            const userName = msg.firstName + ' ' + msg.lastName;
            const userImage = '/uploads/' + msg.photoPath;
            console.log(msg);
            console.log(msg.username + ' ' + userName + ' ' + userImage)
            createNewDialog(msg.username, userName, userImage);
            stompClient.send("/app/requestStatuses", {}, getCurrentUser());
        });

        // подписка для получения сообщений в режиме реального времени
        stompClient.subscribe('/user/queue/newMessages', function(message) {
            let msg = JSON.parse(message.body);
            const recipient = msg.recipient;

            const sender = msg.sender;
            const currentUser = getCurrentUser();
            const opponent = document.getElementById('opponent').classList.toString();

            const lastMessages = JSON.parse(localStorage.getItem('lastMessages') || '{}');
            lastMessages[msg.sender] = new Date().getTime();
            localStorage.setItem('lastMessages', JSON.stringify(lastMessages));

            const otherUser = sender === currentUser ? recipient : sender;
            updateLastMessageInList(otherUser, msg);

            if ((recipient === currentUser && 'opponentName ' + sender === opponent) || sender === currentUser) {
                if (sender !== currentUser) {
                    msg.isRead = true;
                    stompClient.send("/app/msgStatus", {}, JSON.stringify({
                        recipient: sender,
                        msgId: msg.id,
                        msgStatus: msg.isRead
                    }));
                }
                displayMessages(msg);
            } else {
                const opponentElement = document.getElementById(`listItem-${sender}`);
                opponentElement.className = 'listItem newMessage';
            }
            resetUserList();
        });

        // подписка для получения статусов онлайн/оффлайн
        stompClient.subscribe('/user/queue/statuses', function(message) {
            const statuses = JSON.parse(message.body);
            Object.entries(statuses).forEach(([username, isOnline]) => {
                updateUserStatus(username, isOnline);
            });
        });

        // подписка для вывода статуса "печатает..."
        stompClient.subscribe(`/user/queue/typing`, function(message) {
            const notification = JSON.parse(message.body);
            const currentChat = loadChatState();

            if (!currentChat || notification.sender !== currentChat.userId) return;


            const typingIndicator = document.querySelector('#notification .typing-indicator');
            if (typingIndicator) {
                typingIndicator.style.display = notification.isTyping ? 'block' : 'none';
            }
        });

        const savedChat = loadChatState();
        if (savedChat) {
            loadChatMessages(savedChat.userId);
        }
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

        // статус заголовка чата
        const savedChat = loadChatState();
        if (savedChat && savedChat.userId === username) {
            const chatHeaderImage = document.querySelector('#chatHeader .userImage');
            if (chatHeaderImage) {
                chatHeaderImage.classList.remove('online', 'offline');
                chatHeaderImage.classList.add(isOnline ? 'online' : 'offline');
            }
        }
    }

}