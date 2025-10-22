package business.project.noodles.dto.ingredient;

import business.project.noodles.entity.Ingredient;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class IngredientResponse {
    String id_ingredient;
    String name_ingredients;
    Double prices;
    Integer quantity;
    Ingredient.UnitOfMeasurement unit_of_measurement;
    String description;
    String supplier;
    Instant created_at;
    Instant updated_at;
}
