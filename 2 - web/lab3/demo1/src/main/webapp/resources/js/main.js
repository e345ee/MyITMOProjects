$(document).on("click", ".r-button", function() {
    setTimeout(redrawDots, 100);
});

function redrawDots() {
    const r = document.getElementById("form:hiddenR").value;

    if (!r || isNaN(r) || r <= 0) {
        showNotification("Невозможно вычислить попадание, выберите R.");
        return;
    }

    $("#graph circle").remove();

    const rows = document.querySelectorAll("#form\\:table tbody tr");
    rows.forEach(row => {
        const cells = row.querySelectorAll("td");
        const x = +cells[0].innerText;
        const y = +cells[1].innerText;

        let result = checkHit(x, y, r);
        drawDots(x, y, r, result);
    });
}



function drawDots(x, y, r, result) {
    let circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", x * 170 / r + 200);
    circle.setAttribute("cy", -y * 170 / r + 200);
    circle.setAttribute("r", 4);

    circle.style.stroke = "black";
    circle.style["stroke-width"] = "1px";
    circle.style.fill = result? "#0ecc14" : "#d1220f";

    $("#graph").append(circle);
}




function checkData(x, y, r) {
    let resp = {
        isValid: true,
        reason: "Корректные данные"
    }

    if (isNaN(+x) || isNaN(+y) || isNaN(+r)) {
        resp.isValid = false;
        resp.reason = "Невалидные данные"
    }

    if (+y < -5) {
        resp.isValid = false;
        resp.reason = "y должен быть больше или равен -5 (y=" + +y + ")";
    }
    if (+y > 3) {
        resp.isValid = false;
        resp.reason = "y должен быть меньше или равен 3 (y=" + +y + ")";
    }

    return resp;
}

function validateForm() {
    let xElement = document.querySelector('input[name="form:x"]:checked');
    let x = xElement ? xElement.value : null;
    let y = document.getElementById("form:y").value;
    let r = document.getElementById("form:hiddenR").value;

    if (!r || isNaN(r) || r <= 0) {
        showNotification("Выберите R перед проверкой!");
        return false;
    }

    if (!x) {
        showNotification("Выберите X перед проверкой!");
        return false;
    }

    let result = checkData(x, y, r);
    if (!result.isValid) {
        showNotification(result.reason);
        return false;
    }

    return true;
}





function checkHit(x, y, r) {
    return inRect(x, y, r) || inTriangle(x, y, r) || inCircle(x, y, r);
}

function inRect(x, y, r) {
    return x >= 0 && x <= r && y >= 0 && y <= r / 2;
}

function inTriangle(x, y, r) {
    return y >= -x - r / 2 && x <= 0 && y <= 0;
}

function inCircle(x, y, r) {
    return x >= 0 && y <= 0 && (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2));
}

$(document).ready(function () {
    setTimeout(redrawDots, 300);
})


function showNotification(message) {
    let notificationBox = document.getElementById("notification-box");
    notificationBox.innerText = message;
    notificationBox.style.display = "block";

    setTimeout(() => {
        notificationBox.style.display = "none";
    }, 3000);
}




$(document).on("click", "#graph", function(e) {
    let rValue = document.getElementById("form:hiddenR").value;

    if (!rValue || isNaN(rValue) || rValue <= 0) {
        showNotification("Невозможно вычислить попадание, выберите R.");
        return;
    }

    let calculatedX = (e.pageX - $(this).offset().left - $(this).width() / 2) / 135 * rValue;
    let calculatedY = ($(this).height() / 2 - (e.pageY - $(this).offset().top)) / 135 * rValue;

    let result = checkData(calculatedX, calculatedY, rValue);
    if (!result.isValid) {
        showNotification(result.reason);
        return;
    }

    document.getElementById("checkForm:hiddenX").value = calculatedX;
    document.getElementById("checkForm:hiddenY").value = calculatedY;
    document.getElementById("checkForm:hiddenR").value = rValue;

    document.getElementById("checkForm:checkButton").click();
});
