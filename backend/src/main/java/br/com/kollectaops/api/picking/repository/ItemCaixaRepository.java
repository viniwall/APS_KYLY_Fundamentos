package br.com.kollectaops.api.picking.repository;

import br.com.kollectaops.api.picking.domain.ItemCaixa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCaixaRepository extends JpaRepository<ItemCaixa, Long> {

    @Query("SELECT i FROM ItemCaixa i WHERE i.caixa.id = :caixaId ORDER BY i.ordemPicking ASC")
    List<ItemCaixa> findByCaixaIdOrdenado(Long caixaId);

    @Query("SELECT i FROM ItemCaixa i WHERE i.caixa.id = :caixaId AND i.status IN ('PENDENTE','EM_COLETA') ORDER BY i.ordemPicking ASC")
    List<ItemCaixa> findPendentesOuEmColeta(Long caixaId);

    Optional<ItemCaixa> findFirstByCaixaIdAndStatusOrderByOrdemPickingAsc(Long caixaId, ItemCaixa.Status status);

    // Divergencias report – native SQL projection
    interface DivergenciaRowView {
        Long getId();
        String getCaixaPapeleta();
        String getClienteNome();
        String getSkuReferencia();
        String getSkuDescricao();
        String getSkuCor();
        String getSkuTamanho();
        String getEnderecoCodigo();
        Integer getQtdeSolicitada();
        Integer getQtdeColetada();
        String getStatus();
        LocalDateTime getRegistradoEm();
    }

    @Query(value = """
        SELECT ic.id,
               c.codigo_papeleta              AS caixaPapeleta,
               p.cliente_nome                 AS clienteNome,
               s.referencia                   AS skuReferencia,
               s.descricao                    AS skuDescricao,
               s.cor                          AS skuCor,
               s.tamanho                      AS skuTamanho,
               e.codigo                       AS enderecoCodigo,
               ic.qtde_solicitada             AS qtdeSolicitada,
               ic.qtde_coletada               AS qtdeColetada,
               ic.status,
               COALESCE(c.finalizada_em, c.aberta_em) AS registradoEm
        FROM item_caixa ic
        JOIN  caixa c            ON c.id  = ic.caixa_id
        LEFT JOIN pedido p       ON p.id  = c.pedido_id
        LEFT JOIN sku s          ON s.id  = ic.sku_id
        LEFT JOIN endereco_estoque e ON e.id = ic.endereco_sugerido_id
        WHERE ic.status IN ('EM_FALTA', 'DIVERGENTE')
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
          AND (:q   IS NULL OR LOWER(c.codigo_papeleta) LIKE LOWER(CONCAT('%',:q,'%'))
                            OR LOWER(p.cliente_nome)    LIKE LOWER(CONCAT('%',:q,'%'))
                            OR LOWER(s.referencia)      LIKE LOWER(CONCAT('%',:q,'%')))
        ORDER BY registradoEm DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM item_caixa ic
        JOIN  caixa c       ON c.id  = ic.caixa_id
        LEFT JOIN pedido p  ON p.id  = c.pedido_id
        LEFT JOIN sku s     ON s.id  = ic.sku_id
        WHERE ic.status IN ('EM_FALTA', 'DIVERGENTE')
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
          AND (:q   IS NULL OR LOWER(c.codigo_papeleta) LIKE LOWER(CONCAT('%',:q,'%'))
                            OR LOWER(p.cliente_nome)    LIKE LOWER(CONCAT('%',:q,'%'))
                            OR LOWER(s.referencia)      LIKE LOWER(CONCAT('%',:q,'%')))
        """,
        nativeQuery = true)
    Page<DivergenciaRowView> findDivergencias(
        @Param("de") String de,
        @Param("ate") String ate,
        @Param("q") String q,
        Pageable pageable
    );

    @Query(value = """
        SELECT COUNT(*) FROM item_caixa ic
        JOIN caixa c ON c.id = ic.caixa_id
        WHERE ic.status = :status
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
        """, nativeQuery = true)
    long countByStatusInPeriodo(
        @Param("status") String status,
        @Param("de") String de,
        @Param("ate") String ate
    );

    @Query(value = """
        SELECT COUNT(DISTINCT ic.caixa_id) FROM item_caixa ic
        JOIN caixa c ON c.id = ic.caixa_id
        WHERE ic.status IN ('EM_FALTA', 'DIVERGENTE')
          AND (:de  IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) >= :de)
          AND (:ate IS NULL OR DATE(COALESCE(c.finalizada_em, c.aberta_em)) <= :ate)
        """, nativeQuery = true)
    long countCaixasAfetadasInPeriodo(
        @Param("de") String de,
        @Param("ate") String ate
    );
}
