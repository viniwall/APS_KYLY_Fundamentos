package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.SessaoColetor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SessaoColetorRepository extends JpaRepository<SessaoColetor, Long> {

    @Query("SELECT COUNT(DISTINCT s.operador.id) FROM SessaoColetor s WHERE s.encerradaEm IS NULL")
    long countOperadoresAtivos();
}
