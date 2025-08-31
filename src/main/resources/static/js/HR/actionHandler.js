function getCurrentUser() {
    const element = document.getElementById('currentUser');
    return element ? element.getAttribute('data-username') : null;
}

document.addEventListener('DOMContentLoaded', function() {
    connect();

    const addNewUserRequest = document.getElementById('addNewUserRequest');
    const deleteUserRequest = document.getElementById('deleteUserRequest');

    addNewUserRequest.addEventListener('click', function () {
        document.getElementById('menu').style.display="none";
        document.getElementById('createUser').style.display = "flex";
    });

    deleteUserRequest.addEventListener('click', function () {
        document.getElementById('menu').style.display="none";
        document.getElementById('deleteUser').style.display = "flex";
    });

    const backToMenuFromCreate = document.getElementById('returnBackCreate');
    const backToMenuFromDelete = document.getElementById('returnBackDelete');

    backToMenuFromCreate.addEventListener('click', function () {
        document.getElementById('createUser').style.display = "none";
        document.getElementById('menu').style.display="flex";
    });

    backToMenuFromDelete.addEventListener('click', function () {
        document.getElementById('deleteUser').style.display = "none";
        document.getElementById('menu').style.display="flex";
    });

    const submitNewUser = document.getElementById('submitNewUser');
    const submitDeleteUser = document.getElementById('submitDeleteUser');

    submitNewUser.addEventListener('click', function () {
        const firstName = document.getElementById('inputFirstname').value.trim();
        const lastName = document.getElementById('inputLastname').value.trim();
        const position = document.getElementById('inputPosition').value.trim();
        const hiringDate = document.getElementById('inputHiringDate').value.trim();

        if (firstName && lastName && position && hiringDate) {
            stompClient.send("/app/createNewUserRequest", {}, JSON.stringify({
                firstName: firstName,
                lastName: lastName,
                position: position,
                hiringDate: hiringDate
            }));
        }
    });

    submitDeleteUser.addEventListener('click', function () {
        const firstName = document.getElementById('inputFirstnameDelete').value.trim();
        const lastName = document.getElementById('inputLastnameDelete').value.trim();
        const position = document.getElementById('inputPositionDelete').value.trim();
        const hiringDate = document.getElementById('inputHiringDateDelete').value.trim();

        if (firstName && lastName && position && hiringDate) {
            stompClient.send("/app/deleteUserRequest", {}, JSON.stringify({
                firstName: firstName,
                lastName: lastName,
                position: position,
                hiringDate: hiringDate
            }));
        }
    });
});
