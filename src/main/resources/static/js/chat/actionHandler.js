// сообщаем серверу, что пользователь закрыл вкладку или вышел из аккаунта
window.addEventListener('beforeunload', () => {
    stompClient.send("/app/unregister", {}, getCurrentUser());
});

// функции срабатывающие при открытии
document.addEventListener('DOMContentLoaded', function() {
    connect();

    const currentChatUser = localStorage.getItem('currentChatUser');
    if (currentChatUser !== null) {
        localStorage.removeItem('currentChatUser');
        const selectedUsername = document.getElementById('selectedUsername');
        const userId = selectedUsername.getAttribute('data-user-id');
        const userImage = '/uploads/' + selectedUsername.getAttribute('data-userImage');
        const userName = selectedUsername.getAttribute('data-username');

        saveChatState(userId, userName, userImage);
        openChat(userName, userImage, userId);
    } else {
        // загрузка состояния чата
        const savedState = loadChatState();
        if (savedState) {
            restoreChat(savedState.userName, savedState.userImage, savedState.userId);
        }
    }

    // обработчики для поиска
    const searchInput = document.getElementById('searchInput')
    const searchButton = document.getElementById('searchButton');
    const cancelSearchButton = document.getElementById('cancelSearchButton');
    const usersList = document.getElementById('usersList');

    // обрабатывает клик по пользователю для вывода диалога
    usersList.addEventListener('click', function(e) {
        const listItem = e.target.closest('.listItem');
        if (listItem) {
            const userId = listItem.getAttribute('data-user-id');
            const userName = listItem.querySelector('.userName').textContent;
            const userImage = listItem.querySelector('.userImage img').src;

            saveChatState(userId, userName, userImage);
            openChat(userName, userImage, userId);
        }
    });

    // поиск пользователей по кнопке
    searchButton.addEventListener('click', function () {
        resetUserList();
        performSearch();
    });

    // поиск пользователей по Enter
    searchInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            resetUserList();
            performSearch();
        }
    });

    // отмена поиска
    cancelSearchButton.addEventListener('click', function() {
        const searchInput = document.getElementById('searchInput');
        searchInput.value = '';
        this.style.display = 'none';

        const noResults = document.querySelector('.no-results');
        if (noResults) {
            noResults.remove();
        }
        resetUserList();
    });
    resetUserList();
});


// обработка кнопки закрытия чата
document.addEventListener('click', function(e) {
    if (e.target.closest('#closeChatBtn')) {
        document.getElementById('chatHeader').innerHTML = '';
        document.getElementById('chatBody').innerHTML = `<div class="noDialogMessage">Выберите пользователя, чтобы начать общение</div>`;
        document.getElementById('chatInput').innerHTML = '';
        clearChatState();
    }
});