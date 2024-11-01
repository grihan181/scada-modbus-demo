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
import ru.merenkoff.scada.modbus.demo.mapper.Slave1DataMapper;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class ModbusService {
    private final Slave1DataMapper mapper = Mappers.getMapper(Slave1DataMapper.class);
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
        return mapper.mapRegistersToSlave1Data(registers);
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
