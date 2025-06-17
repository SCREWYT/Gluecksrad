package com.example.gluecksrad.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;
import com.example.gluecksrad.database.DBHelper;
import com.example.gluecksrad.database.ScoreDao;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FrameLayout wheelContainer;
    private Button buttonSpin, buttonSettings, buttonUpgrades;
    private TextView textViewScore;
    private TextView[] fields;

    private float currentRotation = 0f;
    private int score = 0;
    private float multiplier = 1.0f;

    private Random random = new Random();
    private String username;
    private ScoreDao scoreDao;

    private Handler handler = new Handler();
    private Runnable passiveRunnable;
    private final String PASSIVE_KEY = "passive_income";
    private final String MULTIPLIER_KEY = "multiplier";

    private final int[] spinValues = {+10, -10, +20, -20, +30, -30, +40, -40};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser";

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        scoreDao = new ScoreDao(db);
        score = scoreDao.getScore(username);

        wheelContainer = findViewById(R.id.wheelContainer);
        buttonSpin = findViewById(R.id.buttonSpin);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonUpgrades = findViewById(R.id.buttonUpgrades);
        textViewScore = findViewById(R.id.textViewScore);

        fields = new TextView[]{
                findViewById(R.id.field1),
                findViewById(R.id.field2),
                findViewById(R.id.field3),
                findViewById(R.id.field4),
                findViewById(R.id.field5),
                findViewById(R.id.field6),
                findViewById(R.id.field7),
                findViewById(R.id.field8)
        };

        multiplier = 1.0f + 0.1f * scoreDao.getUpgradeLevel(username, MULTIPLIER_KEY);

        updateScoreText();
        wheelContainer.post(this::positionNumbersOnCircle);

        buttonSpin.setOnClickListener(v -> spinWheel());

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        buttonUpgrades.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpgradeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        startPassiveScoreTimer();
    }

    private void startPassiveScoreTimer() {
        passiveRunnable = new Runnable() {
            @Override
            public void run() {
                int upgradeLevel = scoreDao.getUpgradeLevel(username, PASSIVE_KEY);
                if (upgradeLevel > 0) {
                    score += upgradeLevel;
                    scoreDao.updateScore(username, score);
                    updateScoreText();
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(passiveRunnable, 10000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(passiveRunnable);
    }

    private void positionNumbersOnCircle() {
        int radius = wheelContainer.getWidth() / 2 - 150;
        int centerX = wheelContainer.getWidth() / 2;
        int centerY = wheelContainer.getHeight() / 2;
        int count = fields.length;

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count - Math.PI / 2;
            float x = (float) (centerX + radius * Math.cos(angle) - fields[i].getWidth() / 2f);
            float y = (float) (centerY + radius * Math.sin(angle) - fields[i].getHeight() / 2f);
            fields[i].setX(x);
            fields[i].setY(y);
            fields[i].setText(String.valueOf(spinValues[i]));
        }
    }

    private void spinWheel() {
        int sectorCount = spinValues.length;
        int randomSector = random.nextInt(sectorCount);
        float degreesPerSector = 360f / sectorCount;

        float rotations = 4f;
        float targetRotation = currentRotation + rotations * 360f + randomSector * degreesPerSector + degreesPerSector / 2f;

        ObjectAnimator animator = ObjectAnimator.ofFloat(wheelContainer, "rotation", currentRotation, targetRotation);
        animator.setDuration(3000);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                buttonSpin.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                buttonSpin.setEnabled(true);
                currentRotation = targetRotation % 360f;
                if (currentRotation < 0) currentRotation += 360f;

                int landedSector = getSectorUnderPointer(currentRotation, sectorCount);
                int basePoints = spinValues[landedSector];

                // Multiplikator wirkt nur auf positive Punkte
                int gained = (basePoints > 0)
                        ? Math.round(basePoints * multiplier)
                        : basePoints;

                score += gained;
                if (score < 0) score = 0;

                updateScoreText();
                scoreDao.updateScore(username, score);

                Toast.makeText(MainActivity.this, (gained >= 0 ? "+" : "") + gained + " Punkte!", Toast.LENGTH_SHORT).show();
            }

            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
    }

    private int getSectorUnderPointer(float wheelRotation, int sectorCount) {
        final float pointerAngle = 180f;

        float normalizedRotation = wheelRotation % 360f;
        if (normalizedRotation < 0) normalizedRotation += 360f;

        float angleUnderPointer = (pointerAngle - normalizedRotation + 360f) % 360f;
        float degreesPerSector = 360f / sectorCount;

        return (int)(angleUnderPointer / degreesPerSector);
    }

    private void updateScoreText() {
        textViewScore.setText("Score: " + score);
    }
}
