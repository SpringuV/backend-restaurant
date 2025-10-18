package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.dto.User.UserAttributeResponse;
import business.project.noodles.dto.ingredient.IngredientAttributeResponse;
import business.project.noodles.entity.InventoryTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransCreateResponse {
    int quantity; // số lượng nhập/xuất
    InventoryTransaction.TypeInventoryTransaction type; // "IMPORT" hoặc "EXPORT"
    UserAttributeResponse perform_by_user;
    String note; // tùy chọn
    IngredientAttributeResponse ingredientAttributeResponse;
}
