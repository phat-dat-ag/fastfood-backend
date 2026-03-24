package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.award.AwardUpdateResponse;
import com.example.fastfoodshop.service.AwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/awards")
@RequiredArgsConstructor
public class AwardController extends BaseController {
    private final AwardService awardService;

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<AwardUpdateResponse>> updateAwardActivation(
            @PathVariable("id") Long awardId,
            @RequestBody @Valid UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(awardService.updateAwardActivation(awardId, updateActivationRequest.activated()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseWrapper<AwardUpdateResponse>> deleteAward(
            @PathVariable("id") Long awardId
    ) {
        return okResponse(awardService.deleteAward(awardId));
    }
}
