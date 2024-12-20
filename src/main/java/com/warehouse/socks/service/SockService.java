package com.warehouse.socks.service;

import com.warehouse.socks.dto.SockDTO;
import com.warehouse.socks.dto.SockFilterDTO;
import com.warehouse.socks.entity.Sock;
import com.warehouse.socks.exception.InsufficientSocksException;
import com.warehouse.socks.exception.InvalidOperationException;
import com.warehouse.socks.repository.SockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//Сервис для работы с носками
@Service
@Slf4j
@RequiredArgsConstructor
public class SockService {

    private final SockRepository sockRepository;

    //Регистрация прихода носков
    @Transactional
    public void income(SockDTO sockDTO) {
        log.info("Регистрация прихода носков: {}", sockDTO);

        Optional<Sock> existingSock = sockRepository.findByColorAndCottonPart(
                sockDTO.getColor(),
                sockDTO.getCottonPart()
        );

        if (existingSock.isPresent()) {
            Sock sock = existingSock.get();
            sock.setQuantity(sock.getQuantity() + sockDTO.getQuantity());
            sockRepository.save(sock);
        } else {
            Sock newSock = new Sock();
            newSock.setColor(sockDTO.getColor());
            newSock.setCottonPart(sockDTO.getCottonPart());
            newSock.setQuantity(sockDTO.getQuantity());
            sockRepository.save(newSock);
        }
    }

    //Регистрация отпуска носков
    @Transactional
    public void outcome(SockDTO sockDTO) {
        log.info("Регистрация отпуска носков: {}", sockDTO);

        Sock sock = sockRepository.findByColorAndCottonPart(
                sockDTO.getColor(),
                sockDTO.getCottonPart()
        ).orElseThrow(() -> new InvalidOperationException("Носки не найдены"));

        if (sock.getQuantity() < sockDTO.getQuantity()) {
            throw new InsufficientSocksException("Недостаточно носков на складе");
        }

        sock.setQuantity(sock.getQuantity() - sockDTO.getQuantity());
        sockRepository.save(sock);
    }

    //Получение количества носков по фильтру
    public List<Sock> getSocks(SockFilterDTO filter) {
        log.info("Получение носков по фильтру: {}", filter);

        Sort sort = Sort.unsorted();
        if (filter.getSortBy() != null) {
            sort = Sort.by(
                    filter.getSortDirection() != null && filter.getSortDirection().equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC,
                    filter.getSortBy()
            );
        }

        if (filter.getCottonPartFrom() != null && filter.getCottonPartTo() != null) {
            return sockRepository.findByColorAndCottonPartBetween(
                    filter.getColor(),
                    filter.getCottonPartFrom(),
                    filter.getCottonPartTo()
            );
        }

        switch (filter.getOperation().toLowerCase()) {
            case "morethan":
                return sockRepository.findByColorAndCottonPartMoreThan(
                        filter.getColor(),
                        filter.getCottonPart()
                );
            case "lessthan":
                return sockRepository.findByColorAndCottonPartLessThan(
                        filter.getColor(),
                        filter.getCottonPart()
                );
            case "equal":
                return sockRepository.findByColorAndCottonPart(
                        filter.getColor(),
                        filter.getCottonPart()
                ).map(List::of).orElse(List.of());
            default:
                throw new InvalidOperationException("Неподдерживаемая операция: " + filter.getOperation());
        }
    }

    //Обновление информации о носках
    @Transactional
    public void update(Long id, SockDTO sockDTO) {
        log.info("Обновление носков с id {}: {}", id, sockDTO);

        Sock sock = sockRepository.findById(id)
                .orElseThrow(() -> new InvalidOperationException("Носки не найдены"));

        sock.setColor(sockDTO.getColor());
        sock.setCottonPart(sockDTO.getCottonPart());
        sock.setQuantity(sockDTO.getQuantity());

        sockRepository.save(sock);
    }

    //Загрузка партии носков из Excel файла
    @Transactional
    public void uploadBatch(MultipartFile file) throws IOException {
        log.info("Загрузка партии носков из файла: {}", file.getOriginalFilename());

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Пропускаем заголовок
                if (row.getRowNum() == 0) continue;

                try {
                    String color = row.getCell(0).getStringCellValue();
                    int cottonPart = (int) row.getCell(1).getNumericCellValue();
                    int quantity = (int) row.getCell(2).getNumericCellValue();

                    SockDTO sockDTO = new SockDTO();
                    sockDTO.setColor(color);
                    sockDTO.setCottonPart(cottonPart);
                    sockDTO.setQuantity(quantity);

                    income(sockDTO);

                } catch (Exception e) {
                    log.error("Ошибка при обработке строки {}: {}", row.getRowNum(), e.getMessage());
                    throw new InvalidOperationException("Ошибка при обработке файла в строке " + row.getRowNum());
                }
            }
        }
    }
}
