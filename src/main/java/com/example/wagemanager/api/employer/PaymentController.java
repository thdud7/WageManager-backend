package com.example.wagemanager.api.employer;

import com.example.wagemanager.common.dto.ApiResponse;
import com.example.wagemanager.domain.payment.dto.PaymentDto;
import com.example.wagemanager.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employer/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 급여 송금 처리 (카카오페이 고정)
     * 고용주가 버튼을 누르면 급여가 카카오페이로 송금 완료 상태로 변경
     */
    @PostMapping
    public ApiResponse<PaymentDto.Response> processPayment(
            @Valid @RequestBody PaymentDto.PaymentRequest request) {
        return ApiResponse.success(paymentService.processPayment(request));
    }

    /**
     * 송금 상세 조회
     */
    @GetMapping("/{id}")
    public ApiResponse<PaymentDto.Response> getPaymentById(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getPaymentById(id));
    }

    /**
     * 사업장별 송금 목록 조회
     */
    @GetMapping
    public ApiResponse<List<PaymentDto.ListResponse>> getPaymentsByWorkplace(
            @RequestParam Long workplaceId) {
        return ApiResponse.success(paymentService.getPaymentsByWorkplace(workplaceId));
    }

    /**
     * 사업장별 송금 목록 조회 (연월 기준)
     */
    @GetMapping("/year-month")
    public ApiResponse<List<PaymentDto.ListResponse>> getPaymentsByYearMonth(
            @RequestParam Long workplaceId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(paymentService.getPaymentsByWorkplaceAndYearMonth(workplaceId, year, month));
    }

    /**
     * 미송금자 목록 조회 - 특정 연월의 미송금자
     * 조회 범위: PENDING(대기) + FAILED(실패) 상태
     * 파라미터: workplaceId, year, month (연월 지정 필수)
     */
    @GetMapping("/unpaid")
    public ApiResponse<List<PaymentDto.ListResponse>> getUnpaidPayments(
            @RequestParam Long workplaceId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(paymentService.getUnpaidPayments(workplaceId, year, month));
    }

    /**
     * 송금 대기 목록 조회 - 현재 사업장의 모든 PENDING 항목
     * 조회 범위: PENDING(대기) 상태만
     * 파라미터: workplaceId (연월 미지정 - 모든 기간 포함)
     */
    @GetMapping("/pending")
    public ApiResponse<List<PaymentDto.ListResponse>> getPendingPaymentsByWorkplace(
            @RequestParam Long workplaceId) {
        return ApiResponse.success(paymentService.getPendingPaymentsByWorkplace(workplaceId));
    }

}
