package br.com.kollectaops.api.inventory.repository;

import br.com.kollectaops.api.inventory.domain.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByFilialIdAndStatus(Long filialId, Inventario.Status status);

    List<Inventario> findByFilialId(Long filialId);
}
