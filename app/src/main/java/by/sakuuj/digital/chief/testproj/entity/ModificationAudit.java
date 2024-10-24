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
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ModificationAudit {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void beforePersist() {
        LocalDateTime currentTimeUTC = LocalDateTime.parse(
                LocalDateTime.now(Clock.systemUTC()).format(dateTimeFormatter),
                dateTimeFormatter
        );

        createdAt = currentTimeUTC;
        updatedAt = currentTimeUTC;
    }

    @PreUpdate
    private void beforeUpdate() {
        LocalDateTime currentTimeUTC = LocalDateTime.parse(
                LocalDateTime.now(Clock.systemUTC()).format(dateTimeFormatter),
                dateTimeFormatter
        );
        updatedAt = currentTimeUTC;
    }
}
