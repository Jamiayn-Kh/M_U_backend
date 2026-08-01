package mn.mungunurlal.moldorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record TransportOrderRequest(

        @NotNull(message = "Автобус хөдлөх огноог оруулна")
        LocalDate departureDate,

        @NotNull(message = "Автобус хөдлөх цагийг оруулна")
        LocalTime departureTime,

        @NotBlank(message = "Автобусны дугаарыг оруулна")
        @Size(
                max = 50,
                message = "Автобусны дугаар 50 тэмдэгтээс хэтрэхгүй"
        )
        String busNumber,

        @NotBlank(message = "Жолоочийн утасны дугаарыг оруулна")
        @Pattern(
                regexp = "^[0-9]{8}$",
                message = "Жолоочийн утасны дугаар 8 оронтой байна"
        )
        String driverPhone,

        @Size(
                max = 500,
                message = "Тээврийн тайлбар 500 тэмдэгтээс хэтрэхгүй"
        )
        String note
) {
}