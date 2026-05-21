package br.com.kollectaops.api.inventory.repository;

import br.com.kollectaops.api.inventory.domain.LocalizacaoPatrimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalizacaoRepository extends JpaRepository<LocalizacaoPatrimonial, Long> {

    List<LocalizacaoPatrimonial> findByFilialIdAndAtivo(Long filialId, boolean ativo);
}
