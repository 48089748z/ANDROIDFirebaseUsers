package com.casino.uri.firebaseusers;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    Button login;
    Button signup;
    Toolbar toolbar;
    boolean exists;
    TextView information;
    EditText emailLogin;
    EditText passwordLogin;
    EditText emailSignup;
    EditText passwordSignup;
    FirebaseConfig config;
    Firebase mainReference;
    Firebase usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        config = (FirebaseConfig) getApplication();
        mainReference = config.getMainReference();
        usersList = mainReference.child("UsersList");

        toolbar.setTitle("LOGIN OR SIGN UP");
        setSupportActionBar(toolbar);
        information = (TextView) this.findViewById(R.id.TVinformation);
        emailLogin = (EditText) this.findViewById(R.id.ETemailLogin);
        passwordLogin = (EditText) this.findViewById(R.id.ETpasswordLogin);
        emailSignup = (EditText) this.findViewById(R.id.ETemailSignup);
        passwordSignup = (EditText) this.findViewById(R.id.ETpasswordSignup);
        login = (Button) this.findViewById(R.id.BTlogin);
        signup = (Button) this.findViewById(R.id.BTsignup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailLogin.getText().toString();
                final String password = passwordLogin.getText().toString();
                mainReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        login(email, password, authData);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        information.setText("\n" + firebaseError.getMessage());
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
                    public void onSuccess()
                    {
                        information.setText("\nSuccesfully Created User");
                    }

                    @Override
                    public void onError(FirebaseError firebaseError)
                    {
                        information.setText("\n"+firebaseError.getMessage());
                    }
                });
            }
        });
    }

    public void login(final String email, final String password, final AuthData authData)
    {
        usersList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    User user = usersSnapshot.getValue(User.class);
                    if (user.getUID().equals(authData.getUid()))
                    {
                        exists = true;
                        config.setMainReference(usersList.child(user.getKey()));
                    }
                }
                Log.w("LOG: ", String.valueOf(exists));
                if (!exists) {
                    Firebase userF = usersList.push();
                    User newUser = new User();
                    newUser.setKey(userF.getKey());
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setUID(authData.getUid());
                    userF.setValue(newUser);
                    config.setMainReference(usersList.child(userF.getKey()));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        Intent openApp = new Intent(this, MainActivity.class);
        startActivity(openApp);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        exists=false;
        emailLogin.setText("48089748z@iespoblenou.org");
        emailSignup.setHint("Email");
        if (config.getLanguage().equals("SPANISH"))
        {
            setSpanish();
        } else {setEnglish();}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_english)
        {
            config.setLanguage("ENGLISH");
            setEnglish();
            return true;
        }
        if (id == R.id.action_spanish)
        {
            config.setLanguage("SPANISH");
            setSpanish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSpanish()
    {
        toolbar.setTitle("ACCEDE O REGISTRATE");
        information.setText("");
        login.setText("Accede");
        signup.setText("Registrate");
        passwordLogin.setHint("Contraseña");
        passwordSignup.setHint("Contraseña");
    }
    public void setEnglish()
    {
        toolbar.setTitle("SIGN UP OR LOGIN");
        information.setText("");
        login.setText("LOGIN");
        signup.setText("SIGN UP");
        passwordLogin.setHint("Password");
        passwordSignup.setHint("Password");
    }
}

