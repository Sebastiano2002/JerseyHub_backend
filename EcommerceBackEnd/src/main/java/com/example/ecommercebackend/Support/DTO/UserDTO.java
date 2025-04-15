package com.example.ecommercebackend.Support.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

    private int idUser;

    @NotNull
    private String email;

    private String nome;

    private String cognome;
    @NotNull
    private String telefono;

    private String citta;

    private String indirizzo;

    private String cap;

    @NotNull
    private Integer carrello;
}
