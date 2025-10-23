package business.project.noodles.repository;

import business.project.noodles.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, String> {

    @Query("SELECT i FROM Ingredient i WHERE i.supplier=:supplier AND i.name_ingredients=:name_ingredient")
    Optional<Ingredient> findBySupplierAndNameIngredient(@Param("supplier") String supplier, @Param("name_ingredient")String name_ingredient);

    @Query("SELECT DISTINCT(i.supplier) FROM Ingredient i WHERE i.supplier IS NOT NULL")
    List<String> getListSupplier();

    @Query("SELECT i FROM Ingredient i WHERE i.supplier=:supplier")
    List<Ingredient> getListBySupplier(@Param("supplier") String supplier);
}
