<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="itmo.web.dead_web.Point" %>
<%@ page import="itmo.web.dead_web.PointManager" %>

<%
    PointManager pointManager = (PointManager) session.getAttribute("pointManager");
    ArrayList<Point> points = (pointManager != null) ? pointManager.getPoints() : new ArrayList<>();
%>

<!DOCTYPE html>
<html lang="ru-RU">

<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа №2</title>
    <script src="static/js/jquery-3.7.1.min.js"></script>
    <script defer src="static/js/dot.js"></script>
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
    <form action="/server/check" method="get" id="form">
        <main class="main">
            <div class="main__left-column">
                <div class="main__block">
                    <div class="result-title">Результат</div>
                    <div class="result-container">
                        <div class="result-header">
                            <div>X</div>
                            <div>Y</div>
                            <div>R</div>
                            <div>Попадание</div>
                        </div>
                        <%
                            for (int index = points.size() - 1; index >= 0; index--) {
                                Point point = points.get(index);
                        %>
                        <div>
                            <div><%= point.getX() %>
                            </div>
                            <div><%= point.getY() %>
                            </div>
                            <div><%= point.getR() %>
                            </div>
                            <div><%= point.getResult() ? "<span style='color:green'>Да</span>" : "<span style='color:red'>Нет</span>" %>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </div>
                <!-- SVG-график -->
                <div class="main__block" id="graph-container">
                    <%@include file="static/media/graph.svg" %>
                </div>
            </div>
            <div>
                <div class="main__block">
                    <div class="row">Параметры</div>
                    <div class="row">
                        <div>Выберите X:</div>
                        <div>
                            <label><input type="radio" name="x" value="-2"> -2</label>
                            <label><input type="radio" name="x" value="-1.5"> -1.5</label>
                            <label><input type="radio" name="x" value="-1"> -1</label>
                            <label><input type="radio" name="x" value="-0.5"> -0.5</label>
                            <label><input type="radio" name="x" value="0"> 0</label>
                            <label><input type="radio" name="x" value="0.5"> 0.5</label>
                            <label><input type="radio" name="x" value="1"> 1</label>
                            <label><input type="radio" name="x" value="1.5"> 1.5</label>
                            <label><input type="radio" name="x" value="2"> 2</label>
                        </div>
                    </div>
                    <div class="row">
                        <div>Введите Y:</div>
                        <input name="y" id="y-input" type="text" placeholder="значение от -3 до 3" maxlength="7">
                    </div>
                    <div class="row">
                        <div>Выберите R:</div>
                        <div>
                            <button type="button" class="r-button" data-r="1">1</button>
                            <button type="button" class="r-button" data-r="2">2</button>
                            <button type="button" class="r-button" data-r="3">3</button>
                            <button type="button" class="r-button" data-r="4">4</button>
                            <button type="button" class="r-button" data-r="5">5</button>
                            <input type="hidden" name="r" id="r-value">
                        </div>
                    </div>
                </div>
                <button type="submit" class="main__block submit_button" style="margin-bottom: 15px;" id="submit">
                    Проверить
                </button>
                <div style="text-align: center; margin-top: 20px;">
                    <img src="static/media/ГифКА.gif" width="400px" height="400px">
                </div>
            </div>
        </main>
    </form>
</div>
<div class="notification-container" id="notification-container"></div>
</body>

<script>
    window.onload = function () {

        // Используем массив points из JSP
        let points = [];
        <%
            for (int index = points.size() - 1; index >= 0; index--) {
                Point point = points.get(index);
        %>
        points.push({
            x: <%= point.getX() %>,
            y: <%= point.getY() %>,
            r: <%= point.getR() %>,
            result: <%= point.getResult() %>
        });
        <%
            }
        %>

        loadDataFromSession(points);
        document.getElementById("y-input").value = "";
        $("#r-value").val("");
        sessionStorage.removeItem("r-value");
        $(".r-button").removeClass("selected");
    };


</script>
</html>
