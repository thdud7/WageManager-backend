package com.example.wagemanager.api.employer;

import com.example.wagemanager.common.dto.ApiResponse;
import com.example.wagemanager.domain.salary.dto.SalaryDto;
import com.example.wagemanager.domain.salary.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employer/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * 급여 목록 조회
     * 사업장별 모든 급여 목록
     */
    @GetMapping
    public ApiResponse<List<SalaryDto.ListResponse>> getSalariesByWorkplace(
            @RequestParam Long workplaceId) {
        return ApiResponse.success(salaryService.getSalariesByWorkplace(workplaceId));
    }

    /**
     * 급여 목록 조회 (연월 기준)
     */
    @GetMapping("/year-month")
    public ApiResponse<List<SalaryDto.ListResponse>> getSalariesByYearMonth(
            @RequestParam Long workplaceId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(salaryService.getSalariesByWorkplaceAndYearMonth(workplaceId, year, month));
    }

    /**
     * 급여 상세 조회
     */
    @GetMapping("/{id}")
    public ApiResponse<SalaryDto.Response> getSalaryById(@PathVariable Long id) {
        return ApiResponse.success(salaryService.getSalaryById(id));
    }

    /**
     * 급여 자동 계산 (근무 기록 기반)
     * - 해당 계약의 해당 월 모든 근무 기록을 조회
     * - 각 근무 기록의 시간을 합산
     * - 고용 형태에 따라 세금/보험료 계산
     * - 순급여 자동 산출
     */
    @PostMapping("/contracts/{contractId}/calculate")
    public ApiResponse<SalaryDto.Response> calculateSalaryByWorkRecords(
            @PathVariable Long contractId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(salaryService.calculateSalaryByWorkRecords(contractId, year, month));
    }
}
