window.addEventListener('beforeunload', () => {
    stompClient.send("/app/unregister", {}, getCurrentUser());
});

function getCurrentUser() {
    const element = document.getElementById('currentUser');
    return element ? element.getAttribute('data-username') : null;
}

document.addEventListener('DOMContentLoaded', function() {
    connect();

    const usersButton = document.getElementById('usersButton');
    const hrButton = document.getElementById('hrButton');
    const passwordButton = document.getElementById('passwordButton');
    const addUserButton = document.getElementById('addUserButton');

    const mainViewUsers =document.getElementById('mainViewUsers');
    const mainViewHR = document.getElementById('mainViewHR');
    const mainViewPassword = document.getElementById('mainViewPassword')
    const mainViewCreateUser = document.getElementById('mainViewCreateUser');

    usersButton.addEventListener('click', () => switchView(mainViewUsers));
    hrButton.addEventListener('click',() => switchView(mainViewHR));
    passwordButton.addEventListener('click',() => switchView(mainViewPassword));
    addUserButton.addEventListener('click', function () { switchView(mainViewCreateUser);
    mainViewCreateUser.querySelector('.viewHeader').textContent = "Создание пользователя"});

    const searchInput = document.getElementById('searchInput')
    const searchButton = document.getElementById('searchButton');
    const cancelSearchButton = document.getElementById('cancelSearchButton');

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

    document.getElementById('haveActivation').addEventListener('change', function() {
        performSearch();
    })

    // отмена поиска
    cancelSearchButton.addEventListener('click', function() {
        const searchInput = document.getElementById('searchInput');
        const haveActivationCheckbox = document.getElementById('haveActivation');

        searchInput.value = '';
        haveActivationCheckbox.checked = false;
        this.style.display = 'none';

        const noResults = document.querySelector('.no-results');
        if (noResults) {
            noResults.remove();
        }

        resetUserList();
    });

    const submitNewUser = document.getElementById('submitNewUser');

    submitNewUser.addEventListener('click', getUserInfo);

    document.addEventListener('click', function (e) {
        if (e.target.closest('.deleteUser')) {
            const button = e.target.closest('.deleteUser');
            const username = button.dataset.username;
            deleteUser(username);
        }
    });
});

function getUserInfo() {

    const inputUsername = document.getElementById('inputUsername').value.trim();
    const inputPassword = document.getElementById('inputPassword').value.trim();
    const inputFirstname = document.getElementById('inputFirstname').value.trim();
    const inputLastname = document.getElementById('inputLastname').value.trim();
    const inputPosition = document.getElementById('inputPosition').value.trim();
    const inputAdminRole = document.getElementById('inputAdminRole').checked;

    const inputElement = document.getElementById('inputUsername');

    inputElement.setCustomValidity('');

    const allUsernames = document.querySelector(`.listItem[user-id="${inputUsername}"]`);
    if (allUsernames) {
        console.log("Пользователь существует");
        inputElement.setCustomValidity("Пользователь уже существует!");
        inputElement.reportValidity();
        inputElement.addEventListener('input', function (){
            inputElement.setCustomValidity('');
            inputElement.reportValidity();
        });
    } else if (inputUsername && inputPassword &&
        inputFirstname && inputLastname &&
        inputPosition) {
        inputElement.setCustomValidity('');
        inputElement.reportValidity();
        console.log(inputFirstname);
        stompClient.send("/app/addNewUser", {},
            JSON.stringify({
                username: inputUsername,
                password: inputPassword,
                firstname: inputFirstname,
                lastname: inputLastname,
                position: inputPosition,
                admin: inputAdminRole
            }));
    }
}

function deleteUser(username) {
    stompClient.send("/app/deleteUser", {}, username);
    const usersList = document.getElementById('usersList');
    const listItem = document.getElementById(`listItem-${username}`);

    usersList.removeChild(listItem);
}

let currenView = document.getElementById('mainViewUsers');

function switchView(view) {
    removeCurrentView();
    currenView.style.display = 'none';
    view.style.display = 'grid';
    currenView = view;
}

function removeCurrentView() {
    if (currenView === null) {
        currenView = document.getElementById('mainViewUsers');
    }
    console.log(currenView);
    currenView.style.display = 'none';
}