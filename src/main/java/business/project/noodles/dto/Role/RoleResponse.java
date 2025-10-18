package business.project.noodles.dto.Role;

import business.project.noodles.dto.Permission.PermissionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RoleResponse {
    String role_name_res;
    String description_res;
    List<PermissionResponse> permission_response_list;
}
