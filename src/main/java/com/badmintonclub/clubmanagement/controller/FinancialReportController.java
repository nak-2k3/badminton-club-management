package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.service.ExpenseService;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
public class FinancialReportController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private GuestPaymentService guestPaymentService;

    @GetMapping("/reports/finance")
    public String financialReport(
            @RequestParam(required = false) String monthKey,
            Model model) {
        YearMonth selectedMonth;

        if (monthKey == null || monthKey.isBlank()) {
            selectedMonth = YearMonth.now();
        } else {
            selectedMonth = YearMonth.parse(monthKey);
        }

        String month = selectedMonth.format(
                DateTimeFormatter.ofPattern("MM/yyyy"));

        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        var startDateTime = startDate.atStartOfDay();
        var endDateTime = endDate.plusDays(1).atStartOfDay();

        // Khoản thu từ thành viên chính thức
        Long memberTotalAmount = safeLong(
                paymentService.getTotalAmountByMonth(month));

        Long memberPaidAmount = safeLong(
                paymentService.getPaidAmountByMonth(month));

        Long memberUnpaidAmount = safeLong(
                paymentService.getUnpaidAmountByMonth(month));

        // Khoản thu từ khách vãng lai trong tháng
        Long guestTotalAmount = safeLong(
                guestPaymentService.getTotalGuestAmountBetween(
                        startDateTime,
                        endDateTime));

        Long guestPaidAmount = safeLong(
                guestPaymentService.getPaidGuestAmountBetween(
                        startDateTime,
                        endDateTime));

        Long guestUnpaidAmount = safeLong(
                guestPaymentService.getUnpaidGuestAmountBetween(
                        startDateTime,
                        endDateTime));

        // Tổng thu = thành viên + khách vãng lai
        Long totalAmount = memberTotalAmount + guestTotalAmount;
        Long paidAmount = memberPaidAmount + guestPaidAmount;
        Long unpaidAmount = memberUnpaidAmount + guestUnpaidAmount;

        // Tổng chi trong tháng
        Long expenseAmount = safeLong(
                expenseService.getTotalExpenseAmountBetween(
                        startDate,
                        endDate));

        // Quỹ còn lại trong tháng
        Long balance = paidAmount - expenseAmount;

        model.addAttribute("monthKey", selectedMonth.toString());
        model.addAttribute("month", month);

        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paidAmount", paidAmount);
        model.addAttribute("unpaidAmount", unpaidAmount);
        model.addAttribute("expenseAmount", expenseAmount);
        model.addAttribute("balance", balance);

        // Số khoản đã đóng = thành viên đã đóng + khách đã thu
        model.addAttribute(
                "paidCount",
                paymentService.countPaidByMonth(month)
                        + guestPaymentService.countPaidGuestBetween(
                                startDateTime,
                                endDateTime));

        // Số khoản chưa đóng = thành viên chưa đóng + khách chưa thu
        model.addAttribute(
                "unpaidCount",
                paymentService.countUnpaidByMonth(month)
                        + guestPaymentService.countUnpaidGuestBetween(
                                startDateTime,
                                endDateTime));

        // Danh sách khoản thu thành viên
        model.addAttribute(
                "batches",
                paymentBatchService.getBatchesByMonth(month));

        // Danh sách khoản chi
        model.addAttribute(
                "expenses",
                expenseService.getExpensesBetween(startDate, endDate));

        // Danh sách khách vãng lai trong tháng
        model.addAttribute(
                "guestPayments",
                guestPaymentService.getGuestPaymentsBetween(
                        startDateTime,
                        endDateTime));

        return "reports/finance";
    }

    private Long safeLong(Long value) {
        return value != null ? value : 0L;
    }
}