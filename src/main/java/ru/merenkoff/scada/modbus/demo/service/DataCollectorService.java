package ru.merenkoff.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

@Service
@RequiredArgsConstructor
public class DataCollectorService {
    private final ModbusService modbusService;
    private final Slave1PersistenceService persistenceService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void monitorModbus() {
        Slave1Data extractedData = modbusService.extractSlave1Data();
        persistenceService.save(extractedData);
    }
}