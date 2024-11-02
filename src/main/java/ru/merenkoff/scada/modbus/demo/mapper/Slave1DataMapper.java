package ru.merenkoff.scada.modbus.demo.mapper;

import net.wimpi.modbus.procimg.InputRegister;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

@Mapper
public interface Slave1DataMapper {

    @Mapping(target = "wordTag", source = ".", qualifiedByName = "extractWordTagFromRegisters")
    @Mapping(target = "floatTag", source = ".", qualifiedByName = "extractFloatTagFromRegisters")
    @Mapping(target = "shortIntTag", source = ".", qualifiedByName = "extractShortIntTagFromRegisters")
    @Mapping(target = "integerTag", source = ".", qualifiedByName = "extractIntegerTagFromRegisters")
    @Mapping(target = "dwordTag", source = ".", qualifiedByName = "extractDwordTagFromRegisters")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    Slave1Data mapRegistersToData(InputRegister[] registers);

    @Named("extractWordTagFromRegisters")
    default Integer extractWordTagFromRegisters(InputRegister[] registers) {
        return registers[0].getValue();
    }

    @Named("extractFloatTagFromRegisters")
    default Float extractFloatTagFromRegisters(InputRegister[] registers) {
        int floatLow = registers[1].getValue();
        int floatHigh = registers[2].getValue();
        return Float.intBitsToFloat((floatHigh << 16) | floatLow);
    }

    @Named("extractShortIntTagFromRegisters")
    default byte extractShortIntTagFromRegisters(InputRegister[] registers) {
        return (byte) (registers[3].getValue() & 0xFF);
    }

    @Named("extractIntegerTagFromRegisters")
    default int extractIntegerTagFromRegisters(InputRegister[] registers) {
        int intLow = registers[4].getValue();
        int intHigh = registers[5].getValue();
        return (intHigh << 16) | intLow;
    }

    @Named("extractDwordTagFromRegisters")
    default long extractDwordTagFromRegisters(InputRegister[] registers) {
        int dwordLow = registers[6].getValue();
        int dwordHigh = registers[7].getValue();
        return ((long) dwordHigh << 16) | dwordLow;
    }
}
