package mn.mungunurlal.moldorder.repository;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoldOrderRepository
        extends JpaRepository<MoldOrder, Long> {

    List<MoldOrder> findAllBySellerIdOrderByCreatedAtDesc(Long sellerId);

    List<MoldOrder> findAllByStatusOrderByCreatedAtDesc(
            MoldOrderStatus status
    );
}