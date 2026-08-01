package mn.mungunurlal.moldorder;

import jakarta.validation.Valid;
import mn.mungunurlal.moldorder.dto.CreateMoldOrderRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderResponse;
import mn.mungunurlal.moldorder.service.MoldOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<MoldOrderResponse>> getOrders(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                moldOrderService.getOrders(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoldOrderResponse> getOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                moldOrderService.getOrder(
                        id,
                        authentication.getName()
                )
        );
    }
}