package by.sakuuj.digital.chief.testproj.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ModificationAudit {

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void beforePersist() {
        LocalDateTime currentTimeUTC = LocalDateTime.now(Clock.systemUTC());

        createdAt = currentTimeUTC;
        updatedAt = currentTimeUTC;
    }

    @PreUpdate
    private void beforeUpdate() {
        LocalDateTime currentTimeUTC = LocalDateTime.now(Clock.systemUTC());

        updatedAt = currentTimeUTC;
    }
}
