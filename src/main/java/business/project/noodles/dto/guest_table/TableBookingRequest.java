package business.project.noodles.dto.guest_table;

import business.project.noodles.entity.GuestTable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TableBookingRequest {
    int id_table;
    String customer_name;
    String phone_cus;
    Integer sum_human;
    String note_booking;
    String user_id;
}
