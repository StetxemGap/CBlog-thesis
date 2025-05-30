// получение текущего пользователя
function getCurrentUser() {
    const element = document.getElementById('currentUser');
    return element ? element.getAttribute('data-username') : null;
}

// получение выбранных фильтров
function getSelectedFilters() {
    return {
        position: document.querySelector('.filterPosition').value,
        gender: document.querySelector('.filterGender').value,
        city: document.querySelector('.filterCity').value,
        isOnline: document.getElementById('isOnline').checked
    };
}

// проверка соответствия пользователя фильтрам
function matchesFilters(user, filters) {
    console.log(user.getAttribute('data-position') + ' ' + filters.position);
    // Проверка фильтра должности
    if (filters.position !== 'Не выбрано' &&
        user.getAttribute('data-position') !== filters.position) {
        return false;
    }

    // Проверка фильтра пола
    if (filters.gender !== 'Не выбрано' &&
        user.getAttribute('data-gender') !== filters.gender) {
        return false;
    }

    // Проверка фильтра города
    if (filters.city !== 'Не выбрано' &&
        user.getAttribute('data-city') !== filters.city) {
        return false;
    }

    // Проверка фильтра онлайн статуса
    if (filters.isOnline) {
        const userImage = user.querySelector('.userImage');
        if (!userImage.classList.contains('online')) {
            return false;
        }
    }

    return true;
}

// поиск пользователя
function performSearch() {
    const searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();
    const cancelButton = document.getElementById('cancelSearchButton');
    const users = document.querySelectorAll('.listItem');
    const filters = getSelectedFilters();
    let hasResults = false;

    document.querySelectorAll('.no-results').forEach(el => el.remove());

    const visibleUsers = Array.from(document.querySelectorAll('.listItem'))
        .filter(user => user.style.display !== 'none');

    const usersList = document.getElementById('usersList');
    usersList.innerHTML = '';
    visibleUsers.forEach(user => usersList.appendChild(user));

    if (!searchTerm && Object.values(filters).every(val => val === false || val === 'Не выбрано')) {
        resetUserList();
        cancelButton.style.display = 'none';
        return;
    }

    users.forEach(user => {
        const userName = user.querySelector('.userName').textContent.toLowerCase();
        const searchText = user.getAttribute('data-search-text').toLowerCase();

        const matchesSearch = !searchTerm ||
            userName.includes(searchTerm) ||
            searchText.includes(searchTerm);
        const matchesFilter = matchesFilters(user, filters);

        if (matchesSearch && matchesFilter) {
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

// сброс поиска и фильтров
function resetFilters() {
    document.querySelector('.filterPosition').value = 'Не выбрано';
    document.querySelector('.filterGender').value = 'Не выбрано';
    document.querySelector('.filterCity').value = 'Не выбрано';
    document.getElementById('isOnline').checked = false;
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