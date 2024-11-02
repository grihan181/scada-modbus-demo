package ru.merenkoff.scada.modbus.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.merenkoff.scada.modbus.demo.domain.Slave2Data;

@Repository
public interface Slave2DataRepository extends JpaRepository<Slave2Data, Long> {

}
