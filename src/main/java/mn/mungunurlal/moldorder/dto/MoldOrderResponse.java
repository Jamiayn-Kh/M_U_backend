package mn.mungunurlal.moldorder.dto;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderItem;
import mn.mungunurlal.moldorder.domain.MoldOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record MoldOrderResponse(
        Long id,
        UserInfo seller,
        UserInfo cityHandler,
        MoldOrderStatus status,
        String note,
        List<ItemInfo> items,
        TransportInfo transport,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        LocalDateTime receivedAt,
        LocalDateTime transportedAt,
        LocalDateTime completedAt
) {

    public static MoldOrderResponse from(MoldOrder order) {
        UserInfo sellerInfo = UserInfo.from(order.getSeller());

        UserInfo cityHandlerInfo = order.getCityHandler() == null
                ? null
                : UserInfo.from(order.getCityHandler());

        List<ItemInfo> itemResponses = order.getItems()
                .stream()
                .map(ItemInfo::from)
                .toList();

        TransportInfo transportInfo = new TransportInfo(
                order.getDepartureDate(),
                order.getDepartureTime(),
                order.getBusNumber(),
                order.getDriverPhone(),
                order.getTransportNote()
        );

        return new MoldOrderResponse(
                order.getId(),
                sellerInfo,
                cityHandlerInfo,
                order.getStatus(),
                order.getNote(),
                itemResponses,
                transportInfo,
                order.getCreatedAt(),
                order.getSentAt(),
                order.getReceivedAt(),
                order.getTransportedAt(),
                order.getCompletedAt()
        );
    }

    public record UserInfo(
            Long id,
            String username,
            String fullName
    ) {
        public static UserInfo from(
                mn.mungunurlal.user.domain.User user
        ) {
            return new UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName()
            );
        }
    }

public record ItemInfo(
        Long id,
        String moldCode,
        String codePrefix,
        int quantity,
        boolean stoneRequired,
        List<AdjustmentResponse> adjustments
) {
    public static ItemInfo from(MoldOrderItem item) {
        List<AdjustmentResponse> adjustmentResponses =
                item.getAdjustments()
                        .stream()
                        .map(AdjustmentResponse::from)
                        .toList();

        return new ItemInfo(
                item.getId(),
                item.getMoldCode(),
                item.getCodePrefix(),
                item.getQuantity(),
                item.isStoneRequired(),
                adjustmentResponses
        );
    }
}

    public record TransportInfo(
            LocalDate departureDate,
            LocalTime departureTime,
            String busNumber,
            String driverPhone,
            String note
    ) {
    }
}