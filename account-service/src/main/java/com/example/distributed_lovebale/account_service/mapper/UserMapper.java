package com.example.distributed_lovebale.account_service.mapper;

import com.example.distributed_lovable.common_lib.dto.UserDto;
import com.example.distributed_lovable.common_lib.security.JwtUserPrincipal;
import com.example.distributed_lovebale.account_service.dto.auth.SignupRequest;
import com.example.distributed_lovebale.account_service.dto.auth.UserProfileResponse;
import com.example.distributed_lovebale.account_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);

    @Mapping(source = "userId", target = "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrincipal user);

    UserDto toUserDto(User user);
}
