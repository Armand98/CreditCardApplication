package com.example.creditcartapplication;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView cardsListView;
    TextView welcomeTextView;
    ArrayList<Card> cardList;
    CardListAdapter cardListAdapter;
    String username;
    CardDatabase cardDB;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int APP_STORAGE_ACCESS_REQUEST_CODE = 101;

    private Boolean manageExternalStoragePermission = false;

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
                    System.out.println("Mamy uprawnienia MANAGE! 222");
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
                Toast.makeText(HomeActivity.this, "Odmówiono uprawnień write",
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addNewCardMenuBtn: {
                Intent intent = new Intent(getApplicationContext(), AddNewCardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } return true;

            case R.id.importMenuBtn: {
                cardDB.importCardDatabase(username);
                updateCards();
            } return true;

            case R.id.exportMenuBtn: {
                if(checkPermission()) {
                    if(cardDB.exportCardDatabase(username)) {
                        Toast.makeText(HomeActivity.this, "Export do folderu Downloads - ukończony",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Odmowa dostępu do pamięci",
                            Toast.LENGTH_SHORT).show();
                }
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
        setContentView(R.layout.activity_home);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            username = extras.getString("username");
        }

        welcomeTextView = findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Witaj " + username);
        cardsListView = findViewById(R.id.cardsList);
        cardList = new ArrayList<>();

        cardDB = new CardDatabase(this, username);
        updateCards();
    }

    public void updateCards() {
        cardList = cardDB.readCardsFromDB();

        cardListAdapter = new CardListAdapter(this, R.layout.adapter_view_layout, cardList);
        cardsListView.setAdapter(cardListAdapter);

        cardsListView.setOnItemClickListener((parent, view, position, id) -> {
            Card selectedCard = cardList.get(position);
            Intent intent = new Intent(getApplicationContext(), EditCardActivity.class);
            intent.putExtra("strBankName", selectedCard.getBankName());
            intent.putExtra("strFirstName", selectedCard.getFirstName());
            intent.putExtra("strLastName", selectedCard.getLastName());
            intent.putExtra("strCardNumber", selectedCard.getCardNumber());
            intent.putExtra("strValidThru", selectedCard.getValidThru());
            intent.putExtra("strCVC", Integer.toString(selectedCard.getCVC()));
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}