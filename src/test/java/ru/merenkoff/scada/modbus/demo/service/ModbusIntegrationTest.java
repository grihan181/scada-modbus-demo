package ru.merenkoff.scada.modbus.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ModbusIntegrationTest {

    @Autowired
    private ModbusService modbusService;

    @Autowired
    private Slave1PersistenceService persistenceService;

    @Test
    public void testSlave1DataExtractSaveGet() {
        // Извлечение данных из Modbus
        Slave1Data extractedData = modbusService.extractSlave1Data();

        // Убедимся, что данные не null перед сохранением
        assertNotNull(extractedData, "Extracted Slave1Data should not be null");

        // Сохранение данных
        persistenceService.save(extractedData);

        // Получение всех записей из базы данных
        List<Slave1Data> allDataFromDBList = persistenceService.getAll();

        // Проверка, что размер списка увеличился на 1
        assertEquals(1, allDataFromDBList.size(), "There should be one record in the database");

        // Проверка, что сохраненные данные совпадают с извлеченными данными
        Slave1Data dataFromDBList = allDataFromDBList.get(0);
        assertEquals(extractedData.getWordTag(), dataFromDBList.getWordTag(), "Field values should match");
        assertEquals(extractedData.getFloatTag(), dataFromDBList.getFloatTag(), "Field values should match");
        assertEquals(extractedData.getShortIntTag(), dataFromDBList.getShortIntTag(), "Field values should match");
        assertEquals(extractedData.getIntegerTag(), dataFromDBList.getIntegerTag(), "Field values should match");
        assertEquals(extractedData.getDwordTag(), dataFromDBList.getDwordTag(), "Field values should match");
        assertEquals(
                extractedData.getTimestamp().truncatedTo(ChronoUnit.MILLIS),
                dataFromDBList.getTimestamp().truncatedTo(ChronoUnit.MILLIS),
                "Field values should match"
        );
    }
}
