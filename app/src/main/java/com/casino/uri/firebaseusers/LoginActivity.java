package com.casino.uri.firebaseusers;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
public class LoginActivity extends AppCompatActivity
{
    ProgressDialog progress;
    Boolean userIsSavedOnFireBase = false;
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<String> existingUsers = new ArrayList<>();
    ArrayAdapter<String> adapter;

   // ListView autocomplete;
    Button login;
    Button signup;
    Toolbar toolbar;
    EditText emailLogin;
    EditText passwordLogin;
    EditText emailSignup;
    EditText passwordSignup;

    FirebaseConfig config;
    Firebase mainReference;
    Firebase usersListReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        config = (FirebaseConfig) getApplication();
        mainReference = config.getMainReference();
        usersListReference = config.getUsersListReference();
        toolbar.setTitle("LOGIN OR SIGN UP");
        setSupportActionBar(toolbar);
        emailLogin = (EditText) this.findViewById(R.id.ETemailLogin);
        passwordLogin = (EditText) this.findViewById(R.id.ETpasswordLogin);
        emailSignup = (EditText) this.findViewById(R.id.ETemailSignup);
        passwordSignup = (EditText) this.findViewById(R.id.ETpasswordSignup);
        login = (Button) this.findViewById(R.id.BTlogin);
        signup = (Button) this.findViewById(R.id.BTsignup);

        adapter = new ArrayAdapter<>(this, 0, existingUsers);
        progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progress.setTitle("           LOGGING IN");
        progress.setMessage("Please wait a few seconds...");
        progress.setCancelable(false);
        usersListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                usersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    User user = userSnapshot.getValue(User.class);
                    existingUsers.add(user.getEmail());
                    usersList.add(user);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
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
                        showInformation("     DONE", "Succesfully Created User.");
                    }
                    @Override
                    public void onError(FirebaseError firebaseError)
                    {
                        showAlert("    ERROR", firebaseError.getMessage());
                    }
                });
            }
        });
    }
    public void login()
    {
        final String email = emailLogin.getText().toString();
        final String password = passwordLogin.getText().toString();
        mainReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData)
            {
                progress.show();
                for (int x = 0; x < usersList.size(); x++) //PARA CADA USUARIO GUARDADO MIRAMOS SI SU UID EXISTE
                {
                    if (usersList.get(x).getUID().equals(authData.getUid())) //SI ESA UID EXISTE, ESE USUARIO EXISTE
                    {
                        config.setLoggedUserReference(usersListReference.child(usersList.get(x).getKey())); //POR LO TANTO COMO HA SIDO AUTENTICADO LE DAMOS LA NUEVA REF DE FIREBASE
                        userIsSavedOnFireBase = true;
                        break;         // Y BREAK
                    }
                    else
                    {
                        userIsSavedOnFireBase = false; //SI NO EXISTE ESTO ES FALSE Y ENTRARA EN EL IF DE ABAJO
                    }
                }
                if (!userIsSavedOnFireBase) //ESTE IF DEBERIA SIMPLEMENTE IR DENTRO DE EL ELSE DE ARRIBA PERO NO VA BIEN HACER ESO XQ SE EJECUTA EN SEGUNDO PLANO LO DE ARRIBA
                {
                    Firebase userF = usersListReference.push();
                    User newUser = new User();
                    newUser.setKey(userF.getKey());
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setUID(authData.getUid());
                    userF.setValue(newUser);
                    config.setLoggedUserReference(usersListReference.child(userF.getKey()));
                }
                startMainActivity();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError)
            {
                showAlert("    ERROR", firebaseError.getMessage());
            }
        });
    }
    public void startMainActivity()
    {
        Intent startMainActivity = new Intent(this, MainActivity.class);
        startActivity(startMainActivity);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        //autocomplete.setVisibility(View.INVISIBLE);
        progress.hide();
        emailLogin.setHint("Email");
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
            emailLogin.setText("48089748z@iespoblenou.org"); //THIS IS FOR TESTING, MUST BE REMOVED
            passwordLogin.setText("password");               //THIS IS FOR TESTING, MUST BE REMOVED
            config.setLanguage("ENGLISH");
            setEnglish();
            return true;
        }
        if (id == R.id.action_spanish)
        {
            emailLogin.setText("oriolcunado@gmail.com");     //THIS IS FOR TESTING, MUST BE REMOVED
            passwordLogin.setText("password");               //THIS IS FOR TESTING, MUST BE REMOVED
            config.setLanguage("SPANISH");
            setSpanish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setSpanish()
    {
        toolbar.setTitle("ACCEDE O REGISTRATE");
        login.setText("Accede");
        signup.setText("Registrate");
        passwordLogin.setHint("Contraseña");
        passwordSignup.setHint("Contraseña");
    }
    public void setEnglish()
    {
        toolbar.setTitle("SIGN UP OR LOGIN");
        login.setText("LOGIN");
        signup.setText("SIGN UP");
        passwordLogin.setHint("Password");
        passwordSignup.setHint("Password");
    }
    public void showAlert(String title, String message)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {}
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }
    public void showInformation(String title, String message)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {}
                })
                .setIcon(R.drawable.ic_information)
                .show();
    }
}

