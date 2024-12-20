package com.warehouse.socks.repository;

import com.warehouse.socks.entity.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//Репозиторий для работы с носками
@Repository
public interface SockRepository extends JpaRepository<Sock, Long> {

    //Поиск носков по цвету и проценту хлопка
    Optional<Sock> findByColorAndCottonPart(String color, Integer cottonPart);

    //Поиск носков с процентом хлопка больше указанного
    @Query("SELECT s FROM Sock s WHERE s.color = :color AND s.cottonPart > :cottonPart")
    List<Sock> findByColorAndCottonPartMoreThan(
            @Param("color") String color,
            @Param("cottonPart") Integer cottonPart
    );

    //Поиск носков с процентом хлопка меньше указанного
    @Query("SELECT s FROM Sock s WHERE s.color = :color AND s.cottonPart < :cottonPart")
    List<Sock> findByColorAndCottonPartLessThan(
            @Param("color") String color,
            @Param("cottonPart") Integer cottonPart
    );

    //Поиск носков в диапазоне процента хлопка
    @Query("SELECT s FROM Sock s WHERE s.color = :color AND s.cottonPart BETWEEN :fromPart AND :toPart")
    List<Sock> findByColorAndCottonPartBetween(
            @Param("color") String color,
            @Param("fromPart") Integer fromPart,
            @Param("toPart") Integer toPart
    );
}
