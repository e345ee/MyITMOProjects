<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="itmo.web.dead_web.Point" %>
<%@ page import="java.io.PrintWriter" %>

<% %>
<!DOCTYPE html>
<html lang="ru-RU">

<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа №2 - Результат</title>
    <script src="static/js/jquery-3.7.1.min.js"></script>
    <script src="static/js/dot.js"></script>
    <link rel="stylesheet" href="static/css/index.css">
    <link rel="icon" type="image/jpg" href="static/media/1.png">
</head>

<body>
<div class="content-container">
    <header class="header">
        <div class="header-container">
            <div>Садовой Григорий Владимирович P3207</div>
            <div></div>
            <div>368748</div>
        </div>
    </header>
    <main class="main">
        <div class="main__left-column">
            <div class="main__block">
                <a class="link-to-form" href="/server/index">Вернуться к форме</a>
            </div>
        </div>
        <div>
            <div class="result-title">Результат</div>
            <div class="result-container">
                <div class="result-header">
                    <div>X</div>
                    <div>Y</div>
                    <div>R</div>
                    <div>Попадание</div>
                </div>
                <%
                    Point newPoint = (Point) request.getAttribute("newPoint");
                    PrintWriter logWriter = response.getWriter();
                    if (newPoint == null) {
                        logWriter.println("result.jsp: newPoint отсутствует (null).");
                    } else {
                        logWriter.println("result.jsp: Получен newPoint: X=" + newPoint.getX() +
                                ", Y=" + newPoint.getY() +
                                ", R=" + newPoint.getR() +
                                ", Попадание=" + newPoint.getResult());
                    }

                    if (newPoint != null) {
                %>
                <div class="result-row">
                    <div><%= newPoint.getX() %></div>
                    <div><%= newPoint.getY() %></div>
                    <div><%= newPoint.getR() %></div>
                    <div>
                        <%= newPoint.getResult() ? "<span style='color:green'>Да</span>" : "<span style='color:red'>Нет</span>" %>
                    </div>
                </div>
                <%
                } else {
                %>
                <div class="result-row">Данные отсутствуют</div>
                <%
                    }
                %>
            </div>
        </div>
    </main>
</div>

<script>
    console.log("Страница результата загружена.");

    <% if (newPoint != null) { %>
    console.log("Отрисовка точки с сервера: X=<%= newPoint.getX() %>, Y=<%= newPoint.getY() %>, R=<%= newPoint.getR() %>, Результат=<%= newPoint.getResult() %>");
    drawPoint(<%= newPoint.getX() %>, <%= newPoint.getY() %>, <%= newPoint.getR() %>, <%= newPoint.getResult() %>);
    <% } else { %>
    console.log("Данные точки отсутствуют.");
    <% } %>
</script>
</body>

</html>
