package ru.avanesyan.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.avanesyan.scada.modbus.demo.domain.Config;
import ru.avanesyan.scada.modbus.demo.repository.ConfigRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository repository;

    @Transactional
    @CachePut(value = "configs")
    public List<Config> saveAndUpdateCache(Config slaveData) {
        repository.save(slaveData);
        return repository.findAll();
    }

    @Transactional
    @CachePut(value = "configs")
    public List<Config> deleteAndUpdateCache(Long id) {
        repository.deleteById(id);
        return repository.findAll();
    }

    @Cacheable("configs")
    public List<Config> getAll() {
        return repository.findAll();
    }
}
