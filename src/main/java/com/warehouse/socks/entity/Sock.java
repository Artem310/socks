package com.warehouse.socks.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

//Сущность, представляющая носки на складе
@Entity
@Table(name = "socks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Цвет носков
    @NotBlank(message = "Цвет не может быть пустым")
    @Column(nullable = false)
    private String color;

    //Процентное содержание хлопка
    @Min(value = 0, message = "Процент хлопка не может быть меньше 0")
    @Max(value = 100, message = "Процент хлопка не может быть больше 100")
    @Column(name = "cotton_part", nullable = false)
    private Integer cottonPart;

    //Количество носков данного типа
    @Min(value = 0, message = "Количество не может быть отрицательным")
    @Column(nullable = false)
    private Integer quantity;
}
