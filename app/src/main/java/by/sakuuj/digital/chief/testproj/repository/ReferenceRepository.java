package by.sakuuj.digital.chief.testproj.repository;

import java.util.UUID;

public interface ReferenceRepository {

    <T> T getReference(Class<T> clazz, UUID id);
}
