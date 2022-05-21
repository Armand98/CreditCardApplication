package com.example.creditcartapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewCardActivity extends AppCompatActivity {

    EditText tvBankName, tvFirstName, tvLastName, tvCardNumber, tvValidThru, tvCVC;
    Button cancelBtn, addBtn;
    CardDatabase cardDB;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_new_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        cardDB = new CardDatabase(this, username);

        tvBankName = findViewById(R.id.cardNewBankName);
        tvFirstName = findViewById(R.id.cardNewFirstName);
        tvLastName = findViewById(R.id.cardNewLastName);
        tvCardNumber = findViewById(R.id.cardNewNumber);
        tvValidThru = findViewById(R.id.cardNewValidThru);
        tvCVC = findViewById(R.id.cardNewCVC);
        cancelBtn = findViewById(R.id.cardNewCancelBtn);
        addBtn = findViewById(R.id.cardNewAddBtn);

        cancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        addBtn.setOnClickListener(v -> {

            String strBankName = tvBankName.getText().toString();
            String strFirstName = tvFirstName.getText().toString();
            String strLastName = tvLastName.getText().toString();
            String strCardNumber = tvCardNumber.getText().toString();
            String strValidThru = tvValidThru.getText().toString();
            String strCVC = tvCVC.getText().toString();

            if(TextUtils.isEmpty(strBankName) || TextUtils.isEmpty(strFirstName) ||
                    TextUtils.isEmpty(strLastName) || TextUtils.isEmpty(strCardNumber) ||
                    TextUtils.isEmpty(strValidThru) || TextUtils.isEmpty(strCVC)) {

                Toast.makeText(AddNewCardActivity.this,
                        "Uzupełnij wszystkie pola formularza", Toast.LENGTH_SHORT).show();
            } else {
                if(strCardNumber.replace(" ", "").length() != 16) {
                    Toast.makeText(AddNewCardActivity.this, "Błędny numer karty",
                            Toast.LENGTH_SHORT).show();
                } else if (strValidThru.replace("/", "").length() != 4) {
                    Toast.makeText(AddNewCardActivity.this, "Błędna data",
                            Toast.LENGTH_SHORT).show();
                } else if(strCVC.length() != 3) {
                    Toast.makeText(AddNewCardActivity.this, "Błędny numer CVC",
                            Toast.LENGTH_SHORT).show();
                } else if(cardDB.doesCardExist(strCardNumber)) {
                    Toast.makeText(AddNewCardActivity.this, "Podana karta już istnieje.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    cardDB.insertCardsData(strBankName,
                            strFirstName,
                            strLastName,
                            strCardNumber,
                            strValidThru,
                            Integer.parseInt(strCVC));

                    Toast.makeText(AddNewCardActivity.this, "Dodano nową kartę",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });

        tvCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = tvCardNumber.getText().toString();
                int textLength = text.length();

                if(textLength == 20) {
                    tvCardNumber.setText(new StringBuilder(text.substring(0,19)));
                    tvCardNumber.setSelection(tvCardNumber.getText().length());
                    Toast.makeText(AddNewCardActivity.this, "Numer karty posiada już 16 znaków", Toast.LENGTH_SHORT).show();
                }

                if(text.endsWith(" "))
                    return;

                if(textLength == 5 || textLength == 10 || textLength == 15) {
                    tvCardNumber.setText(new StringBuilder(text).insert(text.length()-1, " ").toString());
                    tvCardNumber.setSelection(tvCardNumber.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvValidThru.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = tvValidThru.getText().toString();
                int textLength = text.length();

                if(textLength == 6) {
                    tvValidThru.setText(new StringBuilder(text.substring(0,5)));
                    tvValidThru.setSelection(tvValidThru.getText().length());
                }

                if(text.endsWith("/"))
                    return;

                if(textLength == 3) {
                    tvValidThru.setText(new StringBuilder(text).insert(text.length()-1,
                            "/").toString());
                    tvValidThru.setSelection(tvValidThru.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvCVC.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = tvCVC.getText().toString();
                int textLength = text.length();

                if(textLength == 4) {
                    tvCVC.setText(new StringBuilder(text.substring(0,3)));
                    tvCVC.setSelection(tvCVC.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}