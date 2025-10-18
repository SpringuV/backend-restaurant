package business.project.noodles.dto.food;

import business.project.noodles.entity.Food;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FoodResponse {

    String id_food;
    String name_food;
    double prices;
    String description;
    String image_path;
    Food.FoodType type_food;
    boolean is_available;
}
