package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.Pagamento;
import com.example.ecommercebackend.Support.StatoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    Pagamento findByOrdine(Ordine ordine);
    Pagamento findByIdPagamento(Integer id);
    List<Pagamento> findByOrdineUtenteEmailAndStato(String email, StatoPagamento stato);
    List<Pagamento> findByOrdineUtenteEmail(String email);
    List<Pagamento> findByOrdineUtenteIdUserAndDataBetween(Integer idUser, Date data1, Date data2);
}
