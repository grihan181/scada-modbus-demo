package ru.merenkoff.scada.modbus.demo.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Slave1Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Уникальный идентификатор записи (будет автоинкрементироваться)

    private Integer wordTag;          // Значение Word (16 бит беззнаковое целое)
    private Float floatTag;           // Значение Float (32 бита)
    private Byte shortIntTag;         // Значение ShortInt (8 бит)
    private Integer integerTag;       // Значение Integer (32 бита)
    private Long dwordTag;            // Значение DWord (32-битное беззнаковое целое)

    private LocalDateTime timestamp;  // Время считывания данных
}
