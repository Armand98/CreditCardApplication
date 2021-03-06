package com.example.creditcartapplication;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView cardsListView;
    TextView welcomeTextView;
    ArrayList<Card> cardList;
    CardListAdapter cardListAdapter;
    String username, password;
    CardDatabase cardDB;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int APP_STORAGE_ACCESS_REQUEST_CODE = 101;
    private Boolean manageExternalStoragePermission = false;

    boolean sensitiveDataHidden = true;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean checkPermission()
    {
        if (!Environment.isExternalStorageManager()) {

            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
            }
            startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE);

        } else {
            System.out.println("Mamy uprawnienia MANAGE!");
            manageExternalStoragePermission = true;
        }

        return manageExternalStoragePermission;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == APP_STORAGE_ACCESS_REQUEST_CODE)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager())
                {
                    System.out.println("Mamy uprawnienia MANAGE!");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(HomeActivity.this, "Przyznano uprawnienia",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(HomeActivity.this, "Odm??wiono uprawnie?? write",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addNewCardMenuBtn: {
                Intent intent = new Intent(getApplicationContext(), AddNewCardActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            } return true;

            case R.id.importMenuBtn: {
                cardDB.importCardDatabase(username);
                updateCards();
            } return true;

            case R.id.exportMenuBtn: {
                if(checkPermission()) {
                    if(cardDB.exportCardDatabase(username)) {
                        Toast.makeText(HomeActivity.this, "Export do folderu Downloads - uko??czony",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Odmowa dost??pu do pami??ci",
                            Toast.LENGTH_SHORT).show();
                }
            } return true;

            case R.id.information: {
                Toast.makeText(HomeActivity.this, "Aby wy??wietli??\\ukry?? karte - kliknij. \n Aby edytowa?? - przytrzymaj.",
                        Toast.LENGTH_LONG).show();
            } return true;

            case R.id.logoutMenuBtn: {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.username = extras.getString("username");
            this.password = extras.getString("password");
        }

        welcomeTextView = findViewById(R.id.welcomeTextView);
        String welcomeText = "Witaj " + username;
        welcomeTextView.setText(welcomeText);
        cardsListView = findViewById(R.id.cardsList);
        cardList = new ArrayList<>();

        cardDB = new CardDatabase(this, this.username, this.password);
        updateCards();
    }

    public void updateCards() {
        cardList = cardDB.readCardsFromDB();

        cardListAdapter = new CardListAdapter(this, R.layout.adapter_view_layout, cardList,
                sensitiveDataHidden);
        cardsListView.setAdapter(cardListAdapter);

        cardsListView.setOnItemClickListener((adapterView, view, position, l) -> {
            if(sensitiveDataHidden) {
                view.findViewById(R.id.cardFirstName).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cardLastName).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cardValidThru).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cardCVC).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.cardFirstName).setVisibility(View.GONE);
                view.findViewById(R.id.cardLastName).setVisibility(View.GONE);
                view.findViewById(R.id.cardValidThru).setVisibility(View.GONE);
                view.findViewById(R.id.cardCVC).setVisibility(View.GONE);
            }
            sensitiveDataHidden = !sensitiveDataHidden;
        });


        cardsListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            Card selectedCard = cardList.get(position);
            Intent intent = new Intent(getApplicationContext(), EditCardActivity.class);
            intent.putExtra("strBankName", selectedCard.getBankName());
            intent.putExtra("strFirstName", selectedCard.getFirstName());
            intent.putExtra("strLastName", selectedCard.getLastName());
            intent.putExtra("strCardNumber", selectedCard.getCardNumber());
            intent.putExtra("strValidThru", selectedCard.getValidThru());
            intent.putExtra("strCVC", Integer.toString(selectedCard.getCVC()));
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivity(intent);
            return true;
        });
    }
}