buildPage(user);

function buildPage(user) {
    var role = user["role"];
    buildMenu(role);
    var path = document.location.pathname;
    var title;
    switch (path) {
        case "/web/dashboard/":
            title = "Панель";
            break;
        case "/web/profile/":
            title = "Профиль";
            buildProfilePage(user);
            break;
        case "/web/statistics/":
            title = "Статистика";
            break;
        case "/web/system/":
            title = "Настройки системы";
            break;
        default:
            title = "Ошибка!"
            break;
    }
    buildTopBar(title);
}

function buildTopBar(pageTitle) {
    let img = document.createElement('img');
    img.src = "../../icons/menu.png";
    img.className = "icons";
    img.id = "menu-button"

    let button = document.createElement('button');
    button.className = "button-icon";
    button.appendChild(img);

    button.onclick = openMenu;

    let p = document.createElement('p');
    p.className = "font-header-3";
    p.id = "page-title";
    p.textContent = pageTitle;

    let div = document.createElement('div');
    div.className="top-bar"
    div.appendChild(button);
    div.appendChild(p);

    document.body.appendChild(div);
}

function buildMenuItem(link, title) {
    var a = document.createElement('a');
    a.href = link;
    var ul = document.createElement('ul');
    ul.className = "font-header-6";
    ul.textContent = title;
    a.appendChild(ul);
    return a;
}

function buildMenu(role) {
    let menu = document.createElement('div');
    menu.className = "menu";
    menu.id = "menu-bar";
    let menuLi = document.createElement('li');
    switch (role) {
        case "admin":
            menuLi.prepend(buildMenuItem("/web/system", "Настройки системы"))
        case "operator":
            menuLi.prepend(buildMenuItem("/web/statistics", "Статистика"));
        case "client":
            menuLi.prepend(buildMenuItem("/web/profile", "Профиль"));
            menuLi.prepend(buildMenuItem("/web/dashboard", "Панель"));
            break;
        default:
            break;
    }
    let logoutUl = document.createElement('ul');
    logoutUl.className = "font-header-6";
    logoutUl.textContent = "Выход";
    logoutUl.id = "logout";
    menuLi.append(logoutUl)
    menu.appendChild(menuLi);

    logoutUl.onclick = logout;

    document.body.appendChild(menu);
}

function openMenu() {
    let menu = document.getElementById('menu-bar');
    menu.classList.toggle('menu');
    menu.classList.toggle('menu-opened');
}

function logout() {
    config.method = "delete";
    config.url = "/api/users/logout";

    axios(config)
        .then(function (response) {
            window.location.pathname = "/";
        })
        .catch(function (error) {
            console.log();
        })
}

function buildProfilePage(user) {
    var username = user["username"];
    var email = user["email"];
    var firstName = user["firstName"];
    var lastName = user["lastName"];

    let profilePage = document.createElement('div');
    profilePage.id = "profile-page";
    profilePage.className = "main-content";

    let infoBlock = buildBlock("info-block");
    infoBlock.append(buildBlockHeader("Информация о пользователе"));

    let table = document.createElement('table');
    table.id = "info-table";
    table.append(buildTableRow("font-header-6", "Логин", "info-username", username));
    table.append(buildTableRow("font-header-6", "Имя и фамилия", "info-name", firstName+" "+lastName));
    table.append(buildTableRow("font-header-6", "Электронная почта", "info-email", email));
    infoBlock.append(table);

    let changePasswordBlock = buildBlock("change-password-block");
    changePasswordBlock.append(buildBlockHeader("Смена пароля"));
    changePasswordBlock.append(buildInputField("password", "profile-field-old-password", "Старый пароль"));
    changePasswordBlock.append(buildInputField("password", "profile-field-new-password", "Новый пароль"));
    changePasswordBlock.append(buildInputField("password", "profile-field-repeat-password", "Повтор пароля"));

    let button = document.createElement('button');
    button.className = "button-text z-axis-1 font-button";
    button.textContent = "Сменить";
    button.onclick = changePass;

    changePasswordBlock.append(button);

    profilePage.append(infoBlock);
    profilePage.append(changePasswordBlock);

    document.body.append(profilePage);
}

// Profile page - info block
function buildTableRow(className, title, id, textContent) {
    var td1 = document.createElement('td');
    td1.className = className;
    td1.textContent = title;

    var td2 = document.createElement('td');
    td2.className = className;
    td2.id = id;
    td2.textContent = textContent;
    
    var tr = document.createElement('tr');
    tr.append(td1);
    tr.append(td2);

    return tr;
}

function changePass() {
    config.method = "patch";
    config.url = "/api/users/changeInfo"

    var oldPass = document.getElementById('profile-field-old-password').value;
    var newPass = document.getElementById('profile-field-new-password').value;
    var repeatPass = document.getElementById('profile-field-repeat-password').value;
    
    if (newPass.length < 8) {
        showError("Пароль менее 8 символов!");
    } else if (newPass == repeatPass) {
        var body = {
            oldPassword: oldPass,
            newPassword: newPass
        }
        config.data = JSON.stringify(body);
        axios(config)
            .then(function (response) {
                showSuccess("Пароль успешно изменён!");
            })
            .catch(function (response) {
                showError("Неверно указан старый пароль!");
            });
    } else {
        showError("Пароли несовпадают, попробуйте снова!");
    }

    function showError(text) {
        if (document.getElementById("result-message") == null) {
            var form = document.getElementById('change-password-block');
            var errorMsg = document.createElement('span');
            errorMsg.textContent = text;
            errorMsg.className = "error-message font-subtitle-1";
            errorMsg.id = "result-message";
            form.firstElementChild.after(errorMsg);
        } else {
            var errorMsg = document.getElementById("result-message");
            errorMsg.textContent = text;
        }
    }

    function showSuccess(text) {
        if (document.getElementById("result-message") == null) {
            var form = document.getElementById('change-password-block');
            var successMsg = document.createElement('span');
            successMsg.textContent = text;
            successMsg.className = "font-subtitle-1";
            successMsg.id = "result-message";
            form.firstElementChild.after(successMsg);
        } else {
            var errorMsg = document.getElementById("result-message");
            errorMsg.textContent = text;
        }
    }
}

function buildBlock(id) {
    var div = document.createElement('div');
    div.id = id;
    div.className = "surface";
    return div;
}

function buildBlockHeader(title) {
    var header = document.createElement('h3');
    header.className = "font-header-3";
    header.textContent = title;
    return header;
}

function buildInputField(type, id, placeholder) {
    var input = document.createElement('input');
    input.type = type;
    input.className = "field font-subtitle-1";
    input.id = id;
    input.placeholder = placeholder;
    return input;
}