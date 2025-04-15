package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.Pagamento;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Support.StatoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Integer> {

    Ordine findByIdOrdine(Integer idOrdine);

    List<Ordine> findByUtente(User user);

    List<Ordine> findByUtenteEmail(String email);

    List<Ordine> findByDataOrdineBetween(Date startDate, Date endDate);

    List<Ordine> findByUtenteAndDataOrdineBetween(User user, Date startDate, Date endDate);

    List<Ordine> findByPrezzoTotGreaterThanEqual(Double totale);

    List<Ordine> findByUtenteAndPagamentoStato(User user, StatoPagamento statoPagamento);

}

