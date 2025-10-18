package business.project.noodles.dto.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    String username;
    String phone_number;
    String full_name;
    String role;
    String status;
    Instant created_at;
}
