package com.example.ecommercebackend.Support.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdineMagliaDTO {
    @NotNull
    private Integer idOrdine;

    @NotNull
    private Integer idMaglia;

    @NotNull
    private String squadra;

    @NotNull
    private Integer richiesta;

    @NotNull
    private String taglia;

    private String giocatore;

    private int numero;
}
