package com.example.apprdv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableAdvisorsActivity extends AppCompatActivity {

    private RecyclerView availableAdvisorsRecyclerView;
    private List<Advisor> availableAdvisorsList = new ArrayList<>();
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_advisors);

        availableAdvisorsRecyclerView = findViewById(R.id.availableAdvisorsRecyclerView);

        // Récupérer la date sélectionnée passée depuis ClientHomeActivity
        selectedDate = getIntent().getStringExtra("selectedDate");

        // Initialiser l'adaptateur du RecyclerView
        AvailableAdvisorsAdapter adapter = new AvailableAdvisorsAdapter(availableAdvisorsList, this::onAdvisorSlotSelected);
        availableAdvisorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        availableAdvisorsRecyclerView.setAdapter(adapter);

        // Récupérer les conseillers disponibles à la date sélectionnée
        getAvailableAdvisors(selectedDate);
    }

    private void getAvailableAdvisors(String date) {
        DatabaseReference advisorsRef = FirebaseDatabase.getInstance().getReference("advisors");

        advisorsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();

                for (DataSnapshot advisorSnapshot : snapshot.getChildren()) {
                    String advisorId = advisorSnapshot.getKey(); // Récupérer l'ID de l'advisor

                    // Récupérer toutes les informations du conseiller
                    String advisorName = advisorSnapshot.child("name").getValue(String.class);
                    String advisorSurname = advisorSnapshot.child("surname").getValue(String.class);
                    String advisorAgency = advisorSnapshot.child("agency").getValue(String.class);
                    String advisorPhone = advisorSnapshot.child("phone").getValue(String.class);
                    String advisorAddress = advisorSnapshot.child("address").getValue(String.class);
                    String advisorEmail = advisorSnapshot.child("email").getValue(String.class);
                    String advisorPassword = advisorSnapshot.child("password").getValue(String.class);
                    String advisorRole = advisorSnapshot.child("role").getValue(String.class);

                    // Récupérer la disponibilité du conseiller pour la date sélectionnée
                    if (advisorSnapshot.child("availability").hasChild(date)) {
                        DataSnapshot availabilitySnapshot = advisorSnapshot.child("availability").child(date);
                        List<String> availableSlots = new ArrayList<>();

                        // Vérifier chaque créneau horaire pour cette date
                        for (DataSnapshot timeSlotSnapshot : availabilitySnapshot.getChildren()) {
                            boolean isAvailable = timeSlotSnapshot.getValue(Boolean.class);
                            if (isAvailable) {
                                availableSlots.add(timeSlotSnapshot.getKey()); // Ajouter les créneaux disponibles
                            }
                        }

                        // Si le conseiller a des créneaux disponibles, l'ajouter à la liste
                        if (!availableSlots.isEmpty()) {
                            Map<String, Map<String, Boolean>> availability = (Map<String, Map<String, Boolean>>) advisorSnapshot.child("availability").getValue();
                            // Créer l'objet Advisor avec l'ID et toutes les informations
                            Advisor advisor = new Advisor(advisorId, advisorName, advisorSurname, advisorAgency, advisorPhone, advisorAddress, advisorEmail, advisorPassword, advisorRole, availableSlots, availability);
                            availableAdvisorsList.add(advisor);
                        }
                    }
                }

                // Notifier l'adaptateur que les données ont été mises à jour
                availableAdvisorsRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                Toast.makeText(AvailableAdvisorsActivity.this, "Erreur de récupération des conseillers", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Cette méthode est appelée lorsqu'un créneau horaire est sélectionné
    private void onAdvisorSlotSelected(Advisor advisor, String selectedSlot) {
        // Rediriger vers la page MainActivity pour réserver le créneau
        Intent intent = new Intent(AvailableAdvisorsActivity.this, ConfirmAppointmentActivity.class);
        intent.putExtra("advisorName", advisor.getName());
        intent.putExtra("advisorAddress", advisor.getAddress());
        intent.putExtra("selectedDate", selectedDate);  // Date sélectionnée passée depuis AvailableAdvisorsActivity
        intent.putExtra("selectedSlot", selectedSlot);
        intent.putExtra("advisorId", advisor.getAdvisorId());
        startActivity(intent);
    }
}
