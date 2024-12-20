package com.warehouse.socks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.socks.dto.SockDTO;
import com.warehouse.socks.dto.SockFilterDTO;
import com.warehouse.socks.entity.Sock;
import com.warehouse.socks.exception.InsufficientSocksException;
import com.warehouse.socks.exception.InvalidOperationException;
import com.warehouse.socks.service.SockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для контроллера
 */
@WebMvcTest(SockController.class)
class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @Autowired
    private ObjectMapper objectMapper;

    private SockDTO sockDTO;
    private Sock sock;

    @BeforeEach
    void setUp() {
        sockDTO = new SockDTO();
        sockDTO.setColor("red");
        sockDTO.setCottonPart(80);
        sockDTO.setQuantity(10);

        sock = new Sock();
        sock.setId(1L);
        sock.setColor("red");
        sock.setCottonPart(80);
        sock.setQuantity(20);
    }

    @Test
    void income_ValidInput_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isOk());

        verify(sockService).income(any(SockDTO.class));
    }

    @Test
    void income_InvalidInput_ShouldReturn400() throws Exception {
        // Arrange
        sockDTO.setQuantity(-1);

        // Act & Assert
        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void outcome_ValidInput_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isOk());

        verify(sockService).outcome(any(SockDTO.class));
    }

    @Test
    void outcome_InsufficientQuantity_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InsufficientSocksException("Недостаточно носков"))
                .when(sockService).outcome(any(SockDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSocks_ValidFilter_ShouldReturn200() throws Exception {
        // Arrange
        when(sockService.getSocks(any(SockFilterDTO.class)))
                .thenReturn(List.of(sock));

        // Act & Assert
        mockMvc.perform(get("/api/socks")
                        .param("color", "red")
                        .param("operation", "equal")
                        .param("cottonPart", "80"))
                .andExpect(status().isOk());
    }

    @Test
    void update_ValidInput_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/socks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isOk());

        verify(sockService).update(eq(1L), any(SockDTO.class));
    }

    @Test
    void update_NonexistentSock_ShouldReturn400() throws Exception {
        // Arrange
        doThrow(new InvalidOperationException("Носки не найдены"))
                .when(sockService).update(eq(1L), any(SockDTO.class));

        // Act & Assert
        mockMvc.perform(put("/api/socks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadBatch_ValidFile_ShouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "socks.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "test content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/socks/batch")
                        .file(file))
                .andExpect(status().isOk());

        verify(sockService).uploadBatch(any());
    }
}
