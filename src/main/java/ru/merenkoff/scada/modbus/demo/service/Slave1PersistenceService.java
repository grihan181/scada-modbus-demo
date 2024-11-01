package ru.merenkoff.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;
import ru.merenkoff.scada.modbus.demo.repository.Slave1DataRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Slave1PersistenceService {
    private final Slave1DataRepository repository;

    private Slave1Data lastData;

    @Transactional
    public void save(Slave1Data slave1Data) {
        if (hasSignificantChange(slave1Data, lastData)) {
            repository.save(slave1Data);
            lastData = slave1Data;
        }
    }

    public List<Slave1Data> getAll() {
        return repository.findAll();
    }

    private boolean hasSignificantChange(Slave1Data newData, Slave1Data oldData) {
        if (oldData == null) {
            return true;
        }

        return !Objects.equals(oldData.getWordTag(), newData.getWordTag()) ||
                !Objects.equals(oldData.getFloatTag(), newData.getFloatTag()) ||
                !Objects.equals(oldData.getShortIntTag(), newData.getShortIntTag()) ||
                !Objects.equals(oldData.getIntegerTag(), newData.getIntegerTag()) ||
                !Objects.equals(oldData.getDwordTag(), newData.getDwordTag());
    }
}
