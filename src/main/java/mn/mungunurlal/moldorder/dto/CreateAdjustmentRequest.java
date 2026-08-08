package mn.mungunurlal.moldorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mn.mungunurlal.moldorder.domain.AdjustmentAction;

public record CreateAdjustmentRequest(

        @NotNull(message = "Өөрчлөлтийн төрлийг сонгоно")
        AdjustmentAction action,

        @Pattern(
                regexp = "(?i)^[AKS][0-9]{1,4}$",
                message = "Хэвний код A, K эсвэл S үсгээр эхэлж, араас нь 1-4 оронтой тоо байна"
        )
        String finalMoldCode,

        @Min(
                value = 0,
                message = "Тоо ширхэг 0-ээс бага байж болохгүй"
        )
        int finalQuantity,

        @Size(
                max = 500,
                message = "Тайлбар 500 тэмдэгтээс хэтрэхгүй"
        )
        String note
) {
}