package by.sakuuj.digital.chief.testproj.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "SKUS")
public class Sku {

    @Id
    @Column(name = "seq_number")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sequenceNumber;

    private String department;

    private String storeLocation;

    private BigDecimal productPrice;

    private short productSize;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Version
    private short version;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Sku sku)) {
            return false;
        }

        return getSequenceNumber() != null
                && getSequenceNumber().equals(sku.getSequenceNumber());
    }
}
