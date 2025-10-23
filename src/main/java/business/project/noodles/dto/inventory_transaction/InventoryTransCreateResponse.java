package business.project.noodles.dto.inventory_transaction;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransCreateResponse {

    Boolean is_created;
    String message;

}
