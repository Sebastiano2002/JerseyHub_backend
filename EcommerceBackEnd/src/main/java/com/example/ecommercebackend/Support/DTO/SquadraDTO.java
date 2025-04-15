package com.example.ecommercebackend.Support.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SquadraDTO {

    @NotNull
    private String nome;

    private String img;

    @NotNull
    private String leagueName;
}
