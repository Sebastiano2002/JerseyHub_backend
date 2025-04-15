package com.example.ecommercebackend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "league")
public class League implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLeague")
    private Integer idLeague;

    @NotNull
    @Column(name = "leagueName", nullable = false, length = 90, unique = true)
    private String leagueName;

    @Column(name = "img")
    private String img;

    @OneToMany( mappedBy = "league", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Squadra> squadre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return Objects.equals(this.leagueName, league.getLeagueName());
    }
}