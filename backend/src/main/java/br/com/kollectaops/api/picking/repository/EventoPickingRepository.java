package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.EventoPicking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoPickingRepository extends JpaRepository<EventoPicking, Long> {

    List<EventoPicking> findByCaixaIdOrderByOcorridoEmDesc(Long caixaId);

    @Query("SELECT COUNT(ep) FROM EventoPicking ep WHERE ep.sincronizado = false")
    long countPendentes();
}
