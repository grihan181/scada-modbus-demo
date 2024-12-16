package ru.avanesyan.scada.modbus.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.avanesyan.scada.modbus.demo.domain.Config;

import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DataCollectorService {
    private final ModbusService modbusService;
    private final SlavePersistenceService slavePersistenceService;
    private final ConfigService configService;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final ConcurrentMap<Config, Future<?>> tasks = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void monitorSlave1() {
        updateTasks(configService.getAll());
    }

    private void updateTasks(List<Config> configs) {
        System.out.println("updateTasks" + configs);
        for (Config config : configs) {
            if (!tasks.containsKey(config)) {
                Future<?> future = scheduler.scheduleAtFixedRate(() ->
                {
                    slavePersistenceService.save(modbusService.monitorServer(config));
                }, 0, 1, TimeUnit.SECONDS);
                tasks.put(config, future);
            }
        }

        tasks.keySet().forEach(config -> {
            if (!configs.contains(config)) {
                tasks.get(config).cancel(true);
                tasks.remove(config);
                modbusService.removeCon(config);
            }
        });
    }
}