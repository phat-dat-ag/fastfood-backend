package com.example.fastfoodshop.service;

import com.example.fastfoodshop.constant.StoreConstants;
import com.example.fastfoodshop.entity.Address;
import com.example.fastfoodshop.request.DeliveryRequest;
import com.example.fastfoodshop.dto.DeliveryDTO;
import com.example.fastfoodshop.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final AddressService addressService;

    @Value("${goong.api.key}")
    private String goongApiKey;

    private static double normalizeDistance(double distanceMeters) {
        double distanceKm = distanceMeters / 1000.0;
        if (distanceKm < StoreConstants.MIN_DISTANCE_KM) {
            distanceKm = StoreConstants.MIN_DISTANCE_KM;
        }
        return NumberUtils.roundToOneDecimal(distanceKm);
    }

    public static int normalizeDuration(int durationSeconds) {
        return durationSeconds / 60 + StoreConstants.MIN_DURATION_MINUTES;
    }

    public DeliveryDTO calculateDelivery(DeliveryRequest request) {
        try {
            if (request == null) {
                return DeliveryDTO.reject("Hãy chọn địa chỉ hợp lệ để giao hàng nhé");
            }

            Address address = addressService.findAddressOrThrow(request.getAddressId());

            String url = UriComponentsBuilder
                    .fromUriString("https://rsapi.goong.io/DistanceMatrix")
                    .queryParam("origins", StoreConstants.STORE_LATITUDE + "," + StoreConstants.STORE_LONGITUDE)
                    .queryParam("destinations", address.getLatitude() + "," + address.getLongitude())
                    .queryParam("vehicle", "car")
                    .queryParam("api_key", goongApiKey)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(url, String.class);

            JSONObject root = new JSONObject(json);
            JSONObject element = root.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0);

            double distanceMeters = element.getJSONObject("distance").getDouble("value");
            double distanceKm = normalizeDistance(distanceMeters);

            int durationSeconds = element.getJSONObject("duration").getInt("value");
            int durationMinutes = normalizeDuration(durationSeconds);

            int fee = (int) (StoreConstants.BASE_DELIVERY_FEE + distanceKm * StoreConstants.DELIVERY_FEE_PER_KM);

            if (distanceKm > StoreConstants.MAX_DELIVERY_RADIUS_KM) {
                return DeliveryDTO.reject("Quý khách thông cảm! Địa chỉ giao hàng không nên vượt quá bán kính 10km");
            }

            return DeliveryDTO.accept(distanceKm, durationMinutes, NumberUtils.roundToThousand(fee));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tính toán phí giao hàng " + e.getMessage());
        }
    }
}
