package example.users;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

/**
 * A form for validation and the creation of {@link User} entities
 *
 * @author Mason Traylor
 */
@Data
public class UserForm {

    private Long id;

    @NotNull
    @Size(min=2, max=30)
    private String firstname;

    @NotNull
    @Size(min=2, max=30)
    private String lastname;

    @NotNull
    @Pattern(regexp="\\(?\\d{3}\\)?-?\\d{3}-?\\d{4}")
    private String telephone;

    @NotNull
    @Email
    private String email;

    @Data
    public class AddressForm {
        @NotNull
        @Size(min = 2, max = 30)
        private String street;

        @NotNull
        @Size(min = 2, max = 30)
        private String city;

        @NotNull
        @Size(min = 2, max = 30)
        private String state;

        @NotNull
        @Pattern(regexp = "\\d{5}(-\\d+)?")
        private String zip;

        public User.Address makeAddress() {
            return new User.Address(street, city, state, zip);
        }
    }

    @Valid
    private AddressForm address = new AddressForm();

    public User makeUser() {
        User user = new User();

        if (id!=null)
            user.setId(id);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setAddress(address.makeAddress());

        return user;
    }
}
