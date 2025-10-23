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
public class IngredientUpdateRequest {
    Ingredient.UnitOfMeasurement unit_of_measurement;
    Double prices;
    String description;
    String supplier;
}
