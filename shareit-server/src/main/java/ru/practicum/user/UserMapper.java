package ru.practicum.user;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
