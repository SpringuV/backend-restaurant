package business.project.noodles.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDataUpdateRequest {
    Integer id_order;
    List<Integer> table_id_list;

}
