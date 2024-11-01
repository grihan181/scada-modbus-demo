package ru.merenkoff.scada.modbus.demo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
public class ModbusService {
    private TCPMasterConnection connection;

    @PostConstruct
    @SneakyThrows
    public void init() {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        connection = new TCPMasterConnection(address);
        connection.setPort(502);
        connection.connect();
    }

    @PreDestroy
    public void close() {
        connection.close();
    }

    @SneakyThrows
    public Slave1Data extractSlave1Data() {
        InputRegister[] registers = getSlave1InputRegisters();
        return mapSlave1RegistersToSlave1Data(registers);
    }

    private static Slave1Data mapSlave1RegistersToSlave1Data(InputRegister[] registers) {
        Slave1Data.Slave1DataBuilder builder = Slave1Data.builder();
        // Считываем Word (16 бит)
        // Используем 32-битный integer, а не 16-битный short, потому что Word беззнаковый
        int wordValue = registers[0].getValue();
        builder.wordTag(wordValue);

        // Считываем Float (32 бита)
        int floatLow = registers[1].getValue();
        int floatHigh = registers[2].getValue();
        float floatValue = Float.intBitsToFloat((floatHigh << 16) | floatLow);
        builder.floatTag(floatValue);

        // Считываем ShortInt (8 бит)
        int shortIntRaw = registers[3].getValue();
        byte shortIntValue = (byte) (shortIntRaw & 0xFF);
        builder.shortIntTag(shortIntValue);

        // Считываем Integer (32 бита)
        int intLow = registers[4].getValue();
        int intHigh = registers[5].getValue();
        int integerValue = (intHigh << 16) | intLow;
        builder.integerTag(integerValue);

        // Считываем DWord (32-битное беззнаковое целое)
        // Используем 64-битный long, а не 32-битный integer, потому что DWord беззнаковый
        int dwordLow = registers[6].getValue();
        int dwordHigh = registers[7].getValue();
        long dwordValue = ((long) dwordHigh << 16) | dwordLow;
        builder.dwordTag(dwordValue);

        builder.timestamp(LocalDateTime.now());

        return builder.build();
    }

    @SneakyThrows
    private InputRegister[] getSlave1InputRegisters() {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 8);
        request.setUnitID(1);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);

        transaction.execute();

        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
        return response.getRegisters();
    }

}
