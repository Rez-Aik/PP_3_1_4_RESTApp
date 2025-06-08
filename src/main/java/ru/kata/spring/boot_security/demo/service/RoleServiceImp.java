package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Данной роли не существует"));
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Set<Role> getRolesSetByUserName(List<String> roleNames) {
        Set<Role> userRoles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            userRoles = roleNames.stream().map(this::findRoleByName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else {
            userRoles.add(findRoleByName(Role.defaultRoleName));
        }
        return userRoles;
    }
}
