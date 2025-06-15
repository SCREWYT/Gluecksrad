package com.example.gluecksrad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;
import com.example.gluecksrad.database.UserDAO;
import com.example.gluecksrad.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextPasswordConfirm;
    private Button buttonRegister, buttonBack;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBack = findViewById(R.id.buttonBack);

        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prüfen, ob Username schon existiert
            if (userDAO.getUserByUsername(username) != null) {
                Toast.makeText(RegisterActivity.this, "Benutzername bereits vergeben", Toast.LENGTH_SHORT).show();
                return;
            }

            // User speichern
            User newUser = new User(username, password);
            boolean inserted = userDAO.insertUser(newUser);
            if (inserted) {
                Toast.makeText(RegisterActivity.this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                // Zurück zum Login
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registrierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }
}
