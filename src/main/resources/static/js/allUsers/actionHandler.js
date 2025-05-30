// сообщаем серверу, что пользователь закрыл вкладку или вышел из аккаунта
window.addEventListener('beforeunload', () => {
    stompClient.send("/app/unregister", {}, getCurrentUser());
});

// функции срабатывающие при открытии
document.addEventListener('DOMContentLoaded', function() {
    connect();

    // обработчики для поиска
    const searchInput = document.getElementById('searchInput')
    const searchButton = document.getElementById('searchButton');
    const cancelSearchButton = document.getElementById('cancelSearchButton');
    const openFilter = document.getElementById('openFilter');
    const closeFilter = document.getElementById('closeFilter');

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

    openFilter.addEventListener('click', function () {
        const filterMenu = document.getElementById('filterMenu');
        filterMenu.style.display='grid';
        this.style.display = 'none';
        closeFilter.style.display = 'block';
    });

    closeFilter.addEventListener('click', function () {
        const filterMenu = document.getElementById('filterMenu');
        filterMenu.style.display='none';
        this.style.display = 'none';
        openFilter.style.display = 'block';
    })
});