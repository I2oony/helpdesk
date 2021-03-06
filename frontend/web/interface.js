buildPage(user);

function buildPage(user) {
    var role = user["role"];
    buildMenu(role);
    var path = document.location.pathname;
    var title;
    switch (path) {
        case "/web/dashboard/":
            title = "Панель";
            buildDashboardPage();
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
        case "/web/tickets/":
            var ticketId = document.location.search.split("=")[1];
            title = "Заявка #" + ticketId;
            buildTicketPage(ticketId, user);
            break;
        default:
            title = "Ошибка 404 - страница не найдена"
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

    if (user['role']!="client") {
        let statusSwitcher = createStatusSwitcher();
        div.appendChild(statusSwitcher);
    }
    
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

async function buildDashboardPage() {
    var dashboardPage = buildMainContent("dashboard-page");

    var newTicketBlock = buildBlock("new-ticket-block");
    newTicketBlock.append(buildBlockHeader("Создать новую заявку"));
    
    var newTicketDiv = document.createElement('div');
    var newTicketForm = document.createElement('form');
    newTicketForm.id = "new-ticket-form";
    newTicketForm.method = "dialog";
    newTicketForm.onsubmit = createNewTicket;
    var titleLabel = document.createElement('div');
    titleLabel.className = "font-header-6";
    titleLabel.textContent = "Тема обращения:";
    newTicketForm.append(titleLabel);
    var titleField = document.createElement('input');
    titleField.id = "ticket-title-field";
    titleField.className = "field font-header-6";
    titleField.maxLength = 100;
    newTicketForm.append(titleField);
    var createButton = buildButton("Создать", null);
    newTicketForm.append(createButton);
    newTicketDiv.append(newTicketForm);

    newTicketBlock.append(newTicketDiv);
    dashboardPage.append(newTicketBlock);


    var ticketsListBlock = buildBlock("tickets-list");
    ticketsListBlock.append(buildBlockHeader("Список существующих заявок"));

    ticketsListBlock.classList.add("font-header-6");

    var table = await buildTicketsList();

    ticketsListBlock.append(table);

    dashboardPage.append(ticketsListBlock);
    document.body.append(dashboardPage);

    setInterval(buildTicketsList, 30000);
}

async function buildTicketsList() {
    var table = document.getElementById("tickets-table");

    if (table!=null) {
        table.innerHTML = '';
    }
    var header = {
        id: "No.",
        title: "Тема",
        requester: "Отправитель",
        state: "Состояние",
        date: "Время"
    }
    var table = document.createElement('table');
    table.id = "tickets-table";
    table.append(buildTicketBody(header));
    
    var ticketList = await fetchTicketsList();
    ticketList = sortTicketsList(ticketList);
    for (ticket in ticketList) {
        table.append(buildTicketBody(ticketList[ticket]));
    }

    return table;
}

function sortTicketsList(ticketsList) {
    ticketsList.sort(function (a, b) {
        if (a.state == "waiting" && (b.state === "freeze" || b.state ===  "created" || b.state ===  "closed")) {
            return -1;
        } else if (a.state == "freeze" && (b.state ===  "created" || b.state ===  "closed")) {
            return -1;
        } else if (a.state == "created" && b.state ===  "closed") {
            return -1;
        } else {
            return 0;
        }
    });
    return ticketsList;
}

async function createNewTicket() {
    console.log("help");
    var title = document.getElementById("ticket-title-field").value;
    config.method = "post";
    config.url = "/api/tickets/create";

    var body = {
        title: title,
        requester: user['username'],
        messages: []
    };

    if(user['role'] == "operator") {
        body.operator = [user.username]
    } else {
        body.operator = [];
    }

    config.data = JSON.stringify(body);

    var respBody = null;
    await axios(config)
        .then(function (response) {
            respBody = response.data;
            var url = "https://" + document.location.hostname + "/web/tickets" + "?id=" + respBody['id'];
            document.location.assign(url);
        })
        .catch(function (response) {
            respBody = null;
            console.log("Something went wrong while creating the ticket!")
        });
}

function buildTicketBody(ticketInfo) {
    var tbody = document.createElement('tbody');

    var tr1 = document.createElement('tr');
    var tdNum = document.createElement('td');
    tdNum.className = "ticket-num";
    tdNum.textContent = ticketInfo.id;
    tr1.append(tdNum);

    var td1 = document.createElement('td');
    td1.className = "col1";
    td1.textContent = ticketInfo.title;
    tr1.append(td1);

    if (user['role']!="client"){
        var td2 = document.createElement('td');
        td2.className = "col2";
        td2.textContent = ticketInfo.requester;
        tr1.append(td2);

        var td3 = document.createElement('td');
        td3.className = "col3";
        td3.textContent = stateLocalization(ticketInfo.state);
        tr1.append(td3);
    }

    var td4 = document.createElement('td');
    td4.className = "col4";
    if (ticketInfo.date == null) {
        if (ticketInfo.messages != null) {
            var date = new Date(ticketInfo.messages[0].date);
            td4.textContent = date.toLocaleString();
        } else {
            td4.textContent = "-";
        }
    } else {
        td4.textContent = ticketInfo.date;
    }
    tr1.append(td4);

    tbody.append(tr1);

    if (Number.isInteger(ticketInfo.id)) {
        tbody.onclick = openTicket;
    }

    return tbody;
}

function openTicket(event) {
    var ticketId = event.target.parentElement.firstElementChild.innerHTML;
    var url = "https://" + document.location.hostname + "/web/tickets" + "?id=" + ticketId;
    document.location.assign(url);
}

async function fetchTicketsList() {
    config.method = 'get';
    config.url = "/api/tickets";

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
    
    var msgAfter = document.getElementById('change-password-block').firstElementChild;

    if (newPass.length < 8) {
        showError("Пароль менее 8 символов!", msgAfter);
    } else if (newPass == repeatPass) {
        var body = {
            oldPassword: oldPass,
            newPassword: newPass
        }
        config.data = JSON.stringify(body);
        axios(config)
            .then(function (response) {
                showSuccess("Пароль успешно изменён!", msgAfter);
            })
            .catch(function (response) {
                showError("Неверно указан старый пароль!", msgAfter);
            });
    } else {
        showError("Пароли несовпадают, попробуйте снова!", msgAfter);
    }
}

function showError(text, insertAfter) {
    if (document.getElementById("result-message") == null) {
        var errorMsg = document.createElement('span');
        errorMsg.textContent = text;
        errorMsg.classList.add("error-message");
        errorMsg.className = "error-message font-subtitle-1";
        errorMsg.id = "result-message";
        insertAfter.after(errorMsg);
    } else {
        var errorMsg = document.getElementById("result-message");
        errorMsg.textContent = text;
    }
}

function showSuccess(text, insertAfter) {
    if (document.getElementById("result-message") == null) {
        var successMsg = document.createElement('span');
        successMsg.textContent = text;
        successMsg.classList.remove("error-message");
        successMsg.className = "font-subtitle-1";
        successMsg.id = "result-message";
        insertAfter.after(successMsg);
    } else {
        var errorMsg = document.getElementById("result-message");
        errorMsg.textContent = text;
    }
}

// TODO: add an ability to create templates
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
    createUserBlock.append(buildDropdown("role", roles));
    createUserBlock.append(buildButton("Создать", createUser));

    let usersListBlock = await buildUserListBlock();

    systemPage.append(createUserBlock);
    systemPage.append(usersListBlock);

    document.body.append(systemPage);
}

async function buildUserListBlock() {
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
    usersTable.append(buildUserRow(header, "font-header-5"));
    for (user in usersList) {
        usersTable.append(buildUserRow(usersList[user], "font-header-6"));
    }
    usersListBlock.append(usersTable);
    return usersListBlock
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

function buildUserRow(user, className) {
    var tr = document.createElement('tr');

    var className = className;

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

    tr.append(td1);
    tr.append(td2);
    tr.append(td3);
    tr.append(td4);
    tr.append(td5);

    return tr;
}

async function createUser() {
    config.method = "post";
    config.url = "/api/users/create";
    
    var username = document.getElementById("username").value;
    var firstName = document.getElementById("first-name").value;
    var lastName = document.getElementById("last-name").value;
    var email = document.getElementById("email").value;
    var role = document.getElementById("role").value;

    var body = {
        username: username,
        firstName: firstName,
        lastName: lastName,
        email: email,
        role: role
    };
    config.data = JSON.stringify(body);

    var msgAfter = document.getElementById("create-user-block").firstElementChild;

    axios(config)
        .then(function (response) {
            showSuccess("Пользователь успешно создан!", msgAfter);
        })
        .catch(function (response) {
            console.log(response);
            showError("Такой пользователь уже существует!", msgAfter);
        });
    
    var systemPage = document.getElementById("system-page");
    var usersListBlock = document.getElementById("users-list-block");
    usersListBlock.remove();
    usersListBlock = await buildUserListBlock();
    systemPage.append(usersListBlock);
}

async function buildTicketPage(ticketId, user) {
    var body = await fetchTicketData(ticketId);

    var ticketPage = document.createElement('div');
    ticketPage.id = "ticket-page";
    ticketPage.className = "main-content";

    var ticketInfoBlock = buildBlock("ticket-info-block");
    ticketInfoBlock.append(buildBlockHeader("\"" + body["title"] + "\""));
    ticketInfoBlock.append(buildTicketInfoDiv(body['requester'], body['state']));
    ticketPage.append(ticketInfoBlock);

    var messagesContent = document.createElement('div');
    messagesContent.id = "messages-block";
    messagesContent.className = "surface";
    messagesContent.append(buildMessagesList(body["messages"], user["username"]));

    var inputSurface = document.createElement('div');
    inputSurface.id = "input-block";
    inputSurface.className = "surface";
    inputSurface.append(buildMessageForm())

    ticketPage.append(messagesContent);
    ticketPage.append(inputSurface);
    document.body.append(ticketPage);

    var topBarHeight = document.getElementsByClassName('top-bar')[0].scrollHeight;
    var ticketInfoBlockHeight = document.getElementById("ticket-info-block").scrollHeight;
    var inputSurfaceHeight = inputSurface.scrollHeight;

    var messagesContetnHeight = (window.innerHeight - topBarHeight - inputSurfaceHeight - ticketInfoBlockHeight) - 120;
    messagesContent.style.height = messagesContetnHeight + "px";

    messagesContent.scrollTo(0, 99999);

    setInterval(checkForNewMessages, 10000, ticketId);
}

function buildMessagesList(messages, username) {
    var messagesList = document.getElementById("messages-list");

    if (messagesList!=null) {
        messagesList.innerHTML = '';
    } else {
        messagesList = document.createElement('div');
        messagesList.id = "messages-list";
    }

    if (messages.length == 0) {
        var firstMessageTip = document.createElement('div');
        firstMessageTip.className = "font-subtitle-2";
        firstMessageTip.innerHTML = "<i>Напишите первое сообщение!</i><br>";
        messagesList.append(firstMessageTip);
    }

    for (message in messages) {
        messagesList.append(buildMessageBlock(messages[message], username));
    }
    return messagesList;
}

function buildMessageBlock(message, username) {
    var div = document.createElement('div');
    div.classList.add("message-block");

    var date = new Date(message['date']);

    var from = document.createElement('div');
    from.textContent = message['from'] + " - " + date.toLocaleString();
    from.classList.add("font-subtitle-2");
    from.classList.add("message-block-info");

    var text = document.createElement('div');
    if (message['text']!= ""){
        text.innerHTML = newRowSymbolCheck(message['text']);
    } else {
        text.innerHTML = "<i>Состояние заявки изменено.</i>";
    }
    text.classList.add("font-header-6");

    div.append(from);
    div.append(text);
    if (username == message['from']){
        div.classList.add("right");
    } else {
        div.classList.add("left");
    }
    return div;
}

function newRowSymbolCheck(string) {
    var newString = "";
    for (i in string) {
        if (string[i] == "\n") {
            newString += "<br>";
        } else {
            newString += string[i];
        }
    }
    return newString;
}

function buildMessageForm() {
    var form = document.createElement('form');
    form.method = "dialog";
    form.onsubmit = sendMessage;
    form.id = "message-form"

    var input = document.createElement('textarea');
    input.id = "message-text-field";
    input.className = "message-field font-header-6";
    input.placeholder = "Напишите здесь своё сообщение";
    input.autofocus = true;
    input.rows = 3;
    form.append(input);

    if (user['role'] != "client") {
        var divDropdowns = document.createElement('div');

        divDropdowns.className = "dropdown-block"
        var stateList = [
            {value: "freeze", text: "Ожидает ответ клиента"},
            {value: "waiting", text: "Ожидает ответ оператора"},
            {value: "closed", text: "Закрыта"}
        ]
        var stateDropdown = buildDropdown("ticket-state-dropdown", stateList);
        divDropdowns.append(stateDropdown);
        
        // TODO: make the list from received from server templates
        // var templatesList = [
        //     {value: "id0", text: "Шаблоны"},
        //     {value: "id1", text: "Шаблон 1"},
        //     {value: "id2", text: "Шаблон 2"}
        // ]
        // var templatesDropdown = buildDropdown("message-template-dropdown", templatesList);
        // templatesDropdown.onchange = appendTemplateText;
        // divDropdowns.append(templatesDropdown);

        form.append(divDropdowns)
    }

    var button = buildButton("Отправить", null);

    form.append(button);

    return form;
}

function appendTemplateText(event) {
    var textArea = document.getElementById("message-text-field");
    var selectedItem = event.target.options.selectedIndex;
    var dropdownItem = document.getElementById("message-template-dropdown")[selectedItem].value;
    console.log(dropdownItem);
}

async function sendMessage() {
    var form = document.getElementById("message-form");
    form.onsubmit = null;

    var ticketStateItem = document.getElementById("ticket-state-dropdown");
    var ticketState = "waiting";

    if (ticketStateItem != null) {
        ticketState = ticketStateItem.value;
    }

    config.method = "post";
    config.url = "/api/ticket";
    config.params = {
        ticketId: document.location.search.split("=")[1],
        state: ticketState
    }
    
    var date = new Date().toJSON();
    var text = document.getElementById("message-text-field").value;

    var body = {
        id: 0,
        from: user['username'],
        text: text,
        date: date
    };

    config.data = JSON.stringify(body);

    var respBody = null;
    await axios(config)
        .then(function (response) {
            respBody = response.data;
            buildMessagesList(respBody["messages"], user["username"]).parentElement.scrollTo(0, 99999);
            buildTicketInfoDiv(respBody['requester'], respBody['state']);
            form.onsubmit = sendMessage;
            document.getElementById("message-text-field").value = "";
        })
        .catch(function (response) {
            respBody = null;
            console.log("Something went wrong while sending the message!")
            form.onsubmit = sendMessage;
        });
    config.params = {};
}

async function fetchTicketData(ticketId) {
    config.method = "get";
    config.url = "/api/ticket";
    config.params = {
        ticketId: ticketId
    }

    var body = null;

    await axios(config)
        .then(function (response) {
            body = response.data;
        })
        .catch(function (response) {
            body = null;
            console.log("Error while fetching the ticket data.");
        });
    
    config.params = {};
    return body;
}

async function checkForNewMessages(ticketId) {
    var body = await fetchTicketData(ticketId);
    buildMessagesList(body["messages"], user["username"]);
    buildTicketInfoDiv(body['requester'], body['state']);
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
    header.className = "font-header-4 surface-header";
    header.textContent = title;
    header.onclick = hideElement;
    return header;
}

function hideElement(event) {
    var elements = event.target.parentElement.childNodes;
    for (i=1; i<elements.length; i++) {
        elements[i].classList.toggle("hide-element");
    }
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
    if (action!=null) {
        button.onclick = action;
    }
    return button;
}

// Array of following elements: {value: "some_value", text: "some_text"}
function buildDropdown(id, options) {
    var select = document.createElement('select');
    select.id = id;
    select.className = "dropdown-list font-subtitle-1";
    for (option in options) {
        var opt = document.createElement('option');
        opt.value = options[option]["value"];
        opt.text = options[option]["text"];
        select.append(opt);
    }
    return select;
}

function buildTicketInfoDiv(requester, state) {
    var ticketInfoDiv = document.getElementById("ticket-info-div");
    if (ticketInfoDiv!=null) {
        ticketInfoDiv.innerHTML = "";
    } else {
        ticketInfoDiv = document.createElement('div');
        ticketInfoDiv.id = "ticket-info-div";
        ticketInfoDiv.className = "font-header-6";
    }
    ticketInfoDiv.textContent = "От: " + requester + ", cостояние: " + stateLocalization(state);
    return ticketInfoDiv;
}

function stateLocalization(state) {
    var ticketStateString;
    switch (state) {
        case "created":
            ticketStateString = "Создана";
            break;
        case "waiting":
            ticketStateString = "Ожидает ответ оператора";
            break;
        case "freeze":
            ticketStateString = "Ожидает ответ клиента";
            break;
        case "closed":
            ticketStateString = "Закрыта";
            break;
        default:
            ticketStateString = "Состояние";
            break;
    }
    return ticketStateString;
}

function createStatusSwitcher() {
    var div = document.createElement('div');
    div.id = "status-block";

    var divText = document.createElement('div');
    divText.className = "font-header-6";
    divText.textContent = "Готов обрабатывать заявки:";
    div.append(divText);

    var divSwitcher = document.createElement('div');
    divSwitcher.id = "status-switcher";
    divSwitcher.className = "status-switcher-off";

    var divOnline = document.createElement('div');
    divOnline.id = "switcher";
    divOnline.className = "font-subtitle-1 switcher-off";
    divOnline.textContent = "";

    divSwitcher.append(divOnline);
    divSwitcher.onclick = function changeStatus() {
        operatorStatus("patch");
    };

    div.append(divSwitcher);
    operatorStatus("get");

    return div;
}

function operatorStatus(method) {
    config.url = "/api/users/state";
    config.method = method;

    axios(config)
        .then(function (response) {
            body = response.data;
            var divSwitcher = document.getElementById("status-switcher");
            var divOnline = document.getElementById("switcher");
            if (body['status'] == "offline") {
                divSwitcher.classList.add("status-switcher-off");
                divSwitcher.classList.remove("status-switcher-on");
                divOnline.classList.add("switcher-off");
                divOnline.classList.remove("switcher-on");
            } else {
                divSwitcher.classList.remove("status-switcher-off");
                divSwitcher.classList.add("status-switcher-on");
                divOnline.classList.remove("switcher-off");
                divOnline.classList.add("switcher-on");
            }
        })
        .catch(function (error) {
            console.log(error);
        })
}
