package mn.mungunurlal.moldorder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateMoldOrderRequest(

        @Size(
                max = 500,
                message = "Тайлбар 500 тэмдэгтээс хэтрэхгүй байна"
        )
        String note,

        @NotEmpty(message = "Хамгийн багадаа нэг хэвний код оруулна")
        List<@Valid MoldOrderItemRequest> items
) {
}