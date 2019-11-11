package io.moonshard.moonshard;

public class LoginCredentials {
    public String username = "mytest";
    public String password = "test";
    public String jabberHost = "moonshard.tech";

    public boolean isEmpty() {
        return username.equals("") && password.equals("") && jabberHost.equals("");
    }
}
