package business.project.noodles.dto.guest_table;

import business.project.noodles.dto.food.FoodResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderItemResponse {
    String id_food;// food
    String name_food;// food
    Double price;// food
    String note_special;
    String image_url;
    Integer quantity;
}
