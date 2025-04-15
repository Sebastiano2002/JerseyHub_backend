package com.example.ecommercebackend.Support.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeagueDTO {

    @NotNull
    private String league;

    private String img;
}
