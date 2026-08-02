package mn.mungunurlal.moldorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MoldOrderItemRequest(

        @NotBlank(message = "Хэвний код хоосон байж болохгүй")
        @Pattern(
                regexp = "(?i)^[AKS][0-9]{1,4}$",
                message = "Хэвний код A, K эсвэл S үсгээр эхэлж, араас нь 1-4 оронтой тоо байна"
        )
        String moldCode,

        @Min(
                value = 1,
                message = "Тоо ширхэг хамгийн багадаа 1 байна"
        )
        int quantity,

        boolean stoneRequired
) {
}