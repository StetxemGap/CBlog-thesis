// таймер для 'печатает...'
let typingTimer;
const TYPING_DELAY = 1000;

// получение текущего пользователя
function getCurrentUser() {
    const element = document.getElementById('currentUser');
    return element ? element.getAttribute('data-username') : null;
}

// открытие чата
function openChat(userName, userImage, userId) {

    // const lastMessages = JSON.parse(localStorage.getItem('lastMessages') || '{}');
    // lastMessages[userId] = new Date().getTime();
    // localStorage.setItem('lastMessages', JSON.stringify(lastMessages));

    const notificationDiv = document.getElementById('notification');
    notificationDiv.innerHTML = '';

    const typingIndicator = document.createElement('div');
    typingIndicator.className = 'typing-indicator';
    typingIndicator.style.display = 'none';
    typingIndicator.textContent = 'печатает...';
    notificationDiv.appendChild(typingIndicator);

    // смотрим новый ли пользователь добавляется в чат или нет
    const listItems = document.querySelectorAll('.listItem');
    const userIds = [];
    listItems.forEach(item => {
        userIds.push(item.dataset.userId);
    });
    if (userIds.includes(userId)) {
        console.log('Пользователь найден');
    } else {
        createNewDialog(userId, userName, userImage);
    }

    const userImageElement = document.querySelector(`.listItem[data-user-id="${userId}"] .userImage`);
    let userStatus = "userImage";
    const classes = userImageElement.className.split(' ');
    if (classes.length > 1) {
        userStatus ="userImage " + classes[1];
    }

    const chatHeader = document.getElementById('chatHeader');
    chatHeader.innerHTML = `
        <p class="${userStatus}"><img src="${userImage}"></p>
        <p class="opponentName ${userId}" id="opponent">${userName}</p>
        <button type="button" id="closeChatBtn">
            <img src="/img/close.png" height="25px" width="25px">
        </button>
    `;
    const chatInput = document.getElementById('chatInput');
    chatInput.innerHTML = `
                        <button class="docButton" type="sibmit" id="docButton"><img src="/img/attach.png" height="28px" width="28px"></button>
                        <input type="file" id="fileInput" style="display: none;">
                        <textarea class="auto-resize-textarea" id="message" rows="1"></textarea>
                        <button class="sendButton" onclick="handleMessage()"><img src="/img/send.png" height="28px" width="28px"></button>
                `;

    const textarea = document.getElementById('message');
    // отправка сообщения по ентеру
    textarea.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.altKey && !e.shiftKey && !e.ctrlKey) {
            e.preventDefault();
            handleMessage();
        }
    });
    // отслеживаем вводится ли текст для 'печатает...'
    textarea.addEventListener('input', function() {
        clearTimeout(typingTimer);

        const isTyping = this.value.length >= 0;
        sendTypingNotification(true, userId);

        if (isTyping) {
            typingTimer = setTimeout(() => {
                sendTypingNotification(false, userId);
            }, TYPING_DELAY);
        }
    });
    // обработка кнопки для загрузки файлов
    document.getElementById('docButton').addEventListener('click', function() {
        document.getElementById('fileInput').click();
    });
    // само скрытое поля для их загрузки
    document.getElementById('fileInput').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            uploadFile(file);
        }
    });

    // загрузка сообщений
    loadChatMessages(userId);
}

function sendTypingNotification(isTyping, userId) {
    stompClient.send("/app/typing", {},
        JSON.stringify({
            recipient: userId,
            isTyping: isTyping
        })
    );
}

function createNewDialog(userId, userName, userImage) {
    const chatBody = document.getElementById('chatBody');
    chatBody.innerHTML = ` `;

    const usersList = document.getElementById('usersList');
    const newListItem = document.createElement('div');
    newListItem.className = 'listItem newMessage';
    newListItem.id = `listItem ${userId}`;
    console.log("userImage " + userImage);
    if (userImage === "/uploads/null") {
        userImage = "/img/user.png";
        console.log("userImage " + userImage);
    }
    console.log(userImage);
    newListItem.setAttribute('data-user-id', `${userId}`);
    newListItem.setAttribute('data-user-name', `${userName}`);
    newListItem.setAttribute('data-search-text', `${userId} ${userName}`);
    newListItem.innerHTML = `
                         <p class="userImage">
                            <img src='${userImage}'>
                        </p>
                        <p class="userName">${userName}</p>
                        <p class="lastMessage"></p>
        `;
    usersList.appendChild(newListItem);
}

// закидываем сообщения серверу
function handleMessage() {
    const textarea = document.getElementById('message');
    const content = textarea.value.trim();

    const lastMessages = JSON.parse(localStorage.getItem('lastMessages') || '{}');
    lastMessages[getCurrentUser()] = new Date().getTime();
    localStorage.setItem('lastMessages', JSON.stringify(lastMessages));

    //получение id получателя
    const savedChat = JSON.parse(localStorage.getItem('currentChat'));
    const recipientUS = savedChat.userId;

    stompClient.send("/app/handleMessage", {},
        JSON.stringify({
            content: content,
            recipient: recipientUS
        })
    );

    textarea.value = ' ';
}

