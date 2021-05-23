let url = "https://i2oony.com/api/users";
var user;

const getUser = async () => {
    await axios.get(url)
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