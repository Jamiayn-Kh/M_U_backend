package mn.mungunurlal.moldorder.repository;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoldOrderRepository
        extends JpaRepository<MoldOrder, Long> {

    @EntityGraph(attributePaths = {"seller", "cityHandler", "items"})
    List<MoldOrder> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"seller", "cityHandler", "items"})
    List<MoldOrder> findAllBySellerIdOrderByCreatedAtDesc(Long sellerId);

    @EntityGraph(attributePaths = {"seller", "cityHandler", "items"})
    List<MoldOrder> findAllByStatusOrderByCreatedAtDesc(
            MoldOrderStatus status
    );

    @EntityGraph(attributePaths = {"seller", "cityHandler", "items"})
    Optional<MoldOrder> findWithDetailsById(Long id);
}