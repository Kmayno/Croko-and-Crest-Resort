package com.hotel.crock_crest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_admin")
    private int idAdmin;

    @Column(name = "nome")
    private String nome;

    @Column(name = "password_admin")
    private String passwordAdmin;
    @Column(name = "email")
    private String email;

    @Transient
    private String token;

    public Admin(){}



    public Admin(String email, String passwordAdmin){
        this.email = email;
        this.passwordAdmin = passwordAdmin;
    }




    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPasswordAdmin() {
        return passwordAdmin;
    }

    public void setPasswordAdmin(String passwordAdmin) {
        this.passwordAdmin = passwordAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}