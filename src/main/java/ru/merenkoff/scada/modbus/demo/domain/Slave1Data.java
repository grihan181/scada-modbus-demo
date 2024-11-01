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
    private Long id;

    private Integer wordTag;
    private Float floatTag;
    private Byte shortIntTag;
    private Integer integerTag;
    private Long dwordTag;

    private LocalDateTime timestamp;
}
