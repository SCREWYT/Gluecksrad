package com.example.gluecksrad.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;
import com.example.gluecksrad.database.DBHelper;
import com.example.gluecksrad.database.ScoreDao;

public class UpgradeActivity extends AppCompatActivity {

    private Button buttonUpgradeMultiplier, buttonUpgradeFieldValue, buttonBackToMain;
    private TextView textViewUpgradeStatus;

    private String username;
    private ScoreDao scoreDao;

    private static final String UPGRADE_MULTIPLIER = "multiplier";
    private static final String UPGRADE_FIELDVALUE = "field_value";

    private static final int BASE_COST_MULTIPLIER = 5;
    private static final int BASE_COST_FIELDVALUE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser";

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        scoreDao = new ScoreDao(db);

        buttonUpgradeMultiplier = findViewById(R.id.buttonUpgradeMultiplier);
        buttonUpgradeFieldValue = findViewById(R.id.buttonUpgradeFieldValue);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        textViewUpgradeStatus = findViewById(R.id.textViewUpgradeStatus);

        updateUpgradeButtons();

        buttonUpgradeMultiplier.setOnClickListener(v -> {
            int level = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER);
            int cost = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.1, level));
            int currentScore = scoreDao.getScore(username);

            if (currentScore >= cost) {
                scoreDao.updateScore(username, currentScore - cost);
                scoreDao.setUpgradeLevel(username, UPGRADE_MULTIPLIER, level + 1);
                Toast.makeText(this, "Multiplikator-Upgrade gekauft!", Toast.LENGTH_SHORT).show();
                updateUpgradeButtons();
            } else {
                Toast.makeText(this, "Nicht genug Punkte!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonUpgradeFieldValue.setOnClickListener(v -> {
            int level = scoreDao.getUpgradeLevel(username, UPGRADE_FIELDVALUE);
            int cost = (int) Math.ceil(BASE_COST_FIELDVALUE * Math.pow(1.1, level));
            int currentScore = scoreDao.getScore(username);

            if (currentScore >= cost) {
                scoreDao.updateScore(username, currentScore - cost);
                scoreDao.setUpgradeLevel(username, UPGRADE_FIELDVALUE, level + 1);
                Toast.makeText(this, "Feldwerte-Upgrade gekauft!", Toast.LENGTH_SHORT).show();
                updateUpgradeButtons();
            } else {
                Toast.makeText(this, "Nicht genug Punkte!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(UpgradeActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }

    private void updateUpgradeButtons() {
        int levelMultiplier = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER);
        int levelField = scoreDao.getUpgradeLevel(username, UPGRADE_FIELDVALUE);
        int score = scoreDao.getScore(username);

        int costMultiplier = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.1, levelMultiplier));
        int costField = (int) Math.ceil(BASE_COST_FIELDVALUE * Math.pow(1.1, levelField));

        buttonUpgradeMultiplier.setText("Multiplikator x" +
                String.format("%.1f", 1 + 0.1 * levelMultiplier) +
                " → x" + String.format("%.1f", 1 + 0.1 * (levelMultiplier + 1)) +
                "\nKosten: " + costMultiplier);

        buttonUpgradeFieldValue.setText("Feldwerte ±" + (levelField * 5) +
                " → ±" + ((levelField + 1) * 5) + "\nKosten: " + costField);

        textViewUpgradeStatus.setText("Dein Punktestand: " + score);
    }
}
