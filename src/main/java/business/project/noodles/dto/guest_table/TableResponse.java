package business.project.noodles.dto.guest_table;

import business.project.noodles.entity.GuestTable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TableResponse {
    GuestTable.TypeTable type;
    boolean is_available;
}
