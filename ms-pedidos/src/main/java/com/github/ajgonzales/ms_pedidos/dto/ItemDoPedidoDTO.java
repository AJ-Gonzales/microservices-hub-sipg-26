package com.github.ajgonzales.ms_pedidos.dto;

import com.github.ajgonzales.ms_pedidos.entities.ItemDoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemDoPedidoDTO {

    private Long id;

    @NotNull(message = "Quantidade requerido")
    @Positive(message = "Quantidade deve ser um número inteiro")
    private Integer quantidade;

    @NotBlank(message = "Descrição requerida")
    private String descricao;

    @NotNull(message = "Preço Unitário requerido")
    @Positive(message = "O Preço Unitário deve ser um número positivo e maior que zero")
    private BigDecimal precoUnitario;

    public ItemDoPedidoDTO(ItemDoPedido itemDoPedido){
        id = itemDoPedido.getId();
        quantidade = itemDoPedido.getQuantidade();
        descricao = itemDoPedido.getDescricao();
        precoUnitario = itemDoPedido.getPrecoUnitario();
    }
}
