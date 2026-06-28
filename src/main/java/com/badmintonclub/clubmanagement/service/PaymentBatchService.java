package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.repository.PaymentBatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentBatchService {

    @Autowired
    private PaymentBatchRepository paymentBatchRepository;

    public List<PaymentBatch> getAllBatches() {
        return paymentBatchRepository.findAllByOrderByIdDesc();
    }

    public Page<PaymentBatch> searchBatches(
            String batchType,
            String month,
            String keyword,
            Pageable pageable) {
        return paymentBatchRepository.searchBatches(
                batchType,
                month,
                keyword,
                pageable);
    }

    public PaymentBatch getBatchById(Long id) {
        return paymentBatchRepository.findById(id).orElse(null);
    }

    public PaymentBatch saveBatch(PaymentBatch batch) {
        return paymentBatchRepository.save(batch);
    }

    public boolean existsByTitleAndMonth(String title, String month) {
        return paymentBatchRepository.existsByTitleAndMonth(title, month);
    }

    public boolean existsMonthlyBatchByMonth(String month) {
        return paymentBatchRepository.existsByBatchTypeAndMonth("MONTHLY", month);
    }

    // Thu chi theo tháng
    public List<PaymentBatch> getBatchesByMonth(String month) {
        return paymentBatchRepository.findByMonthOrderByIdDesc(month);
    }
}