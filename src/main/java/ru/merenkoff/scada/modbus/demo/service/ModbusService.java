package ru.merenkoff.scada.modbus.demo.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

import net.wimpi.modbus.procimg.InputRegister;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ModbusService {
    private final Slave1PersistenceService persistenceService;

    private TCPMasterConnection connection;

    @PostConstruct
    public void init() {
        try {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            connection = new TCPMasterConnection(address);
            connection.setPort(502);
            connection.connect();
            System.out.println("Connected to Modbus");
        } catch (Exception e) {
            System.err.println("Error connecting to Modbus: " + e.getMessage());
        }
    }

    public Slave1Data extractSlave1DataFromModbus() {
        Slave1Data.Slave1DataBuilder builder = Slave1Data.builder();

        try {
            // Создаем запрос на чтение входных регистров
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 8);
            request.setUnitID(1);

            // Создаем транзакцию
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);

            // Выполняем транзакцию
            transaction.execute();

            // Получаем ответ
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
            InputRegister[] registers = response.getRegisters();

            //--------------

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

            //--------------
        } catch (Exception e) {
            System.err.println("Error reading registers: " + e.getMessage());
        }

        return builder.build();
    }

    @Scheduled(fixedRate = 5000) // Запускать каждые 5 секунд
    @Transactional
    public void monitorModbus() {
        Slave1Data extractedData = extractSlave1DataFromModbus();
        persistenceService.save(extractedData);
    }

    @PreDestroy
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from Modbus");
            }
        } catch (Exception e) {
            System.err.println("Error disconnecting from Modbus: " + e.getMessage());
        }
    }
}