package business.project.noodles.repository;

import business.project.noodles.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {


    @Query("SELECT i FROM Invoice i WHERE i.orders.id_order=:id_order")
    Optional<Invoice> findByIdOrder(@Param("id_order") Long id_order);

}
