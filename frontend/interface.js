let body = document.querySelector('body');
let changeThemeButton = document.getElementById('change-theme');

changeThemeButton.onclick = function() {
    body.classList.toggle('light-theme');
    body.classList.toggle('dark-theme');
}

let authForm = document.getElementById('login-form');

authForm.onsubmit = function() {
    let url = "https://i2oony.com/api/users/auth";

    let username = document.getElementById('login-field-username').value;
    let password = document.getElementById('login-field-password').value;
    
    let request = new XMLHttpRequest();
    request.open("GET", url, false, username, password);
    request.send();

    request.onload = function() {
        console.log(request.response);
    }
};
