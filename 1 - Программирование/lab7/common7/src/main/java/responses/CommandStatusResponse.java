package responses;

//Это класс, представляющий статусный ответ.
public class CommandStatusResponse extends AbsResponse {

    private final String response;
    private final int statusCode;

    public CommandStatusResponse(String response, int statusCode) {
        this.response = response;
        this.statusCode = statusCode;
    }

    public static CommandStatusResponse ofString(String response) {
        return new CommandStatusResponse(response, 0);
    }

    public String getResponse() {
        return response;
    }

    public int getStatusCode() {
        return statusCode;
    }
}