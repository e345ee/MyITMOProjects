package responses;

public class AuthResponse extends AbsResponse {
    boolean auth;
    public AuthResponse(boolean auth) {
        this.auth = auth;
    }

    public boolean isAuth() {
        return auth;
    }
}
