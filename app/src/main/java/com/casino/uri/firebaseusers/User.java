package com.casino.uri.firebaseusers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by uRi on 12/02/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    private String UID;
    private String email;
    private String password;
    private String key;
    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
