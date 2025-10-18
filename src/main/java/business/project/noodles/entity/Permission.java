package business.project.noodles.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Builder
@Setter
@Table(name = "permissions")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Permission {

    @Id
    @Column(name = "permission_name")
    String permission_name;


    @Column(name = "description")
    String description;

    @ManyToMany(mappedBy = "permission_set")
    Set<Role> role_set;
}
