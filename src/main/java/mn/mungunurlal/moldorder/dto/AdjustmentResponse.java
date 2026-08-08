package mn.mungunurlal.moldorder.dto;

import mn.mungunurlal.moldorder.domain.AdjustmentAction;
import mn.mungunurlal.moldorder.domain.MoldOrderItemAdjustment;

import java.time.LocalDateTime;

public record AdjustmentResponse(
        Long id,
        AdjustmentAction action,
        String finalMoldCode,
        int finalQuantity,
        String note,
        UserInfo createdBy,
        LocalDateTime createdAt,
        boolean approved,
        LocalDateTime approvedAt
) {

    public static AdjustmentResponse from(
            MoldOrderItemAdjustment adjustment
    ) {
        return new AdjustmentResponse(
                adjustment.getId(),
                adjustment.getAction(),
                adjustment.getFinalMoldCode(),
                adjustment.getFinalQuantity(),
                adjustment.getNote(),
                new UserInfo(
                        adjustment.getCreatedBy().getId(),
                        adjustment.getCreatedBy().getUsername(),
                        adjustment.getCreatedBy().getFullName()
                ),
                adjustment.getCreatedAt(),
                adjustment.isApproved(),
                adjustment.getApprovedAt()
        );
    }

    public record UserInfo(
            Long id,
            String username,
            String fullName
    ) {
    }
}