package by.sakuuj.digital.chief.testproj.mapper;

import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.SkuRequest;
import by.sakuuj.digital.chief.testproj.dto.SkuResponse;
import by.sakuuj.digital.chief.testproj.entity.Sku;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = LocalDateTimeMapper.class
)
public interface SkuMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "sequenceNumber", ignore = true)
    @Mapping(target = "modificationAudit", ignore = true)
    Sku toEntity(SkuRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "createdAt", source = "modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "modificationAudit.updatedAt")
    SkuResponse toResponse(Sku entity);

    @Mapping(target = "sequenceNumber", source = "id")
    SkuResponse toResponse(SkuDocumentDto skuDocumentDto);

    @Mapping(target = "id", source = "sequenceNumber")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "createdAt", source = "modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "modificationAudit.updatedAt")
    SkuDocumentDto toDocumentDto(Sku entity);

    @InheritConfiguration(name = "toEntity")
    void update(@MappingTarget Sku entityToUpdate, SkuRequest updateRequest);
}
