package business.project.noodles.dto.ingredient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class IngredientOfSupplierResponse {
    String name_supplier;
    List<IngredientWarehouseResponse> ingredient_of_warehouse;
}
