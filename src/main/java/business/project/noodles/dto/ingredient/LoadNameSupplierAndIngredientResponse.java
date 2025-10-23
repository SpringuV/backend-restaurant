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
public class LoadNameSupplierAndIngredientResponse {
    List<IngredientOfSupplierResponse> list_name_supplier_and_ingredient_response;
}
