package com.example.apprdv;

import java.util.List;
import java.util.Map;

public class Advisor {
    private String advisorId;  // Ajout de l'ID de l'advisor
    private String name;
    private String surname;
    private String agency;
    private String phone;
    private String address;
    private String email;
    private String password;
    private String role;
    private List<String> availableSlots;
    private Map<String, Map<String, Boolean>> availability;

    // Constructeur vide nécessaire pour Firebase
    public Advisor() {}

    // Constructeur avec tous les champs, y compris advisorId et availability
    public Advisor(String advisorId, String name, String surname, String agency, String phone, String address, String email, String password, String role, List<String> availableSlots, Map<String, Map<String, Boolean>> availability) {
        this.advisorId = advisorId;  // Initialisation de advisorId
        this.name = name;
        this.surname = surname;
        this.agency = agency;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.password = password;
        this.role = role;
        this.availableSlots = availableSlots;
        this.availability = availability;
    }

    // Getters et setters
    public String getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(String advisorId) {
        this.advisorId = advisorId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAgency() {
        return agency;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public List<String> getAvailableSlots() {
        return availableSlots;
    }

    public Map<String, Map<String, Boolean>> getAvailability() {
        return availability;
    }
}
