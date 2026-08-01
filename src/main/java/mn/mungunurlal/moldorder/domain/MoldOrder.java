package mn.mungunurlal.moldorder.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import mn.mungunurlal.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "mold_orders")
public class MoldOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_handler_id")
    private User cityHandler;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MoldOrderStatus status;

    @Column(length = 500)
    private String note;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "bus_number", length = 50)
    private String busNumber;

    @Column(name = "driver_phone", length = 20)
    private String driverPhone;

    @Column(name = "transport_note", length = 500)
    private String transportNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "transported_at")
    private LocalDateTime transportedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<MoldOrderItem> items = new ArrayList<>();

    protected MoldOrder() {
        // JPA requires a no-argument constructor.
    }

    public MoldOrder(User seller, String note) {
        this.seller = seller;
        this.note = normalizeOptionalText(note);
        this.status = MoldOrderStatus.DRAFT;
    }

    @PrePersist
    void beforeInsert() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = MoldOrderStatus.DRAFT;
        }
    }

    public void addItem(MoldOrderItem item) {
        item.assignTo(this);
        items.add(item);
    }

    public Long getId() {
        return id;
    }

    public User getSeller() {
        return seller;
    }

    public User getCityHandler() {
        return cityHandler;
    }

    public MoldOrderStatus getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getTransportNote() {
        return transportNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public LocalDateTime getTransportedAt() {
        return transportedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<MoldOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public void send() {
    if (items.isEmpty()) {
        throw new IllegalStateException(
                "Хэвний кодгүй хүсэлтийг илгээх боломжгүй"
        );
    }

    if (status != MoldOrderStatus.DRAFT) {
        throw new IllegalStateException(
                "Зөвхөн DRAFT төлөвтэй хүсэлтийг илгээх боломжтой"
        );
    }

    status = MoldOrderStatus.SENT;
    sentAt = LocalDateTime.now();
}
public void receive(User cityHandler) {
    if (status != MoldOrderStatus.SENT) {
        throw new IllegalStateException(
                "Зөвхөн SENT төлөвтэй хүсэлтийг хүлээн авах боломжтой"
        );
    }

    this.cityHandler = cityHandler;
    this.status = MoldOrderStatus.RECEIVED;
    this.receivedAt = LocalDateTime.now();
}

}