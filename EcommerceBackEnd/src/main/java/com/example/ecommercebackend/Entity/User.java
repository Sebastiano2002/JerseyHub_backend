package com.example.ecommercebackend.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Entity
@Data
@ToString(exclude ="orders")
@Table(name = "user")
public class User implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "idUser")
        private Integer idUser;

        @NotNull
        @Column(name = "email",unique = true, nullable = false, length = 90)
        private String email;

        @NotNull
        @Column(name = "nome", nullable = false, length = 90)
        private String nome;

        @Column(name = "cognome", nullable = false, length = 90)
        private String cognome;

        @NotNull
        @Column(name = "telefono", length = 13)
        private String telefono;

        @Column(name = "citta", length = 90)
        private String citta;

        @Column(name = "indirizzo", length = 100)
        private String indirizzo;

        @NotNull
        @Column(name = "cap", length = 5)
        private String cap;

        @NotNull
        @Column(name = "carrello", nullable = false)
        private Integer carrello;

        @JsonManagedReference
        @OneToMany(mappedBy = "utente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<Ordine> orders;

        public Set<Pagamento> getPagamenti(){
                Set<Pagamento> pagamenti = new HashSet<>();
                for(Ordine o : orders){
                        if(o.getPagamento() != null) pagamenti.add(o.getPagamento());
                }
                return pagamenti;
        }
    }
