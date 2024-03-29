package com.prorental.carrental.domain;


import com.prorental.carrental.enumaration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import javax.persistence.Id;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(max = 15)
    @NotNull(message = "Please enter your first name")
    @Column(nullable = false, length = 15)
    private String firstName;

    @Size(max = 15)
    @NotNull(message = "Please enter your last name")
    @Column(nullable = false, length = 15)
    private String lastName;

    @Size(min = 4, max = 60, message = "Please enter min 4 characters")
    @NotNull(message = "Please enter your password")
    @Column(nullable = false, length = 255)
    private String password;

//    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
//            message = "Please enter valid phone number")
    @Size(min = 10, max= 14, message = "Phone number should be exact 14 characters")
    @NotNull(message = "Please enter your phone number")
    @Column(nullable = false, length = 14)
    private String phoneNumber;

    @Email(message = "Please enter valid email")
    @Size(min = 5, max = 150)
    @NotNull(message = "Please enter your email")
    @Column(nullable = false, unique = true, length = 150)
    private String email;


    @Size(max = 250)
    @NotNull(message = "Please enter your address")
    @Column(nullable = false, length = 250)
    private String address;


    @Size(max = 15)
    @NotNull(message = "Please enter your zip code")
    @Column(nullable = false, length = 15)
    private String zipCode;

    @ManyToMany(fetch = FetchType.LAZY) //Here we can specify @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL will delete all roles with the user.
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
     inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean builtIn;





    public User(String firstName, String lastName, String password, String phoneNumber, String email, String address, String zipCode){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
    }

    public User(String firstName, String lastName, String password, String phoneNumber, String email, String address, String zipCode,
                Set<Role> roles, Boolean builtIn){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
        this.roles = roles;
        this.builtIn = builtIn;
    }


    public String getFullName(){
        return getFirstName() + " " + getLastName();
    }

    //This is used in UserDetailsImpl
    public Set<Role> getRole(){
        return roles;
    }

    //We changed Roles like Role_admin, Role_customer to administrator and customer in ResponseEntity.
    public Set<String> getRoles(){
        Set<String> roleStr = new HashSet<>();
//        Role[] role = roles.toArray(new Role[roles.size()]);
//        for(int i = 0;i < roles.size(); i++){
//            if(role[i].getName().equals(UserRole.ROLE_ADMIN)){
//                roleStr.add("Administrator");
//            } else {
//                roleStr.add("Customer");
//            }
//        }
        for(Role role: roles){
            if( role.getName().equals(UserRole.ROLE_ADMIN)){
                roleStr.add("Administrator");
            }else{
                roleStr.add("Customer");
            }
        }

        return roleStr;
    }


}
