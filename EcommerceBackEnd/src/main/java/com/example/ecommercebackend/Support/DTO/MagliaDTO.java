package com.example.ecommercebackend.Support.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MagliaDTO {

    private Integer idMaglia;

    @NotNull
    private String nomeMaglia;

    private String img;

    @NotNull
    private int quantitaDisp;

    @NotNull
    private double prezzo;

    @NotNull
    private String squadra;
}
