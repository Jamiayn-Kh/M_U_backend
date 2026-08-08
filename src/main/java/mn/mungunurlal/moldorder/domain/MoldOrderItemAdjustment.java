package mn.mungunurlal.moldorder.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import mn.mungunurlal.user.domain.User;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Entity
@Table(name = "mold_order_item_adjustments")
public class MoldOrderItemAdjustment {

    private static final Pattern MOLD_CODE_PATTERN =
            Pattern.compile("^[AKS][0-9]{1,4}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private MoldOrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdjustmentAction action;

    @Column(name = "final_mold_code", length = 5)
    private String finalMoldCode;

    @Column(name = "final_quantity", nullable = false)
    private int finalQuantity;

    @Column(length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean approved;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    protected MoldOrderItemAdjustment() {
    }

    public MoldOrderItemAdjustment(
            AdjustmentAction action,
            String finalMoldCode,
            int finalQuantity,
            String note,
            User createdBy
    ) {
        validate(action, finalMoldCode, finalQuantity);

        this.action = action;
        this.finalMoldCode = normalizeMoldCode(finalMoldCode);
        this.finalQuantity = finalQuantity;
        this.note = normalizeOptionalText(note);
        this.createdBy = createdBy;
        this.approved = false;
    }

    void assignTo(MoldOrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void approve() {
        if (!approved) {
            approved = true;
            approvedAt = LocalDateTime.now();
        }
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

    public AdjustmentAction getAction() {
        return action;
    }

    public String getFinalMoldCode() {
        return finalMoldCode;
    }

    public int getFinalQuantity() {
        return finalQuantity;
    }

    public String getNote() {
        return note;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isApproved() {
        return approved;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    private void validate(
            AdjustmentAction action,
            String finalMoldCode,
            int finalQuantity
    ) {
        if (action == null) {
            throw new IllegalArgumentException(
                    "Өөрчлөлтийн төрөл заавал байна"
            );
        }

        if (action == AdjustmentAction.CANCEL) {
            if (finalQuantity != 0) {
                throw new IllegalArgumentException(
                        "CANCEL үед тоо ширхэг 0 байна"
                );
            }

            return;
        }

        if (finalQuantity < 1) {
            throw new IllegalArgumentException(
                    "Тоо ширхэг хамгийн багадаа 1 байна"
            );
        }

        String normalizedCode = normalizeMoldCode(finalMoldCode);

        if (normalizedCode == null
                || !MOLD_CODE_PATTERN.matcher(normalizedCode).matches()) {
            throw new IllegalArgumentException(
                    "Хэвний код A, K эсвэл S үсгээр эхэлж, " +
                            "араас нь 1-4 оронтой тоо байна"
            );
        }
    }

    private String normalizeMoldCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}