package com.warehouse.socks.dto;

import lombok.Data;


//DTO для фильтрации носков
@Data
public class SockFilterDTO {
    private String color;
    private String operation; // moreThan, lessThan, equal
    private Integer cottonPart;
    private Integer cottonPartFrom; // для диапазона
    private Integer cottonPartTo; // для диапазона
    private String sortBy; // color, cottonPart
    private String sortDirection; // asc, desc
}
