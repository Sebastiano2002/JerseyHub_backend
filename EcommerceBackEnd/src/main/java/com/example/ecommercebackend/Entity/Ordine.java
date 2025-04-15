package com.example.ecommercebackend.Entity;

import com.example.ecommercebackend.Support.StatoPagamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@ToString(exclude ="utente")
@Table(name = "ordine")
public class Ordine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOrdine", nullable = false)
    private Integer idOrdine;

    @NotNull
    @Column(name = "prezzoTot", nullable = false)
    private Double prezzoTot;

    @JsonIgnore
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dataOrdine")
    private Date dataOrdine;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "utente")
    private User utente;

    @OneToMany(mappedBy = "ordine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdineMaglia> ordineMaglie = new HashSet<>();

    @JsonManagedReference
    @OneToOne(mappedBy = "ordine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    public StatoPagamento getStatoPagamento() {
        if (this.pagamento != null) {
            return this.pagamento.getStato();
        }
        return StatoPagamento.IN_ATTESA; // stato predefinito in caso di assenza di pagamento
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ordine ordine = (Ordine) o;
        return this.getUtente().equals(ordine.getUtente()) &&
                Objects.equals(this.getOrdineMaglie(), ordine.ordineMaglie) &&
                this.prezzoTot.equals(ordine.getPrezzoTot()) &&
                this.getDataOrdine().equals(ordine.getDataOrdine()) &&
                this.getStatoPagamento().equals(ordine.getStatoPagamento());
    }
}

