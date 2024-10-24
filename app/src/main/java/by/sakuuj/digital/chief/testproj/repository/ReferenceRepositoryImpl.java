package by.sakuuj.digital.chief.testproj.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ReferenceRepositoryImpl implements ReferenceRepository {

    private final EntityManager entityManager;

    @Override
    public <T> T getReference(Class<T> clazz, UUID id) {
        return entityManager.getReference(clazz, id);
    }
}
