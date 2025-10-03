package com.example.apprdv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAdvisorActivity extends AppCompatActivity {

    private EditText etName, etSurname, etAgency, etPhone, etEmail, etAddress, etPassword;
    private Button btnSaveAdvisor;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advisor);

        // Initialiser Firebase Realtime Database et FirebaseAuth
        mDatabase = FirebaseDatabase.getInstance().getReference("advisors");
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les champs de saisie
        etName = findViewById(R.id.et_name);
        etSurname = findViewById(R.id.et_surname);
        etAgency = findViewById(R.id.et_agency);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        btnSaveAdvisor = findViewById(R.id.btn_save_advisor);

        // Gérer l'événement de clic du bouton
        btnSaveAdvisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAdvisor();
            }
        });
    }

    private void saveAdvisor() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String agency = etAgency.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Vérification des champs vides
        if (name.isEmpty() || surname.isEmpty() || agency.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
            Toast.makeText(AddAdvisorActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Générer un ID unique pour le conseiller
        String advisorId = mDatabase.push().getKey();

        // Créer un nouvel objet Advisor avec le rôle "advisor" et initialiser l'availability
        List<String> availableSlots = new ArrayList<>();  // Crée une liste vide pour les créneaux disponibles
        Advisor advisor = new Advisor(advisorId, name, surname, agency, phone, address, email, password, "advisor", availableSlots, createDefaultAvailability());

        // Créer l'utilisateur dans Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Connexion réussie, ajouter les données dans Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (advisorId != null) {
                                mDatabase.child(advisorId).setValue(advisor).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(AddAdvisorActivity.this, "Conseiller ajouté avec succès", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(AddAdvisorActivity.this, "Erreur lors de l'ajout du conseiller", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(AddAdvisorActivity.this, "Erreur lors de la création du compte conseiller", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Méthode pour créer une disponibilité par défaut avec les créneaux horaires
    private Map<String, Map<String, Boolean>> createDefaultAvailability() {
        Map<String, Map<String, Boolean>> availability = new HashMap<>();

        // Par exemple, définissons des créneaux horaires pour 2025-09-10 (modifiable selon la logique de ton application)
        Map<String, Boolean> slots = new HashMap<>();
        slots.put("09:00", true);  // Créneau disponible
        slots.put("10:00", true);  // Créneau disponible
        slots.put("11:00", true);  // Créneau disponible
        slots.put("12:00", false); // Créneau indisponible
        slots.put("14:00", true);  // Créneau disponible
        slots.put("15:00", false); // Créneau indisponible
        slots.put("16:00", true);  // Créneau disponible

        // Ajouter ces créneaux à une date spécifique, ici "2025-09-10"
        availability.put("2025-09-10", slots);

        return availability;
    }

}
