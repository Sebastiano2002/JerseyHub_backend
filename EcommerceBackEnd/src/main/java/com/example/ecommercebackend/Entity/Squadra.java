package com.example.ecommercebackend.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@ToString(exclude ="maglie")
@Table(name = "squadra")
public class Squadra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSquadra")
    private Integer idSquadra;

    @NotNull
    @Column(name = "squadra", nullable = false, length = 90, unique = true)
    private String squadra;

    @Column(name = "img", nullable = false)
    private String img;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league")
    private League league;

    @JsonManagedReference
    @OneToMany(mappedBy = "squadra", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Maglia> maglie;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Squadra squad = (Squadra) o;
        return squadra.equals(squad.getSquadra()) && league.equals((squad.getLeague()));
    }

    @Override
    public  String toString(){return squadra;}

}