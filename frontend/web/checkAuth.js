var config = {};
config.baseUrl = "https://i2oony.com"

var user;

const getUser = async () => {
    config.method = "get";
    config.url = "/api/users"
    await axios(config)
        .then(function (response) {
            user = response.data;

            let body = document.querySelector('body');
            let script = document.createElement('script');
            script.src = "../interface.js";
            body.appendChild(script);
        })
        .catch(function (error) {
            console.log(error)
            window.location.pathname = "/";
        })
}

getUser();