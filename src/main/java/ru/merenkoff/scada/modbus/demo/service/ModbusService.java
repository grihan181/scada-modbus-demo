package ru.merenkoff.scada.modbus.demo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;
import ru.merenkoff.scada.modbus.demo.domain.Slave2Data;
import ru.merenkoff.scada.modbus.demo.mapper.Slave1DataMapper;
import ru.merenkoff.scada.modbus.demo.mapper.Slave2DataMapper;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class ModbusService {
    private final Slave1DataMapper slave1DataMapper = Mappers.getMapper(Slave1DataMapper.class);
    private final Slave2DataMapper slave2DataMapper = Mappers.getMapper(Slave2DataMapper.class);
    private TCPMasterConnection modbusServerConnection;

    @PostConstruct
    @SneakyThrows
    public void init() {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        modbusServerConnection = new TCPMasterConnection(address);
        modbusServerConnection.setPort(502);
        modbusServerConnection.connect();
    }

    @PreDestroy
    public void close() {
        modbusServerConnection.close();
    }

    @SneakyThrows
    public Slave1Data extractSlave1Data() {
        InputRegister[] registers = getSlave1InputRegisters();
        return slave1DataMapper.mapRegistersToData(registers);
    }

    @SneakyThrows
    public Slave2Data extractSlave2Data() {
        InputRegister[] registers = getSlave2InputRegisters();
        return slave2DataMapper.mapRegistersToData(registers);
    }

    @SneakyThrows
    private InputRegister[] getSlave1InputRegisters() {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 8);
        request.setUnitID(1);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(modbusServerConnection);
        transaction.setRequest(request);

        transaction.execute();

        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
        return response.getRegisters();
    }

    @SneakyThrows
    private InputRegister[] getSlave2InputRegisters() {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 18);
        request.setUnitID(2);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(modbusServerConnection);
        transaction.setRequest(request);

        transaction.execute();

        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
        return response.getRegisters();
    }
}
