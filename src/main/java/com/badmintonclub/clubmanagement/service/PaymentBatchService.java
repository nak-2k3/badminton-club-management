package com.badmintonclub.clubmanagement.service;

import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.repository.PaymentBatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentBatchService {

    @Autowired
    private PaymentBatchRepository paymentBatchRepository;

    public List<PaymentBatch> getAllBatches() {
        return paymentBatchRepository.findAllByOrderByIdDesc();
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
}