package com.example.ecommercebackend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "Maglia")
public class Maglia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMaglia")
    private Integer idMaglia;

    @NotNull
    @Column(name = "nome", nullable = false, unique = true, length = 50)
    private String nome;

    @NotNull
    @Column(name = "img", nullable = false)
    private String img;

    @NotNull
    @Column(name = "quantitaDisp", nullable = false, columnDefinition = "integer default 0")
    private int quantitaDisp;

    @NotNull
    @Column(name = "prezzo", nullable = false)
    private double prezzo;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squadra", nullable = false)
    private Squadra squadra;

    @JsonIgnore
    @OneToMany(mappedBy = "maglia", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrdineMaglia> items = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Maglia maglia = (Maglia) o;

        if (!Objects.equals(idMaglia, maglia.getIdMaglia())) return false;
        if (!nome.equals(maglia.getNome())) return false;
        return squadra.equals(maglia.getSquadra()) && Objects.equals(this.getItems(), maglia.getItems());
    }

    public String getSquadName(){ return this.squadra.getSquadra();}

}