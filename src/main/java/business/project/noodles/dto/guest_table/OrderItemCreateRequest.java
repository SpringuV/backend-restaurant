package business.project.noodles.dto.guest_table;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderItemCreateRequest {
    String id_food;
    String note;
    Integer quantity;
}
