package by.sakuuj.digital.chief.testproj.repository;

import by.sakuuj.digital.chief.testproj.ProductTestBuilder;
import by.sakuuj.digital.chief.testproj.SkuTestBuilder;
import by.sakuuj.digital.chief.testproj.entity.Product;
import by.sakuuj.digital.chief.testproj.entity.Sku;
import by.sakuuj.digital.chief.testproj.testcontainers.PostgresSingletonContainerLauncher;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SkuRepositoryTest extends PostgresSingletonContainerLauncher {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SkuRepository skuRepository;

    @Test
    @Transactional
    void shouldFindProductsCreatedAtAfterPointInTime() {

        // given
        entityManager.createQuery("delete Sku s").executeUpdate();
        entityManager.createQuery("delete Product p").executeUpdate();
        entityManager.flush();

        ProductTestBuilder productBuilder = ProductTestBuilder.builder()
                .withId(null)
                .withVersion((short) 0);
        SkuTestBuilder skuBuilder = SkuTestBuilder.builder()
                .withId(null)
                .withVersion((short) 0);


        Product product = productBuilder.build();
        entityManager.persist(product);

        Sku firstSku = skuBuilder.build().setProduct(product);
        Sku secondSku = skuBuilder.build().setProduct(product);
        Sku thirdSku = skuBuilder.build().setProduct(product);

        entityManager.persist(firstSku);
        entityManager.flush();
        entityManager.persist(secondSku);
        entityManager.flush();
        entityManager.persist(thirdSku);
        entityManager.flush();

        // when
        Slice<Sku> found = skuRepository.findAllByModificationAudit_CreatedAtAfter(
                firstSku.getModificationAudit().getCreatedAt(),
                PageRequest.of(0, 10, Sort.by("modificationAudit.createdAt").descending())
        );

        // then
        assertThat(found).hasSize(2).containsExactly(thirdSku, secondSku);
    }

    @Test
    @Transactional
    void shouldFindAllByProductId() {

        // given
        entityManager.createQuery("delete Sku s").executeUpdate();
        entityManager.createQuery("delete Product p").executeUpdate();
        entityManager.flush();

        ProductTestBuilder productBuilder = ProductTestBuilder.builder()
                .withId(null)
                .withVersion((short) 0);
        SkuTestBuilder skuBuilder = SkuTestBuilder.builder()
                .withId(null)
                .withVersion((short) 0);


        Product firstProduct = productBuilder.build();
        Product secondProduct = productBuilder.build();
        entityManager.persist(firstProduct);
        entityManager.flush();
        entityManager.persist(secondProduct);
        entityManager.flush();

        Sku firstSku = skuBuilder.build().setProduct(firstProduct);
        Sku secondSku = skuBuilder.build().setProduct(firstProduct);
        Sku thirdSku = skuBuilder.build().setProduct(firstProduct);
        Sku fourthSku = skuBuilder.build().setProduct(secondProduct);
        Sku fifthSku = skuBuilder.build().setProduct(secondProduct);

        entityManager.persist(firstSku);
        entityManager.flush();
        entityManager.persist(secondSku);
        entityManager.flush();
        entityManager.persist(thirdSku);
        entityManager.flush();
        entityManager.persist(fourthSku);
        entityManager.flush();
        entityManager.persist(fifthSku);
        entityManager.flush();

        // when
        Slice<Sku> found = skuRepository.findAllByProductId(
                firstProduct.getId(),
                PageRequest.of(0, 10, Sort.by("modificationAudit.createdAt").descending())
        );

        // then
        assertThat(found).hasSize(3).containsExactly(thirdSku, secondSku, firstSku);
    }
}
