function getSelectedFilters() {
    return {
        haveActivation: document.getElementById('haveActivation').checked
    };
}

function matchesFilters(user, filters) {
    if (filters.haveActivation) {
        return user.getAttribute('data-activation') === 'true';
    }
    return true;
}

function performSearch() {
    const searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();
    const cancelButton = document.getElementById('cancelSearchButton');
    const users = document.querySelectorAll('.listItem');
    const filters = getSelectedFilters();
    let hasResults = false;

    document.querySelectorAll('.no-results').forEach(el => el.remove());

    if (!searchTerm && !filters.haveActivation) {
        resetUserList();
        cancelButton.style.display = 'none';
        return;
    }

    users.forEach(user => {
        const userName = user.querySelector('.userName').textContent.toLowerCase();
        const fullName = user.querySelector('.name').textContent.toLowerCase();
        const searchText = user.getAttribute('data-search-text').toLowerCase();

        const matchesSearch = !searchTerm ||
            userName.includes(searchTerm) ||
            fullName.includes(searchTerm) ||
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
        noResults.textContent = 'Пользователи не найдены';
        document.querySelector('.usersList').appendChild(noResults);
    }

    cancelButton.style.display = 'flex';
}

function resetUserList() {
    const users = document.querySelectorAll('.listItem');
    users.forEach(user => {
        user.style.display = 'grid';
    });
}