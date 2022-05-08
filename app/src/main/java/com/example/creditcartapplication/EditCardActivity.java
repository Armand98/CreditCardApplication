package com.example.creditcartapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditCardActivity extends AppCompatActivity {

    EditText tvBankName, tvFirstName, tvLastName, tvCardNumber, tvValidThru, tvCVC;
    Button deleteBtn, saveBtn;
    CardDatabase cardDB;
    String strBankName, strFirstName, strLastName, strCardNumber, strValidThru, strCVC;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            strBankName = extras.getString("strBankName");
            strFirstName = extras.getString("strFirstName");
            strLastName = extras.getString("strLastName");
            strCardNumber = extras.getString("strCardNumber");
            strValidThru = extras.getString("strValidThru");
            strCVC = extras.getString("strCVC");
            username = extras.getString("username");
        }

        cardDB = new CardDatabase(this, username);

        tvBankName = findViewById(R.id.cardEditBankName);
        tvFirstName = findViewById(R.id.cardEditFirstName);
        tvLastName = findViewById(R.id.cardEditLastName);
        tvCardNumber = findViewById(R.id.cardEditNumber);
        tvValidThru = findViewById(R.id.cardEditValidThru);
        tvCVC = findViewById(R.id.cardEditCVC);
        deleteBtn = findViewById(R.id.cardEditDeleteBtn);
        saveBtn = findViewById(R.id.cardEditSaveBtn);

        tvBankName.setText(strBankName);
        tvFirstName.setText(strFirstName);
        tvLastName.setText(strLastName);
        tvCardNumber.setText(strCardNumber);
        tvCardNumber.setEnabled(false);
        tvValidThru.setText(strValidThru);
        tvCVC.setText(strCVC);

        deleteBtn.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(cardDB.deleteCard(strCardNumber)) {
                            Toast.makeText(EditCardActivity.this,
                                    "Poprawnie usunięto kartę.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditCardActivity.this);
            alertDialogBuilder.setMessage("Na pewno chcesz usunąć wybraną kartę?").
                    setPositiveButton("Tak", dialogClickListener).
                    setNegativeButton("Nie", dialogClickListener).show();
        });

        saveBtn.setOnClickListener(v -> {

            String strNewBankName = tvBankName.getText().toString();
            String strNewFirstName = tvFirstName.getText().toString();
            String strNewLastName = tvLastName.getText().toString();
            String strNewCardNumber = tvCardNumber.getText().toString();
            String strNewValidThru = tvValidThru.getText().toString();
            String strNewCVC = tvCVC.getText().toString();

            if(TextUtils.isEmpty(strNewBankName) || TextUtils.isEmpty(strNewFirstName) ||
                    TextUtils.isEmpty(strNewLastName) || TextUtils.isEmpty(strNewCardNumber) ||
                    TextUtils.isEmpty(strNewValidThru) || TextUtils.isEmpty(strNewCVC)) {

                Toast.makeText(EditCardActivity.this,
                        "Uzupełnij wszystkie pola formularza", Toast.LENGTH_SHORT).show();
            } else {
                if (strCardNumber.replace(" ", "").length() != 16) {
                    Toast.makeText(EditCardActivity.this, "Błędny numer karty",
                            Toast.LENGTH_SHORT).show();
                } else if (strValidThru.replace("/", "").length() != 4) {
                    Toast.makeText(EditCardActivity.this, "Błędna data",
                            Toast.LENGTH_SHORT).show();
                } else if (strCVC.length() != 3) {
                    Toast.makeText(EditCardActivity.this, "Błędny numer CVC",
                            Toast.LENGTH_SHORT).show();
                } else if (cardDB.updateCardData(strNewBankName, strNewFirstName, strNewLastName,
                        strNewCardNumber, strNewValidThru, Integer.parseInt(strNewCVC))) {
                    Toast.makeText(EditCardActivity.this, "Poprawnie zaktualizowano dane",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
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