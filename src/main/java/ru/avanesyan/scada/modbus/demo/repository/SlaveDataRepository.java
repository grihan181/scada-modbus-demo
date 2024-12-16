package ru.avanesyan.scada.modbus.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.avanesyan.scada.modbus.demo.domain.Data;

@Repository
public interface SlaveDataRepository extends JpaRepository<Data, Long> {

}
