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
            buildSystemPage();
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

    let profilePage = buildMainContent("profile-page");

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
    changePasswordBlock.append(buildButton("Сменить", changePass));

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

async function buildSystemPage() {
    let systemPage = buildMainContent("system-page");

    let createUserBlock = buildBlock("create-user-block");
    createUserBlock.append(buildBlockHeader("Создать пользователя"));
    createUserBlock.append(buildInputField("text", "username", "Логин"));
    createUserBlock.append(buildInputField("text", "first-name", "Имя"));
    createUserBlock.append(buildInputField("text", "last-name", "Фамилия"));
    createUserBlock.append(buildInputField("text", "email", "Адрес электронной почты"));
    let roles = [
        {value: "client", text: "Клиент"},
        {value: "operator", text: "Оператор"},
        {value: "admin", text: "Администратор"}];
    createUserBlock.append(buildDropdown(roles));
    createUserBlock.append(buildButton("Создать", createUser));

    let usersListBlock = buildBlock("users-list-block");
    usersListBlock.append(buildBlockHeader("Список пользователей"));
    let usersList = await fetchUsersList();
    let usersTable = document.createElement('table');
    usersTable.id = "users-table";
    var header = {
        username: "Логин",
        firstName: "Имя",
        lastName: "Фамилия",
        email: "Электронная почта",
        role: "Роль"}
    usersTable.append(buildUserRow(-1, header));
    for (user in usersList) {
        usersTable.append(buildUserRow(user, usersList[user]));
    }
    usersListBlock.append(usersTable);

    systemPage.append(createUserBlock);
    systemPage.append(usersListBlock);

    document.body.append(systemPage);
}

async function fetchUsersList() {
    config.method = 'get';
    config.url = "/api/users/list";

    var body = null;

    await axios(config)
        .then(function (response) {
            body = response.data;
        })
        .catch(function (response) {
            body = null;
        });

    return body;
}

function buildUserRow(num, user) {
    var tr = document.createElement('tr');

    var className = "font-header-6";

    var td0 = document.createElement('td');
    td0.textContent = parseInt(num) + 1;
    td0.className = className;

    var td1 = document.createElement('td');
    td1.textContent = user["username"];
    td1.className = className;

    var td2 = document.createElement('td');
    td2.textContent = user["firstName"];
    td2.className = className;

    var td3 = document.createElement('td');
    td3.textContent = user["lastName"];
    td3.className = className;

    var td4 = document.createElement('td');
    td4.textContent = user["email"];
    td4.className = className;

    var td5 = document.createElement('td');
    td5.textContent = user["role"];
    td5.className = className;

    tr.append(td0);
    tr.append(td1);
    tr.append(td2);
    tr.append(td3);
    tr.append(td4);
    tr.append(td5);

    return tr;
}

function createUser() {
    config.method = "post";
    config.url = "/api/users/create";
    
    var username = document.getElementById("username").value;
    var firstName = document.getElementById("first-name").value;
    var lastName = document.getElementById("last-name").value;
    var email = document.getElementById("email").value;

    var body = {
        username: username,
        firstName: firstName,
        lastName: lastName,
        email: email,
        role: role
    };
    config.data = JSON.stringify(body);

    axios(config)
        .then(function (response) {
            
        })
        .catch(function (response) {
            
        });
}

function buildMainContent(id) {
    var div = document.createElement('div');
    div.id = id;
    div.className = "main-content";
    return div;
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

function buildButton(title, action) {
    var button = document.createElement('button');
    button.className = "button-text z-axis-1 font-button";
    button.textContent = title;
    button.onclick = action;
    return button;
}

// Array of following elements: {value: "some_value", text: "some_text"}
function buildDropdown(options) {
    var select = document.createElement('select');
    select.className = "dropdown-list";
    for (option in options) {
        var opt = document.createElement('option');
        opt.value = options[option]["value"];
        opt.text = options[option]["text"];
        select.append(opt);
    }
    return select;
}