package com.example.wagemanager.domain.salary.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 세금 및 보험료 계산 유틸리티
 *
 * 대상자:
 * - 프리랜서: 소득세 3% + 지방소득세 0.3%
 * - 비정규직(알바): 세금/보험료 적용 여부에 따라 계산
 */
public class TaxCalculator {

    /**
     * 프리랜서 세금 계산
     * - 소득세: 3%
     * - 지방소득세: 0.3%
     */
    public static class FreelancerTax {
        public BigDecimal incomeTax;        // 소득세 3%
        public BigDecimal localIncomeTax;   // 지방소득세 0.3%
        public BigDecimal totalTax;         // 총 세금

        public FreelancerTax(BigDecimal grossPay) {
            this.incomeTax = grossPay.multiply(new BigDecimal("0.03"))
                .setScale(0, RoundingMode.DOWN); // 원단위 절사
            this.localIncomeTax = grossPay.multiply(new BigDecimal("0.003"))
                .setScale(0, RoundingMode.DOWN); // 원단위 절사
            this.totalTax = this.incomeTax.add(this.localIncomeTax);
        }
    }

    /**
     * 비정규직 세금/보험료 적용 유형
     */
    public enum PartTimeTaxType {
        NONE,              // 세금 없음 + 4대보험 없음
        INCOME_TAX_ONLY,   // 세금만 적용 + 4대보험 없음
        INCOME_TAX_AND_INSURANCE  // 세금 + 4대보험 모두 적용
    }

    // 최소 국민연금 보험료 기준 월급 (39만원)
    private static final BigDecimal MINIMUM_PENSION_WAGE = new BigDecimal("390000");

    // 4대보험 요율 (비정규직)
    private static final BigDecimal NATIONAL_PENSION_RATE = new BigDecimal("0.045");     // 국민연금 4.5%
    private static final BigDecimal HEALTH_INSURANCE_RATE = new BigDecimal("0.03545");  // 건강보험 3.545%
    private static final BigDecimal LONG_TERM_CARE_RATE = new BigDecimal("0.1295");    // 장기요양보험 (건강보험의 12.95%)
    private static final BigDecimal EMPLOYMENT_INSURANCE_RATE = new BigDecimal("0.009"); // 고용보험 0.9%

    /**
     * 비정규직 세금 및 보험료 계산
     */
    public static class PartTimeTax {
        public BigDecimal nationalPension;     // 국민연금 4.5% (최소 39만원 기준)
        public BigDecimal healthInsurance;     // 건강보험 3.545%
        public BigDecimal longTermCare;        // 장기요양보험
        public BigDecimal employmentInsurance; // 고용보험 0.9%
        public BigDecimal totalInsurance;      // 총 보험료

        public BigDecimal incomeTax;           // 소득세 (간이세액표 기준)
        public BigDecimal localIncomeTax;      // 지방소득세 (소득세의 10%)
        public BigDecimal totalTax;            // 총 세금

        public BigDecimal totalDeduction;      // 총 공제 (보험료 + 세금)

