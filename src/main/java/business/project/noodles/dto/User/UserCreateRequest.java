package business.project.noodles.dto.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserCreateRequest {
    String username;
    String phone_number;
    String pass;
    String full_name;
}
