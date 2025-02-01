package com.example.travelshooting.payment.controller;

import com.example.travelshooting.payment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partners/companies/{companyId}/products/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

//    @GetMapping
//    public ResponseEntity<CommonListResDto<SettlementResDto>> findAll(@PathVariable Long companyId,
//                                                                      @RequestParam(required = false) LocalDate startAt,
//                                                                      @RequestParam(required = false) LocalDate endAt,
//                                                                      Pageable pageable
//    ) {
//        Page<SettlementResDto> settlements = settlementService.findAll(companyId, startAt, endAt, pageable);
//        List<SettlementResDto> result = settlements.getContent();
//
//        return new ResponseEntity<>(new CommonListResDto<>("상품별 정산 내역 전체 조회 완료", result), HttpStatus.OK);
//    }
}
