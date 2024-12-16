package ru.avanesyan.scada.modbus.demo.mapper;

import net.wimpi.modbus.procimg.InputRegister;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.avanesyan.scada.modbus.demo.domain.Data;

@Mapper
public interface SlaveDataMapper {

    @Mapping(target = "integerTag", source = ".", qualifiedByName = "extractIntegerTagFromRegisters")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    Data mapRegistersToData(InputRegister[] registers);

    @Named("extractIntegerTagFromRegisters")
    default int extractIntegerTagFromRegisters(InputRegister[] registers) {
        int intLow = registers[0].getValue();
        int intHigh = registers[1].getValue();
        int a = (intHigh << 16) | intLow;
        System.out.println("extractIntegerTagFromRegisters" + a);
        return a;
    }


}
