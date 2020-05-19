package com.fdmgroup.backend.service;

import com.fdmgroup.backend.exception.InvalidRequestException;
import com.fdmgroup.backend.model.User;
import com.fdmgroup.backend.model.UserDTO;
import com.fdmgroup.backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class UserService extends AbstractService<User, UserDTO, UserRepository, Long> {

    @Autowired
    public UserService(UserRepository repository, ModelMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public UserDTO create(UserDTO dto) {
        if (repository.existsByUsername(dto.getUsername()))
            throw new InvalidRequestException("Username already in use");
        if (repository.existsByEmail(dto.getEmail()))
            throw new InvalidRequestException("Email already in use");
        log.debug("Registered new user '" + dto.getUsername() + "'");
        return super.create(dto);
    }

    public Optional<UserDTO> findByUsername(String username) {
        return repository
                .findByUsername(username)
                .map(t -> mapper.map(t, dtoClass));
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.findById(id)
                .map(u -> {
                    u.getSharedLists().forEach(l -> l.removeViewer(u));
                    super.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
