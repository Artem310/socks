package com.warehouse.socks.dto;

import lombok.Data;
import javax.validation.constraints.*;

//DTO для операций с носками
@Data
public class SockDTO {

    @NotBlank(message = "Цвет не может быть пустым")
    private String color;

    @Min(value = 0, message = "Процент хлопка не может быть меньше 0")
    @Max(value = 100, message = "Процент хлопка не может быть больше 100")
    private Integer cottonPart;

    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer quantity;
}
