package com.github.ajgonzales.ms_pedidos.dto;

import com.github.ajgonzales.ms_pedidos.entities.ItemDoPedido;
import com.github.ajgonzales.ms_pedidos.entities.Pedido;
import com.github.ajgonzales.ms_pedidos.entities.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoDTO {

    private Long id;

    @NotBlank(message = "Nome requerido")
    @Size(min = 3,max = 100,message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    //@CPF - valida o cpf
    @NotBlank(message = "CPF requerido")
    @Size(min = 11,max = 11,message = "O CPF deve ter 11 caracteres")
    private String cpf;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal valorTotal;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<@Valid ItemDoPedidoDTO> itens = new ArrayList<>();

    public PedidoDTO(Pedido pedido){
        id = pedido.getId();
        nome = pedido.getNome();
        cpf = pedido.getCpf();
        data = pedido.getData();
        status = pedido.getStatus();
        valorTotal = pedido.getValorTotal();

        //para os itens do pedido
        for (ItemDoPedido item : pedido.getItens()){
            ItemDoPedidoDTO itemDTO = new ItemDoPedidoDTO(item);
            itens.add(itemDTO);
        }

    }


}
