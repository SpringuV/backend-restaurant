package business.project.noodles.repository;

import business.project.noodles.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, String> {

    @Query("SELECT i FROM Ingredient i WHERE i.supplier=:supplier AND i.name_ingredients=:name_ingredient")
    Ingredient findBySupplierAndNameIngredient(@Param("supplier") String supplier, @Param("name_ingredient")String name_ingredient);

}
