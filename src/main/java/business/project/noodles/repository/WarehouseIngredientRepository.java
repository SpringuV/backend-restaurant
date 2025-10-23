package business.project.noodles.repository;

import business.project.noodles.entity.WarehouseIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseIngredientRepository extends JpaRepository<WarehouseIngredient, String> {
    // Lấy danh sách theo kho
    @Query("SELECT wi FROM WarehouseIngredient wi WHERE wi.warehouse.code_warehouse = :code_warehouse")
    List<WarehouseIngredient> findByWarehouseCode(@Param("code_warehouse") String code_warehouse);

    // Lấy nhanh số lượng tồn theo nguyên liệu và kho
    @Query("SELECT wi.quantity FROM WarehouseIngredient wi WHERE wi.warehouse.id_warehouse = :warehouseId AND wi.ingredient.id_ingredient = :ingredientId")
    Integer findQuantityByWarehouseAndIngredient(@Param("warehouseId") String warehouseId,
                                                 @Param("ingredientId") String ingredientId);

    // tìm bằng kho và nguyên liệu
    @Query("SELECT wi FROM WarehouseIngredient wi WHERE wi.warehouse.id_warehouse = :warehouseId AND wi.ingredient.id_ingredient = :ingredientId")
    Optional<WarehouseIngredient> findByWarehouseAndIngredient(@Param("warehouseId") String warehouseId,
                                          @Param("ingredientId") String ingredientId);

    @Query("SELECT wi FROM WarehouseIngredient wi WHERE wi.ingredient.supplier IN :suppliers")
    List<WarehouseIngredient> findByIngredientSupplierIn(@Param("suppliers") List<String> suppliers);

}
