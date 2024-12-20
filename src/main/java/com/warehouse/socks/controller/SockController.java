package com.warehouse.socks.controller;

import com.warehouse.socks.dto.SockDTO;
import com.warehouse.socks.dto.SockFilterDTO;
import com.warehouse.socks.entity.Sock;
import com.warehouse.socks.service.SockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

// Контроллер для работы с носками
@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
@Api(tags = "Носки")
public class SockController {

    private final SockService sockService;

    //Регистрация прихода носков
    @PostMapping("/income")
    @ApiOperation("Регистрация прихода носков")
    public ResponseEntity<Void> income(@Valid @RequestBody SockDTO sockDTO) {
        sockService.income(sockDTO);
        return ResponseEntity.ok().build();
    }


    //Регистрация отпуска носков
    @PostMapping("/outcome")
    @ApiOperation("Регистрация отпуска носков")
    public ResponseEntity<Void> outcome(@Valid @RequestBody SockDTO sockDTO) {
        sockService.outcome(sockDTO);
        return ResponseEntity.ok().build();
    }

    //Получение носков по фильтру
    @GetMapping
    @ApiOperation("Получение списка носков с фильтрацией")
    public ResponseEntity<List<Sock>> getSocks(SockFilterDTO filter) {
        List<Sock> socks = sockService.getSocks(filter);
        return ResponseEntity.ok(socks);
    }

    //Обновление информации о носках
    @PutMapping("/{id}")
    @ApiOperation("Обновление информации о носках")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody SockDTO sockDTO
    ) {
        sockService.update(id, sockDTO);
        return ResponseEntity.ok().build();
    }

    //Загрузка партии носков из файла
    @PostMapping("/batch")
    @ApiOperation("Загрузка партии носков из Excel файла")
    public ResponseEntity<Void> uploadBatch(@RequestParam("file") MultipartFile file) throws IOException {
        sockService.uploadBatch(file);
        return ResponseEntity.ok().build();
    }
}
