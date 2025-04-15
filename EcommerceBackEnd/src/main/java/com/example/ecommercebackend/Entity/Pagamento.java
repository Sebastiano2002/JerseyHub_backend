package com.example.ecommercebackend.Entity;

import com.example.ecommercebackend.Support.StatoPagamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "pagamento")
public class Pagamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPagamento")
    private Integer idPagamento;

    @Column(name = "metodoPag")
    private String metodoPag;

    @Column(name = "numCarta")
    private String numCarta;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data",nullable = false)
    private Date data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoPagamento stato;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "ordine", nullable = false)
    private Ordine ordine;

    public User getUser(){return ordine.getUtente();}

    @Override
    public String toString() {
        return "Pagamento{id=" + idPagamento + ", ordineId=" + (ordine != null ? ordine.getIdOrdine() : "null") + "}";
    }

}