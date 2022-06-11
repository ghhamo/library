package job.hamo.library.entity;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence")
    @SequenceGenerator(name = "user_id_sequence", sequenceName = "user_id_sequence", allocationSize = 50000)
    private Long id;

    private String name;

    private String surname;

    private String email;

    private String password;

    private boolean enabled;

    private String country;

    private String region;

    private String city;

    @Column(columnDefinition = "integer default 0")
    private int age;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<BookList> bookLists;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<BookCollection> bookCollections;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Rating> ratedBooks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Rating> getRatedBooks() {
        return ratedBooks;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
