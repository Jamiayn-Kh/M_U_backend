package mn.mungunurlal.moldorder.service;

import mn.mungunurlal.moldorder.domain.MoldOrder;
import mn.mungunurlal.moldorder.domain.MoldOrderItem;
import mn.mungunurlal.moldorder.dto.CreateMoldOrderRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderItemRequest;
import mn.mungunurlal.moldorder.dto.MoldOrderResponse;
import mn.mungunurlal.moldorder.exception.InvalidMoldOrderException;
import mn.mungunurlal.moldorder.exception.MoldOrderNotFoundException;
import mn.mungunurlal.moldorder.repository.MoldOrderRepository;
import mn.mungunurlal.user.domain.User;
import mn.mungunurlal.user.domain.UserRole;
import mn.mungunurlal.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mn.mungunurlal.moldorder.dto.TransportOrderRequest;

import java.util.HashSet;
import java.util.List;
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
        User seller = getUser(username);

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

    @Transactional(readOnly = true)
    public List<MoldOrderResponse> getOrders(String username) {
        User user = getUser(username);

        List<MoldOrder> orders;

        if (user.getRole() == UserRole.PROVINCE_SELLER) {
            orders = moldOrderRepository
                    .findAllBySellerIdOrderByCreatedAtDesc(user.getId());
        } else if (
                user.getRole() == UserRole.ADMIN
                        || user.getRole() == UserRole.CITY_HANDLER
        ) {
            orders = moldOrderRepository
                    .findAllByOrderByCreatedAtDesc();
        } else {
            throw new InvalidMoldOrderException(
                    "Хэвний хүсэлт харах эрхгүй байна"
            );
        }

        return orders.stream()
                .map(MoldOrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MoldOrderResponse getOrder(
            Long orderId,
            String username
    ) {
        User user = getUser(username);

        MoldOrder order = moldOrderRepository
                .findWithDetailsById(orderId)
                .orElseThrow(() ->
                        new MoldOrderNotFoundException(orderId)
                );

        if (user.getRole() == UserRole.PROVINCE_SELLER
                && !order.getSeller().getId().equals(user.getId())) {
            throw new InvalidMoldOrderException(
                    "Бусдын хэвний хүсэлтийг харах эрхгүй байна"
            );
        }

        if (user.getRole() == UserRole.CRAFTSMAN) {
            throw new InvalidMoldOrderException(
                    "Хэвний хүсэлт харах эрхгүй байна"
            );
        }

        return MoldOrderResponse.from(order);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidMoldOrderException(
                                "Нэвтэрсэн хэрэглэгч олдсонгүй"
                        )
                );
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
    @Transactional
public MoldOrderResponse receiveOrder(
        Long orderId,
        String username
) {
    User cityHandler = getUser(username);

    if (cityHandler.getRole() != UserRole.CITY_HANDLER) {
        throw new InvalidMoldOrderException(
                "Зөвхөн хотын харилцагч хүсэлтийг хүлээн авна"
        );
    }

    MoldOrder order = moldOrderRepository
            .findWithDetailsById(orderId)
            .orElseThrow(() ->
                    new MoldOrderNotFoundException(orderId)
            );

    try {
        order.receive(cityHandler);
    } catch (IllegalStateException exception) {
        throw new InvalidMoldOrderException(
                exception.getMessage()
        );
    }

    return MoldOrderResponse.from(order);
}

@Transactional
public MoldOrderResponse startProcessing(
        Long orderId,
        String username
) {
    User cityHandler = getUser(username);

    if (cityHandler.getRole() != UserRole.CITY_HANDLER) {
        throw new InvalidMoldOrderException(
                "Зөвхөн хотын хэрэглэгч хүсэлтийг боловсруулж эхэлнэ"
        );
    }

    MoldOrder order = moldOrderRepository
            .findWithDetailsById(orderId)
            .orElseThrow(() ->
                    new MoldOrderNotFoundException(orderId)
            );

    if (order.getCityHandler() == null
            || !order.getCityHandler().getId().equals(cityHandler.getId())) {
        throw new InvalidMoldOrderException(
                "Та энэ хүсэлтийг хүлээн аваагүй байна"
        );
    }

    try {
        order.startProcessing();
    } catch (IllegalStateException exception) {
        throw new InvalidMoldOrderException(
                exception.getMessage()
        );
    }

    return MoldOrderResponse.from(order);
}

@Transactional
public MoldOrderResponse transportOrder(
        Long orderId,
        String username,
        TransportOrderRequest request
) {
    User cityHandler = getUser(username);

    if (cityHandler.getRole() != UserRole.CITY_HANDLER) {
        throw new InvalidMoldOrderException(
                "Зөвхөн хотын хэрэглэгч хүсэлтийг унаанд тавина"
        );
    }

    MoldOrder order = moldOrderRepository
            .findWithDetailsById(orderId)
            .orElseThrow(() ->
                    new MoldOrderNotFoundException(orderId)
            );

    if (order.getCityHandler() == null
            || !order.getCityHandler().getId().equals(cityHandler.getId())) {
        throw new InvalidMoldOrderException(
                "Та энэ хүсэлтийг хүлээн аваагүй байна"
        );
    }

    try {
        order.markTransported(
                request.departureDate(),
                request.departureTime(),
                request.busNumber(),
                request.driverPhone(),
                request.note()
        );
    } catch (IllegalStateException exception) {
        throw new InvalidMoldOrderException(
                exception.getMessage()
        );
    }

    return MoldOrderResponse.from(order);
}
}