package br.com.kollectaops.api.inventory.repository;

import br.com.kollectaops.api.inventory.domain.LeituraInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeituraInventarioRepository extends JpaRepository<LeituraInventario, Long> {

    List<LeituraInventario> findByInventarioId(Long inventarioId);

    Page<LeituraInventario> findByDivergenciaNot(LeituraInventario.Divergencia divergencia, Pageable pageable);
}
