package by.sakuuj.digital.chief.testproj.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductResponse(
    UUID id,
    String title,
    String description,
    String type,
    String brand,
    short version,
    String createdAt,
    String updatedAt
) {
}
