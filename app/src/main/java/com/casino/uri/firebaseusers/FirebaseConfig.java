package com.casino.uri.firebaseusers;
import android.app.Application;
import com.firebase.client.Firebase;
/**
 * Created by 48089748z on 10/02/16.
 */
public class FirebaseConfig extends Application
{
    private Firebase mainReference;
    private Firebase usersListReference;
    private Firebase loggedUserReference;
    private String language = "ENGLISH";
    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        mainReference = new Firebase("https://userstesturi.firebaseio.com/");
        usersListReference = new Firebase("https://userstesturi.firebaseio.com/UsersList");
        loggedUserReference = new Firebase("https://userstesturi.firebaseio.com/UsersList");

    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public Firebase getLoggedUserReference() {
        return loggedUserReference;
    }
    public void setLoggedUserReference(Firebase loggedUserReference) {
        this.loggedUserReference = loggedUserReference;
    }

    public Firebase getUsersListReference() {
        return usersListReference;
    }

    public void setUsersListReference(Firebase usersListReference) {
        this.usersListReference = usersListReference;
    }

    public Firebase getMainReference() {
        return mainReference;
    }

    public void setMainReference(Firebase mainReference) {
        this.mainReference = mainReference;
    }
}



