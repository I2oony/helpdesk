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
    p.innerHTML = pageTitle;

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
    ul.innerHTML = title;
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
    logoutUl.innerHTML = "Выход";
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
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 GMT;";
    window.location.pathname = "/";
}