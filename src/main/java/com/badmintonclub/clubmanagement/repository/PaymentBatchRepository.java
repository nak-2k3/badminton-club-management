package com.badmintonclub.clubmanagement.repository;

import com.badmintonclub.clubmanagement.entity.PaymentBatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentBatchRepository extends JpaRepository<PaymentBatch, Long> {

        // Lấy danh sách đợt thu mới nhất lên đầu
        List<PaymentBatch> findAllByOrderByIdDesc();

        // Kiểm tra tạo trùng khoản thu
        boolean existsByTitleAndMonth(String title, String month);

        boolean existsByBatchTypeAndMonth(String batchType, String month);

        // Báo cáo thu chi theo tháng
        List<PaymentBatch> findByMonthOrderByIdDesc(String month);

        @Query(value = """
                         SELECT b
                         FROM PaymentBatch b
                         WHERE (:batchType IS NULL OR :batchType = '' OR b.batchType = :batchType)
                         AND (:month IS NULL OR :month = '' OR b.month = :month)
                         AND (:keyword IS NULL OR :keyword = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                         ORDER BY b.id DESC
                        """, countQuery = """
                         SELECT COUNT(b)
                         FROM PaymentBatch b
                         WHERE (:batchType IS NULL OR :batchType = '' OR b.batchType = :batchType)
                         AND (:month IS NULL OR :month = '' OR b.month = :month)
                         AND (:keyword IS NULL OR :keyword = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                        """)
        Page<PaymentBatch> searchBatches(
                        @Param("batchType") String batchType,
                        @Param("month") String month,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}