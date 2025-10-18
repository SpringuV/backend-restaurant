package business.project.noodles.repository;

import business.project.noodles.entity.GuestTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestTableRepository extends JpaRepository<GuestTable, Integer> {
}
