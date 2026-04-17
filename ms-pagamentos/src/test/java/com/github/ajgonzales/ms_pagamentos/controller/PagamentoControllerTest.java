package com.github.ajgonzales.ms_pagamentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajgonzales.ms_pagamentos.dto.PagamentoDTO;
import com.github.ajgonzales.ms_pagamentos.entities.Pagamento;
import com.github.ajgonzales.ms_pagamentos.exceptions.ResourceNotFoundException;
import com.github.ajgonzales.ms_pagamentos.service.PagamentoService;
import com.github.ajgonzales.ms_pagamentos.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService pagamentoService;
    private Pagamento pagamento;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;
        pagamento = Factory.createPagamento();
    }

    @Test
    void findAllPagamentosShouldReturnListPagamentoDTO() throws Exception{

        PagamentoDTO inputDto = new PagamentoDTO(pagamento);
        List<PagamentoDTO> list = List.of(inputDto);
        Mockito.when(pagamentoService.findAllPagamentos()).thenReturn(list);

        ResultActions result = mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON));
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$[0].id").value(pagamento.getId()));
        result.andExpect(jsonPath("$[0].valor").value(pagamento.getValor().doubleValue()));

        Mockito.verify(pagamentoService).findAllPagamentos();
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void findPagamentoByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(pagamentoService.findPagamentoById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: "+ nonExistingId));

        mockMvc.perform(get("/pagamentos/{id}",nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(pagamentoService).findPagamentoById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn201WhenValid() throws Exception{

        PagamentoDTO requestDto = new PagamentoDTO(Factory.createPagamentoSemId());

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
        PagamentoDTO responseDto = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.savePagamento(any(PagamentoDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(pagamento.getId()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).savePagamento(any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn422WhenInvalid() throws Exception {

        Pagamento pagamentoInvalido = Factory.createPagamentoSemId();
        pagamentoInvalido.setValor(BigDecimal.valueOf(0)); //pode ser BigDecimal.ZERO
        pagamentoInvalido.setNome(null);
        PagamentoDTO requestDto = new PagamentoDTO(pagamentoInvalido);
        String jsonRequestBody = objectMapper.writeValueAsString(pagamentoInvalido);
        PagamentoDTO responseDto = new PagamentoDTO(pagamentoInvalido);

        Mockito.when(pagamentoService.savePagamento(any(PagamentoDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void updatePagamentoShouldReturn200WhenValid() throws Exception {

        PagamentoDTO requestDto = new PagamentoDTO(Factory.createPagamento());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
        PagamentoDTO responseDto = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.updatePagamento(eq(existingId),any(PagamentoDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/pagamentos/{id}",existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).updatePagamento(eq(existingId),any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }



    @Test
    void deletePagamentoShouldreturn204WhenIdExists() throws Exception {

        Mockito.doNothing().when(pagamentoService).deletePagamento(existingId);

        mockMvc.perform(delete("/pagamentos/{id}",existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(pagamentoService).deletePagamento(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void deletePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.doThrow(new ResourceNotFoundException("Recurso não encontrado. ID: "+nonExistingId))
                .when(pagamentoService).deletePagamento(nonExistingId);

        mockMvc.perform(delete("/pagamentos/{id}",nonExistingId))
                .andExpect(status().isNotFound());

        Mockito.verify(pagamentoService).deletePagamento(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }


}
