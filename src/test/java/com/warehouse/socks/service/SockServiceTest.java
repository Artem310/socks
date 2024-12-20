package com.warehouse.socks.service;

import com.warehouse.socks.dto.SockDTO;
import com.warehouse.socks.dto.SockFilterDTO;
import com.warehouse.socks.entity.Sock;
import com.warehouse.socks.exception.InsufficientSocksException;
import com.warehouse.socks.exception.InvalidOperationException;
import com.warehouse.socks.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервисного слоя
 */
@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @Mock
    private SockRepository sockRepository;

    @InjectMocks
    private SockService sockService;

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
    void income_WhenSockExists_ShouldUpdateQuantity() {
        // Arrange
        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.of(sock));
        when(sockRepository.save(any(Sock.class))).thenReturn(sock);

        // Act
        sockService.income(sockDTO);

        // Assert
        verify(sockRepository).save(sock);
        assertEquals(30, sock.getQuantity());
    }

    @Test
    void income_WhenSockDoesNotExist_ShouldCreateNew() {
        // Arrange
        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.empty());

        // Act
        sockService.income(sockDTO);

        // Assert
        verify(sockRepository).save(any(Sock.class));
    }

    @Test
    void outcome_WhenSockExistsAndQuantityIsSufficient_ShouldUpdateQuantity() {
        // Arrange
        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.of(sock));

        // Act
        sockService.outcome(sockDTO);

        // Assert
        verify(sockRepository).save(sock);
        assertEquals(10, sock.getQuantity());
    }

    @Test
    void outcome_WhenSockNotFound_ShouldThrowException() {
        // Arrange
        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> sockService.outcome(sockDTO));
    }

    @Test
    void outcome_WhenInsufficientQuantity_ShouldThrowException() {
        // Arrange
        sockDTO.setQuantity(30);
        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.of(sock));

        // Act & Assert
        assertThrows(InsufficientSocksException.class, () -> sockService.outcome(sockDTO));
    }

    @Test
    void getSocks_WithEqualOperation_ShouldReturnMatchingSocks() {
        // Arrange
        SockFilterDTO filter = new SockFilterDTO();
        filter.setColor("red");
        filter.setOperation("equal");
        filter.setCottonPart(80);

        when(sockRepository.findByColorAndCottonPart("red", 80))
                .thenReturn(Optional.of(sock));

        // Act
        List<Sock> result = sockService.getSocks(filter);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(sock, result.get(0));
    }

    @Test
    void getSocks_WithMoreThanOperation_ShouldReturnMatchingSocks() {
        // Arrange
        SockFilterDTO filter = new SockFilterDTO();
        filter.setColor("red");
        filter.setOperation("moreThan");
        filter.setCottonPart(70);

        when(sockRepository.findByColorAndCottonPartMoreThan("red", 70))
                .thenReturn(List.of(sock));

        // Act
        List<Sock> result = sockService.getSocks(filter);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(sock, result.get(0));
    }

    @Test
    void update_WhenSockExists_ShouldUpdateSock() {
        // Arrange
        when(sockRepository.findById(1L)).thenReturn(Optional.of(sock));
        when(sockRepository.save(any(Sock.class))).thenReturn(sock);

        // Act
        sockService.update(1L, sockDTO);

        // Assert
        verify(sockRepository).save(sock);
        assertEquals(sockDTO.getColor(), sock.getColor());
        assertEquals(sockDTO.getCottonPart(), sock.getCottonPart());
        assertEquals(sockDTO.getQuantity(), sock.getQuantity());
    }

    @Test
    void update_WhenSockNotFound_ShouldThrowException() {
        // Arrange
        when(sockRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidOperationException.class,
                () -> sockService.update(1L, sockDTO));
    }
}
