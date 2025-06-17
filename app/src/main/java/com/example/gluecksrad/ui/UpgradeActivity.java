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

    private Button buttonUpgradePassive, buttonUpgradeMultiplier, buttonBackToMain;
    private TextView textViewUpgradeStatus;

    private String username;
    private ScoreDao scoreDao;

    private static final String UPGRADE_PASSIVE = "passive_income";
    private static final String UPGRADE_MULTIPLIER = "multiplier";

    private static final int BASE_COST_PASSIVE = 50;
    private static final int BASE_COST_MULTIPLIER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser";

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        scoreDao = new ScoreDao(db);

        buttonUpgradePassive = findViewById(R.id.buttonUpgradePassive);
        buttonUpgradeMultiplier = findViewById(R.id.buttonUpgradeMultiplier);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        textViewUpgradeStatus = findViewById(R.id.textViewUpgradeStatus);

        updateUpgradeButtons();

        buttonUpgradePassive.setOnClickListener(v -> {
            int currentLevel = scoreDao.getUpgradeLevel(username, UPGRADE_PASSIVE);
            int cost = (int) Math.ceil(BASE_COST_PASSIVE * Math.pow(1.2, currentLevel));
            int currentScore = scoreDao.getScore(username);

            if (currentScore >= cost) {
                scoreDao.updateScore(username, currentScore - cost);
                scoreDao.setUpgradeLevel(username, UPGRADE_PASSIVE, currentLevel + 1);
                Toast.makeText(this, "Passiv-Upgrade gekauft!", Toast.LENGTH_SHORT).show();
                updateUpgradeButtons();
            } else {
                Toast.makeText(this, "Nicht genug Punkte!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonUpgradeMultiplier.setOnClickListener(v -> {
            int currentLevel = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER);
            int cost = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.2, currentLevel));
            int currentScore = scoreDao.getScore(username);

            if (currentScore >= cost) {
                scoreDao.updateScore(username, currentScore - cost);
                scoreDao.setUpgradeLevel(username, UPGRADE_MULTIPLIER, currentLevel + 1);
                Toast.makeText(this, "Multiplikator-Upgrade gekauft!", Toast.LENGTH_SHORT).show();
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
        int passiveLevel = scoreDao.getUpgradeLevel(username, UPGRADE_PASSIVE);
        int multiplierLevel = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER);
        int score = scoreDao.getScore(username);

        int costPassive = (int) Math.ceil(BASE_COST_PASSIVE * Math.pow(1.2, passiveLevel));
        int costMultiplier = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.2, multiplierLevel));

        buttonUpgradePassive.setText("Passiv: +" + (passiveLevel + 1) + " alle 10 Sek\nKosten: " + costPassive);
        buttonUpgradeMultiplier.setText("Multiplikator: x" +
                String.format("%.1f", 1 + multiplierLevel * 0.1) +
                " → x" + String.format("%.1f", 1 + (multiplierLevel + 1) * 0.1) +
                "\nKosten: " + costMultiplier);

        textViewUpgradeStatus.setText("Dein Punktestand: " + score);
    }
}
