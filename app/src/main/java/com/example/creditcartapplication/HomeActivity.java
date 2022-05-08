package com.example.creditcartapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
    private static final int MANAGE_PERMISSION_CODE = 101;

    private Boolean writeExternalStoragePermission = false;
    private Boolean manageExternalStoragePermission = false;

    public Boolean checkPermission()
    {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    STORAGE_PERMISSION_CODE);
        } else {
            writeExternalStoragePermission = true;
        }

        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[] { Manifest.permission.MANAGE_EXTERNAL_STORAGE },
                    MANAGE_PERMISSION_CODE);
        } else {
            manageExternalStoragePermission = true;
        }

        return (writeExternalStoragePermission && manageExternalStoragePermission);
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
        } else if (requestCode == MANAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(HomeActivity.this, "Przyznano uprawnienia",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(HomeActivity.this, "Odmówiono uprawnień manage",
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addNewCardMenuBtn: {
                Intent intent = new Intent(getApplicationContext(), AddNewCardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } return true;

            case R.id.importMenuBtn: {

            } return true;

            case R.id.exportMenuBtn: {
                if(checkPermission()) {
                    cardDB.exportCardDatabase();
                } else {
                    Toast.makeText(HomeActivity.this, "Odmowa dostępu do pamięci",
                            Toast.LENGTH_SHORT).show();
                }
                cardDB.exportCardDatabase();
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