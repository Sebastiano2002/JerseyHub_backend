package com.example.ecommercebackend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@Table(name = "ordineMaglia")
public class OrdineMaglia implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrdineMaglia")
    private Integer idOrdineMaglia;

    @ManyToOne
    @JoinColumn(name = "ordine")
    @JsonIgnore
    private Ordine ordine;

    @ManyToOne
    @JoinColumn(name = "maglia")
    private Maglia maglia;

    @Column(name = "giocatore", length = 50)
    private String giocatore;

    @Column(name = "numero")
    private int numero;

    @Column(name = "taglia")
    private String taglia;

    @NotNull
    @Column(name = "richiesta", nullable = false)
    private int richiesta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdineMaglia ordM = (OrdineMaglia) o;
        return numero == ordM.numero &&
                Objects.equals(maglia, ordM.maglia) &&
                Objects.equals(ordine, ordM.ordine) &&
                Objects.equals(taglia, ordM.taglia) &&
                Objects.equals(giocatore, ordM.giocatore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maglia, ordine, taglia, giocatore, numero);
    }


}