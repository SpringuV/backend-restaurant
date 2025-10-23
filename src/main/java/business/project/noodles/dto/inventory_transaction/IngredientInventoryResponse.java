package business.project.noodles.dto.inventory_transaction;

import business.project.noodles.entity.Ingredient;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientInventoryResponse {
    String id_ingredient;
    String name_ingredients;
    Double prices;
    Ingredient.UnitOfMeasurement unit_of_measurement;
    String supplier;
}
