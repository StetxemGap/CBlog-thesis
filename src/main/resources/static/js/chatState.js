// загрузка чата если на момент перезагрузки/закрытия вкладки он был открыт
function restoreChat(userName, userImage, userId) {
    openChat(userName, userImage, userId);
}

// сохраняем открытый чат (или отсутствие открытых чатов)
function saveChatState(userId, userName, userImage) {
    localStorage.setItem('currentChat', JSON.stringify({
        userId: userId,
        userName: userName,
        userImage: userImage,
        timestamp: new Date().getTime()
    }));

    // время последнего сообщения
    const lastMessages = JSON.parse(localStorage.getItem('lastMessages') || '{}');
    lastMessages[userId] = new Date().getTime();
    localStorage.setItem('lastMessages', JSON.stringify(lastMessages));
}

// загрузка чата
function loadChatState() {
    const savedChat = localStorage.getItem('currentChat');
    if (savedChat) {
        return JSON.parse(savedChat);
    }
    return null;
}

// убираем состояние
function clearChatState() {
    localStorage.removeItem('currentChat');
}