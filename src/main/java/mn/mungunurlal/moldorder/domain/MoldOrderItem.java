package mn.mungunurlal.moldorder.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Entity
@Table(name = "mold_order_items")
public class MoldOrderItem {

    private static final Pattern MOLD_CODE_PATTERN =
            Pattern.compile("^[AKS][0-9]{1,4}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private MoldOrder order;

    @Column(name = "mold_code", nullable = false, length = 5)
    private String moldCode;

    @Column(name = "code_prefix", nullable = false, length = 1)
    private String codePrefix;

    @Column(name = "stone_required", nullable = false)
    private boolean stoneRequired;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int quantity;

    protected MoldOrderItem() {
        // JPA requires a no-argument constructor.
    }

public MoldOrderItem(
        String moldCode,
        int quantity,
        boolean stoneRequired
) {
    String normalizedCode = normalizeMoldCode(moldCode);

    if (quantity < 1) {
        throw new IllegalArgumentException(
                "Тоо ширхэг хамгийн багадаа 1 байна"
        );
    }

    this.moldCode = normalizedCode;
    this.codePrefix = normalizedCode.substring(0, 1);
    this.quantity = quantity;
    this.stoneRequired = stoneRequired;
}

    void assignTo(MoldOrder order) {
        this.order = order;
    }

    @PrePersist
    void beforeInsert() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getMoldCode() {
        return moldCode;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public boolean isStoneRequired() {
        return stoneRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getQuantity() {
    return quantity;
}

    private String normalizeMoldCode(String moldCode) {
        if (moldCode == null || moldCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Хэвний код хоосон байж болохгүй"
            );
        }

        String normalizedCode = moldCode
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!MOLD_CODE_PATTERN.matcher(normalizedCode).matches()) {
            throw new IllegalArgumentException(
                    "Хэвний код A, K эсвэл S үсгээр эхэлж, " +
                    "араас нь 1-4 оронтой тоо агуулна"
            );
        }

        return normalizedCode;
    }
}