package ru.avanesyan.scada.modbus.demo.service;

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
import ru.avanesyan.scada.modbus.demo.domain.Config;
import ru.avanesyan.scada.modbus.demo.domain.Data;
import ru.avanesyan.scada.modbus.demo.mapper.SlaveDataMapper;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ModbusService {
    private final SlaveDataMapper dataMapper = Mappers.getMapper(SlaveDataMapper.class);
    private final ConcurrentMap<Config, TCPMasterConnection> connections = new ConcurrentHashMap<>();

    @SneakyThrows
    public Data monitorServer(Config config) {
        if (!connections.containsKey(config)) {
            InetAddress address = InetAddress.getByName(config.getIp());
            TCPMasterConnection connection = new TCPMasterConnection(address);
            connection.setPort(config.getPort());
            connection.connect();

            connections.put(config, connection);
        }

        TCPMasterConnection connection = connections.get(config);
        Data test = extractSlaveData(connection);
        test.setConfig(config);
        System.out.println("monitorServer " + test);
        return test;
    }

    public void removeCon(Config config) {
        connections.get(config).close();
    }

    @SneakyThrows
    private Data extractSlaveData(TCPMasterConnection tCPMasterConnection) {
        InputRegister[] registers = getInputRegisters(tCPMasterConnection);
        return dataMapper.mapRegistersToData(registers);
    }

    @SneakyThrows
    private InputRegister[] getInputRegisters(TCPMasterConnection tCPMasterConnection) {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 2);
        request.setUnitID(1);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(tCPMasterConnection);
        transaction.setRequest(request);

        transaction.execute();

        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
        return response.getRegisters();
    }

    @PreDestroy
    public void close() {
        connections.forEach((c, tcp) -> tcp.close());
    }
}
