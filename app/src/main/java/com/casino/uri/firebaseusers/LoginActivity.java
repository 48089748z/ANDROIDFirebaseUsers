package com.casino.uri.firebaseusers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private Firebase mainReference;
    private FirebaseConfig config;
    private TextView information;
    private EditText emailLogin;
    private EditText passwordLogin;
    private EditText emailSignup;
    private EditText passwordSignup;
    boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        config = (FirebaseConfig) getApplication();
        mainReference = config.getMainReference();
        information = (TextView) this.findViewById(R.id.TVinformation);
        emailLogin = (EditText) this.findViewById(R.id.ETemailLogin);
        passwordLogin = (EditText) this.findViewById(R.id.ETpasswordLogin);
        emailSignup = (EditText) this.findViewById(R.id.ETemailSignup);
        passwordSignup = (EditText) this.findViewById(R.id.ETpasswordSignup);
        Button login = (Button) this.findViewById(R.id.BTlogin);
        Button signup = (Button) this.findViewById(R.id.BTsignup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String email = emailLogin.getText().toString();
                final String password = passwordLogin.getText().toString();

                mainReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData)
                    {
                        login(email, password, authData);
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError)
                    {
                        information.setText("\n"+firebaseError.getMessage());
                    }
                });

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailSignup.getText().toString();
                final String password = passwordSignup.getText().toString();
                mainReference.createUser(email, password, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        information.setText("\nSuccesfully Created User \n\nEmail: " + email + "\nPassword: " + password);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        information.setText("\n" + firebaseError.getMessage());
                    }
                });
            }
        });
    }
    public void login(final String email, final String password, final AuthData authData)
    {
        final Firebase usersList = mainReference.child("UsersList");
        usersList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot usersSnapshot : snapshot.getChildren()) {
                    User user = usersSnapshot.getValue(User.class);
                    if (user.getUID().equals(authData.getUid()))
                    {
                        config.setMainReference(usersList.child(user.getKey()));
                        exists = true;
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        if (exists==false)
        {
            Firebase userF = usersList.push();
            User newUser = new User();
            newUser.setKey(userF.getKey());
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUID(authData.getUid());
            userF.setValue(newUser);
            config.setMainReference(usersList.child(userF.getKey()));
            exists=false;
        }
        Intent startApp = new Intent(this, MainActivity.class);
        startActivity(startApp);
    }
    @Override
    protected void onStart() {
        super.onStart();
        emailLogin.setText("48089748z@iespoblenou.org");
        passwordLogin.setText("password");
        emailSignup.setHint("Email");
        passwordSignup.setHint("Password");
    }
}

