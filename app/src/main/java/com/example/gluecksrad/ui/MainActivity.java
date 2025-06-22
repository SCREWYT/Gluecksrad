package com.example.gluecksrad.ui;

import android.animation.Animator; // Für Animationen, z. B. Drehen des Glücksrads
import android.animation.ObjectAnimator; // Führt die eigentliche Animation aus
import android.content.Intent; // Um zwischen Screens zu wechseln
import android.database.sqlite.SQLiteDatabase; // Für Zugriff auf SQLite-Datenbank
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout; // Für das Layout, das das Glücksrad enthält
import android.widget.TextView;
import android.widget.Toast; // Für kleine Info-Fenster (z. B. "Punkte erhalten!")

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;
import com.example.gluecksrad.database.DBHelper;
import com.example.gluecksrad.database.ScoreDao;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI-Elemente
    private FrameLayout wheelContainer;
    private Button buttonSpin, buttonSettings, buttonUpgrades;
    private TextView textViewScore;

    // Array von TextView Elementen für die Punktzahlen. Es heißt "fields"
    private TextView[] fields;

    // Aktueller Drehwinkel des Glücksrads
    private float currentRotation = 0f;

    // Punktestand und Upgradewerte
    private int score = 0;
    private float multiplier = 1.0f; // f = float = Gleitkommazahl/Dezimalzahl

    // Für jede Zufallsaktion später
    private Random random = new Random();
    private String username;
    private ScoreDao scoreDao;

    // Keys für die Upgrades in der DB
    private final String MULTIPLIER_KEY = "multiplier";
    private final String FIELDVALUE_KEY = "field_value";

    // Ursprungswerte für die Glücksrad-Felder (8 Felder, je 4 positiv/negativ)
    private final int[] baseSpinValues = {+10, -10, +20, -20, +30, -30, +40, -40};

    // Aktuelle Zahlenwerte auf dem Glücksrad je nach Upgrade
    private final int[] spinValues = new int[8];

    // OnCreate wird immer automatisch aufgerufen bei Screenwechsel oder Start.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Username aus vorherigem Screen übernehmen. Bei keinem Namen = "testuser"
        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser"; // Fallback

        // DB-Verbindung aufbauen und Punktestand laden
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        scoreDao = new ScoreDao(db);
        score = scoreDao.getScore(username);

        // UI-Elemente verbinden
        wheelContainer = findViewById(R.id.wheelContainer);
        buttonSpin = findViewById(R.id.buttonSpin);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonUpgrades = findViewById(R.id.buttonUpgrades);
        textViewScore = findViewById(R.id.textViewScore);

        // Nachdem man den Code mit der XML verbunden hat pflegt man die Felder in das Array "fields" ein
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

        // Felder an Upgrade anpassen
        updateFieldValues();

        // Punktestand anzeigen
        updateScoreText();

        // Zahlen werden visuell auf der Drehscheibe platziert
        wheelContainer.post(this::positionNumbersOnCircle);

        // Klick-Events
        buttonSpin.setOnClickListener(v -> spinWheel());

        // Hier wechselt man bei Klick auf den Button "Settings" zu "Settings"
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
        // Hier wechselt man bei Klick auf den Button "Upgrade" zu dem "Upgrade" Screen
        buttonUpgrades.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpgradeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    // Passt alle Feldwerte anhand des Upgrades an (±5 pro Level)
    private void updateFieldValues() {
        int fieldLevel = scoreDao.getUpgradeLevel(username, FIELDVALUE_KEY);
        for (int i = 0; i < baseSpinValues.length; i++) {
            if (baseSpinValues[i] > 0) {
                spinValues[i] = baseSpinValues[i] + (fieldLevel * 5); // Positiv wird positiver
            } else {
                spinValues[i] = baseSpinValues[i] - (fieldLevel * 5); // Negativ wird negativer
            }
        }
    }


    // Ohne KI nicht ganz hinbekommen: https://chatgpt.com/share/6857fd20-43cc-800b-800a-a34dac4b47b6
    // Platziert alle Textfelder gleichmäßig im Kreis
    private void positionNumbersOnCircle() {
        int radius = wheelContainer.getWidth() / 2 - 150;
        int centerX = wheelContainer.getWidth() / 2;
        int centerY = wheelContainer.getHeight() / 2;

        for (int i = 0; i < fields.length; i++) {
            double angle = 2 * Math.PI * i / fields.length - Math.PI / 2;
            float x = (float) (centerX + radius * Math.cos(angle) - fields[i].getWidth() / 2f);
            float y = (float) (centerY + radius * Math.sin(angle) - fields[i].getHeight() / 2f);
            fields[i].setX(x);
            fields[i].setY(y);
            fields[i].setText(String.valueOf(spinValues[i]));
        }
    }

    // Führt die Glücksrad-Drehung und Punktauswertung durch
    private void spinWheel() {
        int sectorCount = spinValues.length;                    // Array mit den Feldern
        int randomSector = random.nextInt(sectorCount);        // Wählt zufällig eine Zahl
        float degreesPerSector = 360f / sectorCount;          // 8 Felder, jedes Feld also 45 Grad

        float rotations = 4f; // 4 volle Umdrehungen. Zahl steuert, wie spannend die Umdrehung aussieht.
        // Also 4x dreht sich das Rad immer + ein bisschen mehr. Random Sector (z. B. 1 oder 2 und das dann multipliziert mit dem Gradwert eines Feldes 45.
        // Also dreht er sich bei Feld 2 nochmal um 90 Grad quasi, bei Feld 3 um 135 Grad und so weiter.
        float targetRotation = currentRotation + rotations * 360f + randomSector * degreesPerSector + degreesPerSector / 2f;

                    // currentRotation = Dort steht das Rad gerade
                   // rotations 4*360 = 4 volle Umdrehungen
                  // randomSector * degreesPerSector = bringt das richtige Feld oben hin
                 // degreesPerSector / 2f = Zentriert das Feld, was gewählt wird, genau beim Zeiger

        // ObjectAnimator = Android interne Klasse, die Animationen abspielen kann. Hier dreht es den WheelContainer
        ObjectAnimator animator = ObjectAnimator.ofFloat(wheelContainer, "rotation", currentRotation, targetRotation);

        // Animation dauert 3 Sekunden
        animator.setDuration(3000);

        // Das hier sorgt dafür, dass sich die Scheibe zum Ende hin langsamer dreht.
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());

        //Hier wird geregelt, was während der Animation bzw. danach passieren soll.
        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                buttonSpin.setEnabled(false); // Während der Drehung wird der "Drehen" Button deaktiviert
            }

            @Override public void onAnimationEnd(Animator animation) {
                buttonSpin.setEnabled(true); // Hier wird der Button wieder aktiviert

                // Das stellt sicher, dass das Rad für die nächste Drehung richtig steht.
                currentRotation = targetRotation % 360f;
                if (currentRotation < 0) currentRotation += 360f;

                // Hier wird entschieden, welches Feld dann getroffen wird.
                int landedSector = getSectorUnderPointer(currentRotation, sectorCount);
                // Holt sich hier den Zahlenwert auf dem getroffenen Feld
                int basePoints = spinValues[landedSector];

                // Der aktuelle Multiplikator des Spielers. Basiswert ist 1.0, mit jedem Kauf steigt er um 0.1 und nur positive Felder werden beeinflusst.
                float multiplierValue = 1.0f + 0.1f * scoreDao.getUpgradeLevel(username, MULTIPLIER_KEY);

                // Math.round rundet Ergebnis auf eine glatte Zahl. Im Score hat man nämlich nie Nachkommastellen.
                int gained = (basePoints > 0)
                        ? Math.round(basePoints * multiplierValue)
                        : basePoints;

                // Punkte werden zum Score addiert.
                score += gained;
                // Hier wird sichergestellt, dass der Score nie negativ werden kann. Sobald er es wird, wird er wieder auf 0 gesetzt.
                if (score < 0) score = 0;

                // Updated den angezeigten Score auf dem Screen & speichert ihn direkt in der Datenbank.
                updateScoreText();
                scoreDao.updateScore(username, score);

                // Über den aufloppenden Text wird angezeigt, was passiert ist. (Punkte bekommen/verloren etc.)
                Toast.makeText(MainActivity.this, (gained >= 0 ? "+" : "") + gained + " Punkte!", Toast.LENGTH_SHORT).show();
            }

            // Startet am Ende final die Animation
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
    }

    // Mit KI nachgeholfen: https://chatgpt.com/share/6857fc4a-f364-800b-b0f8-504c819daff1
    // Berechnet, welches Feld bei einem bestimmten Winkel oben liegt
    private int getSectorUnderPointer(float wheelRotation, int sectorCount) {
        final float pointerAngle = 180f; // Oben in der Mitte
        float normalizedRotation = wheelRotation % 360f;
        if (normalizedRotation < 0) normalizedRotation += 360f;

        float angleUnderPointer = (pointerAngle - normalizedRotation + 360f) % 360f;
        float degreesPerSector = 360f / sectorCount;

        return (int)(angleUnderPointer / degreesPerSector);
    }

    // Aktualisiert angezeigten Text für Punktestand im Spiel
    private void updateScoreText() {
        textViewScore.setText("Score: " + score);
    }
}
