package responses;

//Это класс, представляющий ответ с ошибкой.
public class ErrorResponse extends CommandStatusResponse {



    public ErrorResponse(String message) {
        super(message, -228);

    }

}