package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.dto.User.UserAttributeResponse;
import business.project.noodles.entity.Ingredient;
import business.project.noodles.entity.InventoryTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransAttributeResponse {
    int quantity; // số lượng nhập/xuất
    InventoryTransaction.TypeInventoryTransaction type; // "IMPORT" hoặc "EXPORT"
    String note; // tùy chọn
    UserAttributeResponse userAttributeResponse;
    String id_ingredient;
    Ingredient.UnitOfMeasurement unit_of_measurement;
}
