package com.example.ecommercebackend.Support.DTO;

import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrdineDTO {

    private Integer idOrdine;

    @NotNull
    private Double prezzoTot;

    @NotNull
    private String utente;

    private List<OrdineMaglia> items;

    @NotNull
    private Integer pagamento;
}
