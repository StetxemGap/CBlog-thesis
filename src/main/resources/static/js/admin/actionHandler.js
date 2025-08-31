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
    const mainViewNewPassword = document.getElementById('mainViewNewPassword');

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

    document.addEventListener('click', function (e) {
        if (e.target.closest('.accept')) {
            switchView(mainViewNewPassword);
            const button = e.target.closest('.accept');
            const id = +button.getAttribute('request-id');

            submitNewPassword.addEventListener('click', function () {
                const inputPassword = document.getElementById('inputNewPassword').value.trim();
                if (inputPassword.length > 4) {
                    stompClient.send("/app/changePassword", {}, JSON.stringify({
                        id: id,
                        password: inputPassword
                    }))
                }
            });
        }
    });

    const submitNewPassword = document.getElementById('submitNewPassword');

    document.addEventListener('click', function (e) {
        if (e.target.closest('.cancel')) {
            const button = e.target.closest('.cancel');
            const username = button.getAttribute('data-username');
            const id = button.getAttribute('request-id');
            const viewPassword  = document.getElementById('viewPassword');
            const listItem = document.getElementById(`listItem-password-${username}-${id}`);
            viewPassword.removeChild(listItem);
            stompClient.send("/app/cancelRequest", {}, id);
        }
    });

    const createUserRequests = document.getElementById('createUserRequests');
    const deleteUserRequests = document.getElementById('deleteUserRequests');
    const createUserItems = document.getElementById('createUserItems');
    const deleteUserItems = document.getElementById('deleteUserItems');


    createUserRequests.addEventListener('click', function () {
        createUserRequests.className = 'checked';
        deleteUserRequests.className = 'unchecked';

        createUserItems.style.display = "block";
        deleteUserItems.style.display = "none";
    });

    deleteUserRequests.addEventListener('click', function () {
        createUserRequests.className = 'unchecked';
        deleteUserRequests.className = 'checked';

        createUserItems.style.display = "none";
        deleteUserItems.style.display = "block";
    });

    document.addEventListener('click', function (e) {
        if (e.target.closest('.cancelCreateRequest')) {
            const button = e.target.closest('.cancelCreateRequest');
            const id = button.getAttribute('request-id');
            const createUserItems  = document.getElementById('createUserItems');
            const listItem = document.getElementById(`listItem-request-${id}`);
            createUserItems.removeChild(listItem);
            stompClient.send("/app/cancelHRRequest", {}, id);
        }
    });

    document.addEventListener('click', function (e) {
        if (e.target.closest('.cancelDeleteRequest')) {
            const button = e.target.closest('.cancelDeleteRequest');
            const id = button.getAttribute('request-id');
            const deleteUserItems  = document.getElementById('deleteUserItems');
            const listItem = document.getElementById(`listItem-request-${id}`);
            deleteUserItems.removeChild(listItem);
            stompClient.send("/app/cancelHRRequest", {}, id);
        }
    });

    document.addEventListener('click', function (e) {
        if (e.target.closest('.acceptCreateRequest')) {
            const id = document.getElementById('requestId').getAttribute('data');
            const firstname = document.getElementById('requestFirstname').getAttribute('data');
            const lastname = document.getElementById('requestLastname').getAttribute('data');
            const position = document.getElementById('requestPosition').getAttribute('data');
            const hiringDate = document.getElementById('requestHiringDate').getAttribute('data');

            document.getElementById('inputRequestId').value = id;
            document.getElementById('inputFirstname').value = firstname;
            document.getElementById('inputLastname').value = lastname;
            document.getElementById('inputPosition').value = position;
            document.getElementById('inputHiringDate').value = hiringDate;

            switchView(mainViewCreateUser);
        }
    });

    document.addEventListener('click', function (e) {
        if (e.target.closest('.acceptDeleteRequest')) {
            const id = document.getElementById('deleteRequestId').getAttribute('data');
            const firstname = document.getElementById('deleteRequestFirstname').getAttribute('data');
            const lastname = document.getElementById('deleteRequestLastname').getAttribute('data');
            const position = document.getElementById('deleteRequestPosition').getAttribute('data');
            const hiringDate = document.getElementById('deleteRequestHiringDate').getAttribute('data');

            const deleteUserItems  = document.getElementById('deleteUserItems');
            const listItem = document.getElementById(`listItem-request-${id}`);
            deleteUserItems.removeChild(listItem);

            stompClient.send("/app/acceptDeleteRequest", {}, JSON.stringify({
                firstname: firstname,
                lastname: lastname,
                position: position,
                hiringDate: hiringDate
            }));
            stompClient.send("/app/cancelHRRequest", {}, id);
        }
    });
});

function getUserInfo() {

    const inputRequestId = document.getElementById('inputRequestId').value.trim();
    const inputUsername = document.getElementById('inputUsername').value.trim();
    const inputPassword = document.getElementById('inputPassword').value.trim();
    const inputEmail = document.getElementById('inputEmail').value.trim();
    const inputFirstname = document.getElementById('inputFirstname').value.trim();
    const inputLastname = document.getElementById('inputLastname').value.trim();
    const inputPosition = document.getElementById('inputPosition').value.trim();
    const inputHiringDate = document.getElementById('inputHiringDate').value.trim();
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
        inputPosition && inputHiringDate) {
        inputElement.setCustomValidity('');
        inputElement.reportValidity();
        console.log(inputFirstname);
        stompClient.send("/app/addNewUser", {},
            JSON.stringify({
                username: inputUsername,
                password: inputPassword,
                email: inputEmail,
                firstname: inputFirstname,
                lastname: inputLastname,
                position: inputPosition,
                hiringDate: inputHiringDate,
                admin: inputAdminRole
            }));
        if(inputRequestId) {
            stompClient.send("/app/cancelHRRequest", {}, inputRequestId);
            document.getElementById('inputRequestId').value = '';
            const createUserItems  = document.getElementById('createUserItems');
            const listItem = document.getElementById(`listItem-request-${inputRequestId}`);
            createUserItems.removeChild(listItem);
        }
        document.getElementById('inputUsername').value = '';
        document.getElementById('inputPassword').value = '';
        document.getElementById('inputEmail').value = '';
        document.getElementById('inputFirstname').value = '';
        document.getElementById('inputLastname').value = '';
        document.getElementById('inputPosition').value = '';
        document.getElementById('inputHiringDate').value = '';
        switchView(document.getElementById('mainViewUsers'));
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
