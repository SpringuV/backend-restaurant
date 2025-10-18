package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.entity.Ingredient;
import business.project.noodles.entity.InventoryTransaction;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransCreateRequest {
    int quantity; // số lượng nhập/xuất
    InventoryTransaction.TypeInventoryTransaction type; // "IMPORT" hoặc "EXPORT"
    String note; // tùy chọn
    String id_user;
    String id_ingredient;
    Ingredient.UnitOfMeasurement unit_of_measurement;
}
