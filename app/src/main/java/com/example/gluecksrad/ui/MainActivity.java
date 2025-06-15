package com.example.gluecksrad.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FrameLayout wheelContainer;
    private Button buttonSpin;
    private TextView textViewScore;

    private TextView[] fields;

    private float currentRotation = 0f;
    private int score = 0;

    private Random random = new Random();

    // Deine Werte für die 8 Felder (positiv/negativ)
    private final int[] spinValues = {+10, +20, +30, +40, -10, -20, -30, -40};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelContainer = findViewById(R.id.wheelContainer);
        buttonSpin = findViewById(R.id.buttonSpin);
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

        updateScoreText();

        wheelContainer.post(this::positionNumbersOnCircle);

        buttonSpin.setOnClickListener(v -> spinWheel());
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

                int points = spinValues[landedSector];
                score += points;
                if (score < 0) score = 0;

                updateScoreText();

                Toast.makeText(MainActivity.this, (points >= 0 ? "+" : "") + points + " Punkte!", Toast.LENGTH_SHORT).show();
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

        int sectorIndex = (int)(angleUnderPointer / degreesPerSector);

        return sectorIndex;
    }

    private void updateScoreText() {
        textViewScore.setText("Score: " + score);
    }
}
