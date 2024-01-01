package com.example.dto;

import com.example.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends BaseDTO<User>{
    private String username;
    private String password;
    private String email;

    public UserDTO(User user) {super(user);}
    @Override
    public BaseDTO<User> convertToDTO(User entity) {
        setId(entity.getId());
        setUsername(entity.getUsername());
        setPassword(entity.getEmail());
        setEmail(entity.getEmail());
        setCreatedAt(entity.getCreatedAt());
        setUpdatedAt(entity.getUpdatedAt());
        return this;
    }
}
