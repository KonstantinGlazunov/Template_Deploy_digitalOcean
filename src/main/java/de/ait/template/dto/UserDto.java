package de.ait.template.dto;

import de.ait.template.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "User", description = "Данные пользователя")
public class UserDto {

    @Schema(description = "идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "имя пользователя", example = "Marsel")
    private String firstName;

    @Schema(description = "фамилия пользователя", example = "Sidikov")
    private String lastName;

    @Schema(description = "Email пользователя", example = "user@mail.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;

    @Schema(description = "User state", example = "CONFIRMED")
    private User.State state;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .state(User.State.valueOf(user.getState().toString()))
                .build();

    }

    public static List<UserDto> from(Collection<User> users) {
        return users.stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }
}