// отправка файлов
function uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    const savedChat = loadChatState();
    if (!savedChat) return;

    formData.append('recipient', savedChat.userId);

    const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

    fetch('/upload', {
        method: 'POST',
        body: formData,
        headers: {
            'X-XSRF-TOKEN': csrfToken
        },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) throw new Error('Upload failed');
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Сообщение уже сохранено и отправлено сервером
                console.log('File uploaded and message saved', data.message);
            }
        })
        .catch(error => {
            console.error('Upload error:', error);
            alert('Ошибка загрузки файла');
        });
}

// запрос у сервера сообщений между двумя пользователями
function loadChatMessages(recipientId) {
    stompClient.send("/app/allMessagesRead", {}, JSON.stringify({
        sender: getCurrentUser(),
        opponent: recipientId
    }));
    stompClient.send("/app/requestMessages", {},
        JSON.stringify({
            otherUser: recipientId
        })
    );

    stompClient.subscribe('/topic/messages', function (message) {
        const messages = JSON.parse(message.body);
        displayMessages(messages);
    });
}

// красивые сообщеня
function displayMessages(messages) {
    const chatBody = document.getElementById('chatBody');

    if (Array.isArray(messages)) {
        chatBody.innerHTML = '';

        messages.forEach(msg => displaySingleMessage(msg));
    }
    else {
        displaySingleMessage(messages);
    }
}

function displaySingleMessage(msg) {
    const chatBody = document.getElementById('chatBody');
    const currentUser = getCurrentUser();

    const otherUser = msg.sender === currentUser ? msg.recipient : msg.sender;
    updateLastMessageInList(otherUser, msg);

    const parentDiv = document.createElement('div')
    const messageDiv = document.createElement('div');
    const messageMenu = document.createElement('div');

    parentDiv.className = `parentMessages ${msg.id}`;
    messageDiv.className = msg.sender === currentUser ? 'messages sender' : 'messages recipient';
    if (currentUser === msg.sender) {
        parentDiv.id=`parentMessages${msg.id}`;
        messageDiv.id=`message${msg.id}`;
        messageDiv.className = msg.isRead ? 'messages sender' : 'messages sender unchecked';
        messageMenu.className ='messageMenu sender';

        messageMenu.innerHTML = `
              <div class="item change ${msg.id}" id="change"><img src="/img/change.png"></div>
              <div class="item delete" id="delete"><img src="/img/delete.png"></div>
        `;
        parentDiv.appendChild(messageMenu);
    }
    messageDiv.innerHTML = `
             <p class="messagesText">${msg.content}</p>
             <p class="messagesTime">${formatTime(msg.timestamp)}</p>
        `;

    parentDiv.appendChild(messageDiv);
    chatBody.appendChild(parentDiv);
    chatBody.scrollTop = chatBody.scrollHeight;


    // обрабатываем действия с сообщениями
    parentDiv.addEventListener('click', function(e) {
        if (e.target.closest('.delete')) {
            console.log("Message id:", msg.id);
            stompClient.send("/app/deleteMessage", {}, msg.id);
            const parentDiv = document.getElementById(`parentMessages${msg.id}`);
            chatBody.removeChild(parentDiv);
        }
    });

    parentDiv.addEventListener('click', function(e) {
        if (e.target.closest('.change')) {
            console.log("Message id:", msg.id);

            const changeMessage = document.getElementById(`message${msg.id}`);
            const messageText = changeMessage.querySelector('.messagesText').textContent;
            changeMessage.innerHTML = `
                <textarea class="auto-resize-textarea" id="newMessage" rows="1">${messageText}</textarea>
                `;
            const newMessage = document.getElementById('newMessage');
            newMessage.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' && !e.altKey && !e.shiftKey && !e.ctrlKey) {
                    e.preventDefault();
                    const content = newMessage.value.trim();
                    if (content !== msg.content) {
                        stompClient.send("/app/updateMessage", {},
                            JSON.stringify({
                                id: msg.id,
                                content: content
                            }));
                        changeMessage.innerHTML = `
                            <p class="messagesText">${content}</p>
                             <p class="messagesTime">${formatTime(msg.timestamp)}</p>
                        `;
                    } else {
                        changeMessage.innerHTML = `
                            <p class="messagesText">${msg.content}</p>
                             <p class="messagesTime">${formatTime(msg.timestamp)}</p>
                        `;
                    }
                }
            });
        }
    });
}

function updateLastMessageInList(userId, message) {
    const userElement = document.querySelector(`.listItem[data-user-id="${userId}"]`);
    const sender = message.sender;
    const currentUser = getCurrentUser();

    if (userElement) {
        const lastMessageElement = userElement.querySelector('.lastMessage');
        if (lastMessageElement) {
            if (sender === currentUser) {
                lastMessageElement.textContent = message.content.length > 20 ? 'Вы: ' + message.content.substring(0, 20) + '...' : 'Вы: ' + message.content;
            } else {
                lastMessageElement.textContent = message.content.length > 20 ? message.content.substring(0, 20) + '...' : message.content;
            }
        }
    }
}

function updateMessageStatus(id) {
    const msg = document.getElementById(`message${id}`);
    msg.className = 'messages sender';
}

// форматированное время для сообщений
function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
}