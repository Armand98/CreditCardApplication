package com.example.creditcartapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, repassword;
    Button signup, login;
    UserDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.createAnAccountBtn);
        DB = new UserDatabase(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_string = username.getText().toString();
                String password_string = password.getText().toString();
                String rePassword_string = repassword.getText().toString();

                if(TextUtils.isEmpty(username_string) ||
                        TextUtils.isEmpty(password_string) ||
                        TextUtils.isEmpty(rePassword_string))
                    Toast.makeText(RegisterActivity.this, "Wypełnij poprawnie formularz",
                            Toast.LENGTH_SHORT).show();
                else {
                    if(password_string.equals(rePassword_string)) {
                        Boolean checkUser = DB.checkUsername(username_string);
                        if (checkUser == false) {
                            Boolean insert = DB.insertUserData(username_string, password_string);
                            if(insert == true) {
                                Toast.makeText(RegisterActivity.this,
                                        "Rejestracja pomyślna - zaloguj się",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),
                                        LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Wystąpił błąd podczas rejestracji",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Ten użytkownik już istnieje",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Podane hasła nie zgadzają się",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}