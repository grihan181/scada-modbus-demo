package ru.merenkoff.scada.modbus.demo.mapper;

import net.wimpi.modbus.procimg.InputRegister;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.merenkoff.scada.modbus.demo.domain.Slave2Data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Mapper
public interface Slave2DataMapper {
    @Mapping(target = "int64Tag", source = ".", qualifiedByName = "extractInt64TagFromRegisters")
    @Mapping(target = "dateTimeTag", source = ".", qualifiedByName = "extractDateTimeTagFromRegisters")
    @Mapping(target = "doubleTag", source = ".", qualifiedByName = "extractDoubleTagFromRegisters")
    @Mapping(target = "stringTag", source = ".", qualifiedByName = "extractStringTagFromRegisters")
    @Mapping(target = "boolTag", source = ".", qualifiedByName = "extractBoolTagFromRegisters")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    Slave2Data mapRegistersToData(InputRegister[] registers);

    @Named("extractInt64TagFromRegisters")
    default Long extractInt64TagFromRegisters(InputRegister[] registers) {
        return ((long) registers[3].getValue() << 48) |
                ((long) registers[2].getValue() << 32) |
                ((long) registers[1].getValue() << 16) |
                (long) registers[0].getValue();
    }

    @Named("extractDateTimeTagFromRegisters")
    default Instant extractDateTimeTagFromRegisters(InputRegister[] registers) {
        // Преобразуем 4 регистра в 64-битное целое число
        long bits = ((long) registers[7].getValue() << 48) |
                ((long) registers[6].getValue() << 32) |
                ((long) registers[5].getValue() << 16) |
                (registers[4].getValue() & 0xFFFFL);

        // Преобразуем 64-битное значение в double
        double floatingPointValue = Double.longBitsToDouble(bits);

        // Базовая дата (30.12.1899) в формате UTC
        Instant baseInstant = Instant.parse("1899-12-30T00:00:00Z");

        // Извлекаем целую часть (дни) и дробную часть (доля суток)
        int days = (int) floatingPointValue;
        double fractionOfDay = floatingPointValue - days;

        // Вычисляем количество секунд, прошедших за текущие сутки
        long secondsInDay = 24 * 60 * 60;
        long seconds = Math.round(fractionOfDay * secondsInDay);

        // Возвращаем восстановленный Instant
        return baseInstant.plus(days, ChronoUnit.DAYS).plus(seconds, ChronoUnit.SECONDS);
    }

@Named("extractDoubleTagFromRegisters")
default Double extractDoubleTagFromRegisters(InputRegister[] registers) {
    long doubleBits = ((long) registers[11].getValue() << 48) |
            ((long) registers[10].getValue() << 32) |
            ((long) registers[9].getValue() << 16) |
            (long) registers[8].getValue();
    return Double.longBitsToDouble(doubleBits);
}

@Named("extractStringTagFromRegisters")
default String extractStringTagFromRegisters(InputRegister[] registers) {
    StringBuilder sb = new StringBuilder();
    for (int i = 12; i <= 16; i++) {
        int value = registers[i].getValue();
        char highChar = (char) ((value >> 8) & 0xFF);
        char lowChar = (char) (value & 0xFF);
        sb.append(highChar).append(lowChar);
    }
    return sb.toString().trim();
}

@Named("extractBoolTagFromRegisters")
default Boolean extractBoolTagFromRegisters(InputRegister[] registers) {
    return registers[17].getValue() != 0;
}
}

