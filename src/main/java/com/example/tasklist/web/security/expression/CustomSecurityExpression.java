package com.example.tasklist.web.security.expression;

import com.example.tasklist.domain.user.Role;
import com.example.tasklist.service.UserService;
import com.example.tasklist.web.security.JwtEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {

    private final UserService userService;

    public boolean canAccessUser(Long id) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final JwtEntity user = (JwtEntity) authentication.getPrincipal();
        return user.getId().equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    private boolean hasAnyRole(Authentication authentication, Role... roles) {
        for (Role role : roles) {
            final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
            if (authentication.getAuthorities().contains(authority)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccessTask(Long taskId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final JwtEntity user = (JwtEntity) authentication.getPrincipal();
        return userService.isTaskOwner(user.getId(), taskId);
    }
}
