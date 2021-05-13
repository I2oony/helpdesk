buildPage(user);

function buildPage(user) {
    var role = user["role"];
    switch (role) {
        case "admin":

        case "operator":

        case "client":
            buildTopBar("Главная панель");
            break;
        default:
            break;
    }
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

function openMenu() {
    let menu = document.getElementById('menu-bar');
    menu.classList.toggle('menu');
    menu.classList.toggle('menu-opened');
}