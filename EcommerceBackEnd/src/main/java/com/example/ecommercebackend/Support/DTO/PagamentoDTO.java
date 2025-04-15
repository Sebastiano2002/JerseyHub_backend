package com.example.ecommercebackend.Support.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagamentoDTO {

    @NotNull
    private String numCarta;

    @NotNull
    private String metodo;

    @NotNull
    private Integer idOrdine;

}
