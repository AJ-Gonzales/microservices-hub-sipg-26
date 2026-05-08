package com.github.ajgonzales.ms_pedidos.repositories;

import com.github.ajgonzales.ms_pedidos.entities.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido,Long> {
}
