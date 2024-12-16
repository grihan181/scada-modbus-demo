package ru.avanesyan.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avanesyan.scada.modbus.demo.domain.Data;
import ru.avanesyan.scada.modbus.demo.repository.SlaveDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlavePersistenceService {
    private final SlaveDataRepository repository;

    private Data lastData;

    @Transactional
    public void save(Data data) {
            repository.save(data);
            lastData = data;
    }

    public List<Data> getAll() {
        return repository.findAll();
    }
}
