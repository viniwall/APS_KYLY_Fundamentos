package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.EnderecoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<EnderecoEstoque, Long> {

    @Query(value = "SELECT e.*, est.quantidade FROM endereco_estoque e " +
                   "JOIN estoque est ON est.endereco_id = e.id " +
                   "WHERE est.sku_id = :skuId AND est.quantidade > 0 AND e.ativo = 1 " +
                   "ORDER BY est.quantidade DESC LIMIT 5",
           nativeQuery = true)
    List<Object[]> findPosicoesComEstoqueBySkuId(Long skuId);
}
