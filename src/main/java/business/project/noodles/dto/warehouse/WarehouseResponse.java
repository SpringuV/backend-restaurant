package business.project.noodles.dto.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    String code_warehouse;
    String name_warehouse;
    String address_warehouse;
}
