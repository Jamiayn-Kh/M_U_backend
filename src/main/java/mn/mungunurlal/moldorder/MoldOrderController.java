package mn.mungunurlal.moldorder;

import jakarta.validation.Valid;
import mn.mungunurlal.moldorder.dto.CreateMoldOrderRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderResponse;
import mn.mungunurlal.moldorder.service.MoldOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mold-orders")
public class MoldOrderController {

    private final MoldOrderService moldOrderService;

    public MoldOrderController(
            MoldOrderService moldOrderService
    ) {
        this.moldOrderService = moldOrderService;
    }

    @PostMapping
    public ResponseEntity<MoldOrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateMoldOrderRequest request
    ) {
        MoldOrderResponse response =
                moldOrderService.createOrder(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}