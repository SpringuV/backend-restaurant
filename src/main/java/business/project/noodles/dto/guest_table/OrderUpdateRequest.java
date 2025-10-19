package business.project.noodles.dto.guest_table;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateRequest {
    List<OrderItemCreateRequest> food_items;
    Long id_order;
    Integer id_table;
    String note_order;
    String order_status;
    String phone_number;
    Double total_amount;
}
