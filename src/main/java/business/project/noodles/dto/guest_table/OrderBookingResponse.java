package business.project.noodles.dto.guest_table;

import business.project.noodles.entity.Orders;
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
public class OrderBookingResponse {
    String id_order;
    String note_order;
    String order_status;
    Instant created_at;
    List<Integer> table_id_list;
}
