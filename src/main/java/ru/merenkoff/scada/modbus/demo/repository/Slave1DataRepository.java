package ru.merenkoff.scada.modbus.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.merenkoff.scada.modbus.demo.domain.Slave1Data;

@Repository
public interface Slave1DataRepository extends JpaRepository<Slave1Data, Long> {
    // Здесь можно добавить дополнительные методы, если это необходимо
}
