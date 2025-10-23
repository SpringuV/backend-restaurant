package business.project.noodles.repository;

import business.project.noodles.entity.InventoryTransaction;
import business.project.noodles.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    @Query("SELECT w FROM Warehouse w WHERE w.code_warehouse=:code")
    Optional<Warehouse> findByCodeWarehouse(@Param("code") String codewarehouse);


}
