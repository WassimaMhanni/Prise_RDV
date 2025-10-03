package com.example.apprdv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText etUserId;  // Champ pour l'ID utilisateur
    private Button btnLogin;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser les vues
        etUserId = findViewById(R.id.etUserId);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialiser la base de données Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("clients");

        // Écouter le clic sur le bouton de login
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String userId = etUserId.getText().toString().trim();

        // Vérifier que l'ID n'est pas vide
        if (userId.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Veuillez entrer votre ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier l'existence de l'ID dans la base de données
        mDatabase.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Récupérer les informations de l'utilisateur
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();  // Prendre le premier utilisateur qui correspond
                    boolean firstConnection = userSnapshot.child("firstConnection").getValue(Boolean.class);

                    if (firstConnection) {
                        // Si c'est la première connexion, générer un mot de passe
                        String generatedPassword = generateRandomPassword();

                        // Mettre à jour la base de données avec le mot de passe et modifier firstConnection
                        mDatabase.child(userSnapshot.getKey()).child("password").setValue(generatedPassword);
                        mDatabase.child(userSnapshot.getKey()).child("firstConnection").setValue(false).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Rediriger vers la page de confirmation avec mot de passe généré
                                Intent intent = new Intent(LoginActivity.this, ConfirmPasswordActivity.class);
                                intent.putExtra("password", generatedPassword);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        // Si ce n'est pas la première connexion, rediriger vers la page de mot de passe
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        Intent intent = new Intent(LoginActivity.this, EnterPasswordActivity.class);
                        intent.putExtra("storedPassword", storedPassword);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // Si l'ID n'existe pas dans la base de données
                    Toast.makeText(LoginActivity.this, "ID incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Erreur de connexion à la base de données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour générer un mot de passe aléatoire de 8 caractères
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 8; i++) {
            int randomIndex = rand.nextInt(chars.length());
            password.append(chars.charAt(randomIndex));
        }

        return password.toString();
    }
}
