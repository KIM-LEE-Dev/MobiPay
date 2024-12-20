package com.example.mobipay.domain.car.controller;

import com.example.mobipay.domain.car.dto.CarAutoPayChangeRequest;
import com.example.mobipay.domain.car.dto.CarAutoPayChangeResponse;
import com.example.mobipay.domain.car.dto.CarListResponse;
import com.example.mobipay.domain.car.dto.CarMemberListResponse;
import com.example.mobipay.domain.car.dto.CarRegisterRequest;
import com.example.mobipay.domain.car.dto.CarRegisterResponse;
import com.example.mobipay.domain.car.service.CarService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<CarRegisterResponse> registerCar(@RequestBody @Valid CarRegisterRequest request,
                                                           @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        CarRegisterResponse response = carService.registerCar(request, oauth2User);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CarListResponse> getCars(@AuthenticationPrincipal CustomOAuth2User oauth2User) {

        CarListResponse carListResponse = carService.getCars(oauth2User);

        if (carListResponse.getItems().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(carListResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/auto-pay")
    public ResponseEntity<CarAutoPayChangeResponse> changeAutoPayStatus(
            @RequestBody @Valid CarAutoPayChangeRequest request, @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        CarAutoPayChangeResponse response = carService.changeAutoPayStatus(request, oauth2User);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{carId}/members")
    public ResponseEntity<CarMemberListResponse> getCarMemberList(@PathVariable("carId") Long carId,
                                                                  @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        CarMemberListResponse response = carService.getCarMemberList(carId, oauth2User);
        return ResponseEntity.ok(response);
    }

}