        public PartTimeTax(BigDecimal grossPay, PartTimeTaxType taxType) {
            switch (taxType) {
                case NONE:
                    // 세금 없음 + 4대보험 없음
                    this.nationalPension = BigDecimal.ZERO;
                    this.healthInsurance = BigDecimal.ZERO;
                    this.longTermCare = BigDecimal.ZERO;
                    this.employmentInsurance = BigDecimal.ZERO;
                    this.totalInsurance = BigDecimal.ZERO;
                    this.incomeTax = BigDecimal.ZERO;
                    this.localIncomeTax = BigDecimal.ZERO;
                    this.totalTax = BigDecimal.ZERO;
                    this.totalDeduction = BigDecimal.ZERO;
                    break;

                case INCOME_TAX_ONLY:
                    // 세금만 적용 + 4대보험 없음
                    this.nationalPension = BigDecimal.ZERO;
                    this.healthInsurance = BigDecimal.ZERO;
                    this.longTermCare = BigDecimal.ZERO;
                    this.employmentInsurance = BigDecimal.ZERO;
                    this.totalInsurance = BigDecimal.ZERO;

                    // 소득세 계산 (간이세액표 기준, 간단한 근사)
                    this.incomeTax = calculateSimpleIncomeTax(grossPay)
                        .setScale(0, RoundingMode.DOWN); // 원단위 절사
                    this.localIncomeTax = this.incomeTax.multiply(new BigDecimal("0.1"))
                        .setScale(0, RoundingMode.DOWN);

                    this.totalTax = this.incomeTax.add(this.localIncomeTax);
                    this.totalDeduction = this.totalTax;
                    break;

                case INCOME_TAX_AND_INSURANCE:
                    // 세금 + 4대보험 모두 적용
                    // 보험료 계산
                    BigDecimal pensionBaseSalary = grossPay.compareTo(MINIMUM_PENSION_WAGE) < 0
                        ? MINIMUM_PENSION_WAGE
                        : grossPay;

                    // 국민연금 기준소득월액: 1천원 미만 절사
                    pensionBaseSalary = pensionBaseSalary.setScale(-3, RoundingMode.DOWN);

                    this.nationalPension = pensionBaseSalary.multiply(NATIONAL_PENSION_RATE)
                        .setScale(0, RoundingMode.DOWN); // 원단위 절사

                    this.healthInsurance = grossPay.multiply(HEALTH_INSURANCE_RATE)
                        .setScale(0, RoundingMode.DOWN);

                    this.longTermCare = this.healthInsurance.multiply(LONG_TERM_CARE_RATE)
                        .setScale(0, RoundingMode.DOWN);

                    this.employmentInsurance = grossPay.multiply(EMPLOYMENT_INSURANCE_RATE)
                        .setScale(0, RoundingMode.DOWN);

                    this.totalInsurance = this.nationalPension.add(this.healthInsurance)
                        .add(this.longTermCare).add(this.employmentInsurance);

                    // 소득세 계산 (간이세액표 기준, 간단한 근사)
                    this.incomeTax = calculateSimpleIncomeTax(grossPay)
                        .setScale(0, RoundingMode.DOWN); // 원단위 절사
                    this.localIncomeTax = this.incomeTax.multiply(new BigDecimal("0.1"))
                        .setScale(0, RoundingMode.DOWN);

                    this.totalTax = this.incomeTax.add(this.localIncomeTax);
                    this.totalDeduction = this.totalInsurance.add(this.totalTax);
                    break;
            }
        }
    }

    /**
     * 프리랜서 세금 계산
     */
    public static FreelancerTax calculateFreelancerTax(BigDecimal grossPay) {
        return new FreelancerTax(grossPay);
    }

    /**
     * 비정규직 세금 및 보험료 계산
     */
    public static PartTimeTax calculatePartTimeTax(BigDecimal grossPay, PartTimeTaxType taxType) {
        return new PartTimeTax(grossPay, taxType);
    }

    /**
     * 비정규직 세금 및 보험료 계산 (하위 호환성 유지)
     * @deprecated Use {@link #calculatePartTimeTax(BigDecimal, PartTimeTaxType)} instead
     */
    @Deprecated
    public static PartTimeTax calculatePartTimeTax(BigDecimal grossPay, Boolean applyInsuranceAndTax) {
        PartTimeTaxType taxType = applyInsuranceAndTax
            ? PartTimeTaxType.INCOME_TAX_AND_INSURANCE
            : PartTimeTaxType.NONE;
        return new PartTimeTax(grossPay, taxType);
    }

    /**
     * 간단한 소득세 계산 (간이세액표 근사)
     * Todo: 실제 간이세액표를 반영한 정확한 계산 로직으로 대체 필요
     */
    private static BigDecimal calculateSimpleIncomeTax(BigDecimal grossPay) {
        // 매우 간단한 근사: 대략 3~5%
        // 실제 간이세액표를 사용하려면 더 복잡한 로직 필요
        if (grossPay.compareTo(new BigDecimal("1000000")) < 0) {
            return grossPay.multiply(new BigDecimal("0.03"));
        } else if (grossPay.compareTo(new BigDecimal("2000000")) < 0) {
            return grossPay.multiply(new BigDecimal("0.035"));
        } else {
            return grossPay.multiply(new BigDecimal("0.04"));
        }
    }
}
