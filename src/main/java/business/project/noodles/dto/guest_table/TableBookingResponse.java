package business.project.noodles.dto.guest_table;

import business.project.noodles.entity.GuestTable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TableBookingResponse {
    CustomerBookingResponse customerBookingResponse;
    OrderBookingResponse orderBookingResponse;
}
