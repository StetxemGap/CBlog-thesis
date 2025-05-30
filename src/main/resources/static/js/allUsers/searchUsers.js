// получение текущего пользователя
function getCurrentUser() {
    const element = document.getElementById('currentUser');
    return element ? element.getAttribute('data-username') : null;
}

// поиск пользователя
function performSearch() {
    const searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();
    const cancelButton = document.getElementById('cancelSearchButton');
    const users = document.querySelectorAll('.listItem');
    let hasResults = false;

    document.querySelectorAll('.no-results').forEach(el => el.remove());

    const visibleUsers = Array.from(document.querySelectorAll('.listItem'))
        .filter(user => user.style.display !== 'none');

    const usersList = document.getElementById('usersList');
    usersList.innerHTML = '';
    visibleUsers.forEach(user => usersList.appendChild(user));

    if (!searchTerm) {
        resetUserList();
        cancelButton.style.display = 'none';
        return;
    }

    users.forEach(user => {
        const userName = user.querySelector('.userName').textContent.toLowerCase();
        const searchText = user.getAttribute('data-search-text').toLowerCase();

        if (userName.includes(searchTerm) || searchText.includes(searchTerm)) {
            user.style.display = 'grid';
            hasResults = true;
        } else {
            user.style.display = 'none';
        }
    });

    if (!hasResults) {
        const noResults = document.createElement('div');
        noResults.className = 'no-results';
        noResults.id = 'tmpText';
        noResults.textContent = 'Пользователи не найдены';
        document.getElementById('usersList').appendChild(noResults);
    }

    cancelButton.style.display = 'flex';
}

// сброс поиска пользователя
function resetUserList() {
    const usersList = document.getElementById('usersList');
    const users = document.querySelectorAll('.listItem');

    usersList.innerHTML = '';
    users.forEach(user => {
        usersList.appendChild(user);
        user.style.display = 'grid';
    });
}