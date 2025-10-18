package business.project.noodles.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAttributeResponse {
    String phone_number;
    String name_cus;
}
