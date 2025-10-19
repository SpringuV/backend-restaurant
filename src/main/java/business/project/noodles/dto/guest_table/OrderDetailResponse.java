package business.project.noodles.dto.guest_table;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderDetailResponse {
    Long id_order;
    Integer id_table;
    String phone_cus;
    String name_cus;
    Instant created_at;
    Integer sum_human;
    String note_order;
    String order_status;
    Double total_amount;
    List<OrderItemResponse> order_item_list_response;
}
