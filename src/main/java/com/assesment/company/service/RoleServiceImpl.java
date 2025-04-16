package com.assesment.company.service;

import com.assesment.company.entity.Role;
import com.assesment.company.repository.RoleRepository;
import com.assesment.company.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Integer id, Role role) {
        // Implement update logic here (e.g., check if exists, update fields, save)
        if (roleRepository.existsById(id)) {
            role.setId(id); // Ensure the ID is set for updating
            return roleRepository.save(role);
        }
        return null; // Or throw an exception
    }

    @Override
    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }
}