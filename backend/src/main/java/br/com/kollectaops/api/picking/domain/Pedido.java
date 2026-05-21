package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 50)
    private String numeroPedido;

    @Column(name = "cliente_nome", length = 200)
    private String clienteNome;

    @Column(name = "cliente_doc", length = 30)
    private String clienteDoc;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ABERTO;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<Caixa> caixas;

    public enum Status { ABERTO, EM_PICKING, FINALIZADO, CANCELADO }
}
