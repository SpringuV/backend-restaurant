package business.project.noodles.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CustomerCreateRequest {
    String phone_number;
    String name_cus;
    boolean sex_cus;
    String description;
}
