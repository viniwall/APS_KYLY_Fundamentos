package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    Optional<Peca> findByCodigoUnico(String codigoUnico);

    @Query("SELECT COUNT(p) FROM Peca p WHERE p.itemCaixa.id = :itemId AND p.status = 'EM_CAIXA'")
    int countByItemCaixaId(Long itemId);
}
