package io.moonshard.moonshard;

public class LoginCredentials {
    public String username = "";
    public String password = "";
    public String accessToken = "";
    public String refreshToken = "";
    public String jabberHost = "moonshard.tech";


    public boolean isEmpty() {
        return username.equals("") && password.equals("") && accessToken.equals("") && refreshToken.equals("");
    }
}
