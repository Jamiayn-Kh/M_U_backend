package mn.mungunurlal.moldorder.dto;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderItem;
import mn.mungunurlal.moldorder.domain.MoldOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MoldOrderResponse(
        Long id,
        SellerInfo seller,
        MoldOrderStatus status,
        String note,
        List<ItemInfo> items,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {

    public static MoldOrderResponse from(MoldOrder order) {
        List<ItemInfo> itemResponses = order.getItems()
                .stream()
                .map(ItemInfo::from)
                .toList();

        SellerInfo sellerInfo = new SellerInfo(
                order.getSeller().getId(),
                order.getSeller().getUsername(),
                order.getSeller().getFullName()
        );

        return new MoldOrderResponse(
                order.getId(),
                sellerInfo,
                order.getStatus(),
                order.getNote(),
                itemResponses,
                order.getCreatedAt(),
                order.getSentAt()
        );
    }

    public record SellerInfo(
            Long id,
            String username,
            String fullName
    ) {
    }

    public record ItemInfo(
            Long id,
            String moldCode,
            String codePrefix,
            boolean stoneRequired
    ) {
        public static ItemInfo from(MoldOrderItem item) {
            return new ItemInfo(
                    item.getId(),
                    item.getMoldCode(),
                    item.getCodePrefix(),
                    item.isStoneRequired()
            );
        }
    }
}