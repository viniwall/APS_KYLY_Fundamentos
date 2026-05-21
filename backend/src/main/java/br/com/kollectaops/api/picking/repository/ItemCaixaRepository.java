package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.ItemCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCaixaRepository extends JpaRepository<ItemCaixa, Long> {

    @Query("SELECT i FROM ItemCaixa i WHERE i.caixa.id = :caixaId ORDER BY i.ordemPicking ASC")
    List<ItemCaixa> findByCaixaIdOrdenado(Long caixaId);

    @Query("SELECT i FROM ItemCaixa i WHERE i.caixa.id = :caixaId AND i.status IN ('PENDENTE','EM_COLETA') ORDER BY i.ordemPicking ASC")
    List<ItemCaixa> findPendentesOuEmColeta(Long caixaId);

    Optional<ItemCaixa> findFirstByCaixaIdAndStatusOrderByOrdemPickingAsc(Long caixaId, ItemCaixa.Status status);
}
