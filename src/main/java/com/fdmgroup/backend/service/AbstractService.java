package com.fdmgroup.backend.service;

import com.fdmgroup.backend.model.AbstractEntity;
import com.fdmgroup.backend.model.DTO;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents an abstract {@code @Service} for some {@code @Entity}
 * @param <E> the {@code @Entity} type
 * @param <D> the {@code DTO} type for the {@code @Entity}
 * @param <R> the {@code @Repository} type for the {@code Entity}
 * @param <I> the {@code @Id} type for the {@code Entity}
 */
@Getter
@Log4j2
public abstract class AbstractService<
        E extends AbstractEntity<I>,
        D extends DTO<E, I>,
        R extends CrudRepository<E, I>,
        I extends Serializable> {

    protected final Class<E> entityClass;
    protected final Class<D> dtoClass;
    protected final R repository;
    protected final ModelMapper mapper;

    /**
     * Constructor for an AbstractService. Subclasses must annotate their overridden constructors with {@code @Autowired}.
     * @param repository the autowired {@code @Repository}
     * @param mapper the autowired {@code ModelMapper}
     */
    public AbstractService(R repository, ModelMapper mapper) {
        Type[] params = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments();
        //noinspection unchecked
        entityClass = (Class<E>) params[0];
        //noinspection unchecked
        dtoClass = (Class<D>) params[1];

        this.repository = repository;
        this.mapper = mapper;
    }

    public TypeMap<E, D> getDTOMapper() {
        return mapper.typeMap(entityClass, dtoClass);
    }
    public TypeMap<D, E> getEntityMapper() {
        return mapper.typeMap(dtoClass, entityClass);
    }

    /**
     * @return all entities in the repository.
     */
    public Iterable<D> findAll() {
        ArrayList<D> ds = new ArrayList<>();
        repository.findAll().forEach(e -> ds.add(getDTOMapper().map(e)));
        return ds;
    }

    /**
     * Retrieves the entity with the specified ID.
     * @param id the target entity's ID
     * @return an {@code Optional} containing a populated DTO, if found
     */
    public Optional<D> findById(I id) {
        return repository
                .findById(id)
                .map(e -> getDTOMapper().map(e));
    }

    /**
     * Creates and persists a new entity.
     * @param dto the DTO containing data to populate the new entity with.
     *            The {@code id} field is ignored.
     * @return a DTO populated with the new entity's details
     */
    public D create(D dto) {
        dto.setId(null);
        var ret = getDTOMapper().map(repository.save(getEntityMapper().map(dto)));
        log.debug("Created new " + entityClass + " with ID " + ret.getId());
        return ret;
    }

    /**
     * Updates and persists an entity.
     * @param dto the DTO containing the entity's ID and changed data.
     * @return an {@code Optional} containing an updated DTO if the entity existed
     */
    @Transactional
    public Optional<D> update(D dto) {
        return repository
                .findById(dto.getId())
                .map(e -> {
                    getEntityMapper().map(dto, e);
                    log.debug("Updated " + entityClass + " with ID " + dto.getId());
                    return getDTOMapper().map(e);
                });
    }

    /**
     * Deletes the entity with the specified ID.
     * @param id the target entity's ID
     * @return true if the entity existed and was deleted, false otherwise
     */
    public boolean deleteById(I id) {
        return repository
                .findById(id)
                .map(e -> {
                    repository.delete(e);
                    log.debug("Deleted " + entityClass + " with ID " + id);
                    return true;
                })
                .orElse(false);
    }
}
