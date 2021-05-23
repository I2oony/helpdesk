checkAuth();

let body = document.querySelector('body');
let authForm = document.getElementById('login-form');

authForm.onsubmit = function() {
    let url = "https://i2oony.com/api/users/auth";

    var username = document.getElementById('login-field-username').value;
    var password = document.getElementById('login-field-password').value;

    axios.get(url, {
        withCredentials: true,
        auth: {
            username: username,
            password: password
        }
        })
    .then(function (response) {
        var cookie = response.data;
        document.cookie = "token=" + cookie + "; secure";
        window.location.pathname = "/web/dashboard"
    })
    .catch(function (error) {
        if (document.getElementById("id-error-message") == null) {
            let span = document.createElement('span');
            span.className = "error-message font-subtitle-1";
            span.id = "id-error-message";
            span.innerHTML = "Incorrect login or password!";
            authForm.prepend(span);
        }
    })
};

function checkAuth() {
    let url = "https://i2oony.com/api/users";

    axios.get(url)
        .then(function (response) {
            window.location.pathname = "/web/dashboard";
        })
        .catch(function (error) {
            console.log();
        })
}