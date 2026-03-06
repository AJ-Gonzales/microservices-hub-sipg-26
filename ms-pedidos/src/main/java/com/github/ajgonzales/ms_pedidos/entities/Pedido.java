package com.github.ajgonzales.ms_pedidos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column - define características da coluna do DB
    @Column(name = "nome", nullable = false,length = 100)
    private String nome;
//    @Column(unique = true,nullable = false,length = 11)
    @Column(nullable = false,length = 11)
    private String cpf;
    private LocalDate data;
    @Enumerated(EnumType.STRING)
    private Status status;
    //Valor calculado
    private BigDecimal valorTotal;

}
