package business.project.noodles.dto.guest_table;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TableResponse {
    int id_table;
    int capacity;
    boolean available;
}
