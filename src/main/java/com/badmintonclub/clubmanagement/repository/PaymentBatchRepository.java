package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentBatchRepository extends JpaRepository<PaymentBatch, Long> {
    // lấy danh sách đợt thu mới nhất lên đầu
    List<PaymentBatch> findAllByOrderByIdDesc();

    // kiểm tra tạo trùng khoảng thu
    boolean existsByTitleAndMonth(String title, String month);

    boolean existsByBatchTypeAndMonth(String batchType, String month);
}