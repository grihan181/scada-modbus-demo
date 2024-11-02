package ru.merenkoff.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.merenkoff.scada.modbus.demo.domain.Slave2Data;
import ru.merenkoff.scada.modbus.demo.repository.Slave2DataRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Slave2PersistenceService {
    private final Slave2DataRepository repository;

    private Slave2Data lastData;

    @Transactional
    public void save(Slave2Data slave2Data) {
        if (hasSignificantChange(slave2Data, lastData)) {
            repository.save(slave2Data);
            lastData = slave2Data;
        }
    }

    public List<Slave2Data> getAll() {
        return repository.findAll();
    }

    private boolean hasSignificantChange(Slave2Data newData, Slave2Data oldData) {
        if (oldData == null) {
            return true;
        }

        return !Objects.equals(oldData.getInt64Tag(), newData.getInt64Tag()) ||
                !Objects.equals(oldData.getDateTimeTag(), newData.getDateTimeTag()) ||
                !Objects.equals(oldData.getDoubleTag(), newData.getDoubleTag()) ||
                !Objects.equals(oldData.getStringTag(), newData.getStringTag()) ||
                !Objects.equals(oldData.getBoolTag(), newData.getBoolTag());
    }
}
