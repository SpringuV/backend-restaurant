package business.project.noodles.dto.ingredient;

import business.project.noodles.entity.Ingredient;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class IngredientCreateRequest {
    String name_ingredients;
    Ingredient.UnitOfMeasurement unit_of_measurement;
    String description;
    String supplier;
    double prices;
}
