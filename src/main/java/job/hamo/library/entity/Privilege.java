package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}