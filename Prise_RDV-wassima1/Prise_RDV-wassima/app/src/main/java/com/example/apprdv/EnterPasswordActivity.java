package com.example.apprdv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EnterPasswordActivity extends AppCompatActivity {

    private EditText etPassword;
    private Button btnConfirm;
    private String storedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        // Initialiser les vues
        etPassword = findViewById(R.id.etPassword);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Récupérer le mot de passe stocké passé depuis LoginActivity
        storedPassword = getIntent().getStringExtra("storedPassword");

        // Lorsque l'utilisateur clique sur le bouton, vérifier le mot de passe
        btnConfirm.setOnClickListener(v -> confirmPassword());
    }

    private void confirmPassword() {
        String enteredPassword = etPassword.getText().toString().trim();

        // Vérifier que le mot de passe entré est correct
        if (enteredPassword.isEmpty()) {
            Toast.makeText(EnterPasswordActivity.this, "Veuillez entrer le mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredPassword.equals(storedPassword)) {
            // Si le mot de passe est correct, rediriger vers la page d'accueil du client
            Intent intent = new Intent(EnterPasswordActivity.this, ClientHomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Si le mot de passe est incorrect
            Toast.makeText(EnterPasswordActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}
