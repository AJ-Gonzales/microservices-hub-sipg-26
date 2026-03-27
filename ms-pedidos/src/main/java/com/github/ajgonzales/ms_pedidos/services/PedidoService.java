package com.github.ajgonzales.ms_pedidos.services;

import com.github.ajgonzales.ms_pedidos.dto.ItemDoPedidoDTO;
import com.github.ajgonzales.ms_pedidos.dto.PedidoDTO;
import com.github.ajgonzales.ms_pedidos.entities.ItemDoPedido;
import com.github.ajgonzales.ms_pedidos.entities.Pedido;
import com.github.ajgonzales.ms_pedidos.entities.Status;
import com.github.ajgonzales.ms_pedidos.exceptions.ResourceNotFoundException;
import com.github.ajgonzales.ms_pedidos.repositories.ItemDoPedidoRepository;
import com.github.ajgonzales.ms_pedidos.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoDTO> findAllPedidos(){
        return pedidoRepository.findAll().stream().map(PedidoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PedidoDTO findPedidoById(Long id){

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: "+id)
        );
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO savePedido(PedidoDTO pedidoDTO){

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO,pedido);
        pedido.calcularValorTotalDoPedido();
        pedido = pedidoRepository.save(pedido);

        return new PedidoDTO(pedido);
    }

    private void mapDtoToPedido(PedidoDTO pedidoDTO, Pedido pedido){

        pedido.setNome(pedidoDTO.getNome());
        pedido.setCpf(pedidoDTO.getCpf());

        for (ItemDoPedidoDTO itemDTO : pedidoDTO.getItens()){
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }

    @Transactional
    public PedidoDTO updatePedido(Long id, PedidoDTO pedidoDTO){

        try {
            Pedido pedido = pedidoRepository.getReferenceById(id);
            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
            pedido.setStatus(Status.CRIADO);
            mapDtoToPedido(pedidoDTO,pedido);
            pedido.calcularValorTotalDoPedido();
            pedido = pedidoRepository.save(pedido);

            return new PedidoDTO(pedido);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado. ID: "+id);
        }
    }

    @Transactional
    public void deletePedidoById(Long id){

        if (!pedidoRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: "+id);
        }

        pedidoRepository.deleteById(id);
    }

}
