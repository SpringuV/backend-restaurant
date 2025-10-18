package business.project.noodles.dto.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleUpdateRequest {
    private String role_name_req;
    private String description_req;
    private List<String> per_string_list_req;
}
