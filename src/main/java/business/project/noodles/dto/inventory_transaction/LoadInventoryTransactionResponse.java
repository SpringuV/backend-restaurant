package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.entity.Ingredient;
import business.project.noodles.entity.InventoryTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadInventoryTransactionResponse {

    Long id;
    Integer quantity;
    InventoryTransaction.TypeInventoryTransaction type;
    String note;
    Instant created_at;
    UserInventoryResponse user_response;
    IngredientInventoryResponse ingredient_response;

}
