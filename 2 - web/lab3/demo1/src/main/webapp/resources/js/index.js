function updateClocks() {
    const date = new Date();
    let hours = date.getHours();
    let minutes = date.getMinutes();
    let seconds = date.getSeconds();
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();


    hours = hours < 10 ? "0" + hours : hours;
    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;
    day = day < 10 ? "0" + day : day;
    month = month < 10 ? "0" + month : month;

    // Отображаем часы с секундами
    document.getElementById("time").innerHTML = `${hours}:${minutes}:${seconds} ${day}.${month}.${year}`;

    // Обновляем каждую секунду
    setTimeout(updateClocks, 7000);
}


function showLoadingScreen(event) {
    event.preventDefault(); // Остановим стандартный переход по ссылке

    // Скрываем основной контент
    document.getElementById("main-content").style.display = "none";

    // Показываем экран загрузки
    document.getElementById("loading-screen").style.display = "flex";

    // Через 3 секунды (можно изменить) перенаправляем на основную страницу
    setTimeout(() => {
        window.location.href = document.querySelector(".link-to-main-page").getAttribute("href");
    }, 3000);
}


window.onload = updateClocks;
