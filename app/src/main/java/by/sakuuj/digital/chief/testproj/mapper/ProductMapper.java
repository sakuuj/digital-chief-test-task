package by.sakuuj.digital.chief.testproj.mapper;

import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import by.sakuuj.digital.chief.testproj.dto.ProductRequest;
import by.sakuuj.digital.chief.testproj.dto.ProductResponse;
import by.sakuuj.digital.chief.testproj.entity.Product;
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
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modificationAudit", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "createdAt", source = "modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "modificationAudit.updatedAt")
    ProductResponse toResponse(Product entity);

    ProductResponse toResponse(ProductDocumentDto productDocumentDto);

    @Mapping(target = "createdAt", source = "modificationAudit.createdAt")
    @Mapping(target = "updatedAt", source = "modificationAudit.updatedAt")
    ProductDocumentDto toDocumentDto(Product entity);

    @InheritConfiguration(name = "toEntity")
    void update(@MappingTarget Product entityToUpdate, ProductRequest updateRequest);
}
