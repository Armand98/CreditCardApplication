package com.example.creditcartapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button signIn, createAnAccount;
    UserDatabase DB;
    CheckBox rememberLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        signIn = findViewById(R.id.loginBtn);
        createAnAccount = findViewById(R.id.createAnAccountBtn);
        rememberLogin = findViewById(R.id.rememberme);
        DB = new UserDatabase(this);

        AtomicReference<SharedPreferences> preferences = new AtomicReference<>(
                getSharedPreferences("checkbox", MODE_PRIVATE));
        String checkboxSavedValue = preferences.get().getString("remember", "");
        String checkboxSavedUsername = preferences.get().getString("username", "");

        if(checkboxSavedValue.equals("true")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("username", checkboxSavedUsername);
            startActivity(intent);
        } else if (checkboxSavedValue.equals("false")){
            Toast.makeText(LoginActivity.this, "Zaloguj się lub załóż nowe konto",
                    Toast.LENGTH_SHORT).show();
        }

        signIn.setOnClickListener(view -> {
            String username_string = username.getText().toString();
            String password_string = password.getText().toString();

            if(TextUtils.isEmpty(username_string) || TextUtils.isEmpty(password_string))
                Toast.makeText(LoginActivity.this, "Uzupełnij formularz logowania",
                        Toast.LENGTH_SHORT).show();
            else {
                Boolean checkUserPassword = DB.checkUsernameAndPassword(username_string,
                        password_string);
                if (checkUserPassword) {
                    Toast.makeText(LoginActivity.this, "Udało się zalogować :)",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("username", username_string);
                    intent.putExtra("password", password_string);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Logowanie nie powiodło się :(",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        rememberLogin.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()) {
                preferences.set(getSharedPreferences("checkbox", MODE_PRIVATE));
                SharedPreferences.Editor editor = preferences.get().edit();
                editor.putString("remember", "true");
                editor.putString("username", username.getText().toString());
                editor.apply();
                Toast.makeText(LoginActivity.this, "Zaznaczono",
                        Toast.LENGTH_SHORT).show();
            } else {
                preferences.set(getSharedPreferences("checkbox", MODE_PRIVATE));
                SharedPreferences.Editor editor = preferences.get().edit();
                editor.putString("remember", "false");
                editor.putString("username", "");
                editor.apply();
                Toast.makeText(LoginActivity.this, "Odznaczono",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}