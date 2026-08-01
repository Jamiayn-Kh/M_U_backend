package mn.mungunurlal.moldorder.service;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderItem;
import mn.mungunurlal.moldorder.dto.CreateMoldOrderRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderItemRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderResponse;
import mn.mungunurlal.moldorder.exception.InvalidMoldOrderException;
import mn.mungunurlal.moldorder.repository.MoldOrderRepository;
import mn.mungunurlal.user.domain.User;
import mn.mungunurlal.user.domain.UserRole;
import mn.mungunurlal.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class MoldOrderService {

    private final MoldOrderRepository moldOrderRepository;
    private final UserRepository userRepository;

    public MoldOrderService(
            MoldOrderRepository moldOrderRepository,
            UserRepository userRepository
    ) {
        this.moldOrderRepository = moldOrderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MoldOrderResponse createOrder(
            String username,
            CreateMoldOrderRequest request
    ) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidMoldOrderException(
                                "Нэвтэрсэн хэрэглэгч олдсонгүй"
                        )
                );

        if (seller.getRole() != UserRole.PROVINCE_SELLER) {
            throw new InvalidMoldOrderException(
                    "Зөвхөн аймгийн борлуулагч хүсэлт үүсгэнэ"
            );
        }

        validateDuplicateCodes(request);

        MoldOrder order = new MoldOrder(
                seller,
                request.note()
        );

        for (MoldOrderItemRequest itemRequest : request.items()) {
            MoldOrderItem item = new MoldOrderItem(
                    itemRequest.moldCode(),
                    itemRequest.stoneRequired()
            );

            order.addItem(item);
        }

        order.send();

        MoldOrder savedOrder = moldOrderRepository.save(order);

        return MoldOrderResponse.from(savedOrder);
    }

    private void validateDuplicateCodes(
            CreateMoldOrderRequest request
    ) {
        Set<String> uniqueCodes = new HashSet<>();

        for (MoldOrderItemRequest item : request.items()) {
            String normalizedCode = item.moldCode()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (!uniqueCodes.add(normalizedCode)) {
                throw new InvalidMoldOrderException(
                        "Нэг хүсэлтэд ижил хэвний код давхардаж болохгүй: "
                                + normalizedCode
                );
            }
        }
    }
}