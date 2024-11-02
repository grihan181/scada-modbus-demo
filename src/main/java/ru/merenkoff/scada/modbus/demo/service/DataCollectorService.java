package ru.merenkoff.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;
import ru.merenkoff.scada.modbus.demo.domain.Slave2Data;

@Service
@RequiredArgsConstructor
public class DataCollectorService {
    private final ModbusService modbusService;
    private final Slave1PersistenceService slave1PersistenceService;
    private final Slave2PersistenceService slave2PersistenceService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void monitorSlave1() {
        Slave1Data extractedData = modbusService.extractSlave1Data();
        slave1PersistenceService.save(extractedData);
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void monitorSlave2() {
        Slave2Data extractedData = modbusService.extractSlave2Data();
        slave2PersistenceService.save(extractedData);
    }
}