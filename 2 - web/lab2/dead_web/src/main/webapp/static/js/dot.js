function loadDataFromSession(points) {
    if (points && points.length > 0) {
        const lastPointIndex = points.length - 1;
        const lastPoint = points[lastPointIndex];
        const neededPoint = points[0];

        if (lastPoint) {
            $("input[name='x'][value='" + lastPoint.x + "']").prop("checked", true);
            $("input[name='y']").val(lastPoint.y);
            $("#r-value").val(lastPoint.r);

            $("#graph circle").remove();

            drawPoint(neededPoint.x, neededPoint.y, neededPoint.r, neededPoint.result);
            console.log("Последняя точка нарисована:", neededPoint);
        }
    } else {
        console.warn("Массив точек пуст или не задан.");
    }
}

function checkData(x, y, r) {
    let resp = {isValid: true, reason: "Корректные данные"};

    if (isNaN(+x)) {
        resp.isValid = false;
        resp.reason = `Невалидные данные. X: ${x}`;
    } else if (isNaN(+y)) {
        resp.isValid = false;
        resp.reason = `Невалидные данные.  Y: ${y};`
    } else if (isNaN(+r)) {
        resp.isValid = false;
        resp.reason = `Невалидные данные.  Y: ${r};`
    }

    if (+x < -2 || +x > 2) {
        resp.isValid = false;
        resp.reason = "x должен быть в пределах от -2 до 2";
    }

    if (+y < -3 || +y > 3) {
        resp.isValid = false;
        resp.reason = "y должен быть в пределах от -3 до 3";
    }

    return resp;
}

function drawPoint(x, y, r, result) {
    let circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", x * 170 / r + 200);
    circle.setAttribute("cy", -y * 170 / r + 200);
    circle.setAttribute("r", 4);

    circle.style.stroke = "black";
    circle.style["stroke-width"] = "1px";
    circle.style.fill = result ? "#0ecc14" : "#d1220f";

    $("#graph").append(circle);
}

$(".r-button").click(function () {
    $(".r-button").removeClass("selected");
    $(this).addClass("selected");
    $("#r-value").val($(this).data("r"));
    console.log("Установлено значение R:", $(this).data("r"));
});

$("#form").submit(function (e) {
    let xValue = $("input[name='x']:checked").val();
    let yValue = $("input[name='y']").val();
    let rValue = $("#r-value").val();

    if (!xValue) {
        e.preventDefault();
        showNotification("Пожалуйста, выберите X.");
        return;
    } else if (!yValue) {
        e.preventDefault();
        showNotification("Пожалуйста, введите Y.");
        return;
    } else if (!rValue) {
        e.preventDefault();
        showNotification("Пожалуйста, выберите R.");
        return;
    }

    let result = checkData(xValue, yValue, rValue);

    if (!result.isValid) {
        e.preventDefault();
        showNotification(result.reason);
        return;
    }

    console.log("Отправка формы с данными:", {x: xValue, y: yValue, r: rValue});
});


$("#graph").on("click", function (e) {
    let rValue = $("#r-value").val();

    if (!rValue) {
        return;
    }

    let calculatedX = ((e.pageX - $(this).offset().left - $(this).width() / 2) / 150) * rValue;
    let calculatedY = ((($(this).height() / 2) - (e.pageY - $(this).offset().top)) / 150) * rValue;

    calculatedX = parseFloat(calculatedX.toFixed(2));
    calculatedY = parseFloat(calculatedY.toFixed(2));


    let result = checkData(calculatedX, calculatedY, rValue);
    window.location.href = "/server/check?x=" + calculatedX + "&y=" + calculatedY + "&r=" + rValue;


});


function showNotification(message) {
    let container = document.getElementById("notification-container");

    let notification = document.createElement("div");
    notification.className = "notification";
    notification.textContent = message;

    container.appendChild(notification);

    // Делаем уведомление видимым
    setTimeout(() => {
        notification.classList.add("show");
    }, 100);

    // Удаляем уведомление через 3 секунды
    setTimeout(() => {
        notification.classList.remove("show");
        setTimeout(() => {
            container.removeChild(notification);
        }, 500);
    }, 3000);
}






