package com.kyeongho.biz.repository;

import com.kyeongho.biz.entity.IncomeTaxDeductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeTaxDeductionRepository extends JpaRepository<IncomeTaxDeductionEntity, Long> {
}