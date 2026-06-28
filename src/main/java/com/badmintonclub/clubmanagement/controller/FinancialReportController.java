package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.service.ExpenseService;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
public class FinancialReportController {

        private static final int PAGE_SIZE = 5;

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
                        @RequestParam(defaultValue = "0") int incomePage,
                        @RequestParam(defaultValue = "0") int guestPage,
                        @RequestParam(defaultValue = "0") int expensePage,
                        Model model) {
                if (incomePage < 0) {
                        incomePage = 0;
                }

                if (guestPage < 0) {
                        guestPage = 0;
                }

                if (expensePage < 0) {
                        expensePage = 0;
                }

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

                Long memberTotalAmount = safeLong(
                                paymentService.getTotalAmountByMonth(month));

                Long memberPaidAmount = safeLong(
                                paymentService.getPaidAmountByMonth(month));

                Long memberUnpaidAmount = safeLong(
                                paymentService.getUnpaidAmountByMonth(month));

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

                Long totalAmount = memberTotalAmount + guestTotalAmount;
                Long paidAmount = memberPaidAmount + guestPaidAmount;
                Long unpaidAmount = memberUnpaidAmount + guestUnpaidAmount;

                Long expenseAmount = safeLong(
                                expenseService.getTotalExpenseAmountBetween(
                                                startDate,
                                                endDate));

                Long balance = paidAmount - expenseAmount;

                Pageable incomePageable = PageRequest.of(incomePage, PAGE_SIZE);
                Pageable guestPageable = PageRequest.of(guestPage, PAGE_SIZE);
                Pageable expensePageable = PageRequest.of(expensePage, PAGE_SIZE);

                Page<PaymentBatch> batchPage = paymentBatchService.searchBatches(
                                "",
                                month,
                                "",
                                incomePageable);

                Page<GuestPayment> guestPaymentPage = guestPaymentService.getGuestPaymentsPageBetween(
                                startDateTime,
                                endDateTime,
                                guestPageable);

                Page<Expense> expensePageData = expenseService.searchExpenses(
                                startDate,
                                endDate,
                                "",
                                "",
                                expensePageable);

                model.addAttribute("monthKey", selectedMonth.toString());
                model.addAttribute("month", month);

                model.addAttribute("totalAmount", totalAmount);
                model.addAttribute("paidAmount", paidAmount);
                model.addAttribute("unpaidAmount", unpaidAmount);
                model.addAttribute("expenseAmount", expenseAmount);
                model.addAttribute("balance", balance);

                model.addAttribute("memberTotalAmount", memberTotalAmount);
                model.addAttribute("memberPaidAmount", memberPaidAmount);
                model.addAttribute("memberUnpaidAmount", memberUnpaidAmount);

                model.addAttribute("guestTotalAmount", guestTotalAmount);
                model.addAttribute("guestPaidAmount", guestPaidAmount);
                model.addAttribute("guestUnpaidAmount", guestUnpaidAmount);

                model.addAttribute(
                                "paidCount",
                                paymentService.countPaidByMonth(month)
                                                + guestPaymentService.countPaidGuestBetween(
                                                                startDateTime,
                                                                endDateTime));

                model.addAttribute(
                                "unpaidCount",
                                paymentService.countUnpaidByMonth(month)
                                                + guestPaymentService.countUnpaidGuestBetween(
                                                                startDateTime,
                                                                endDateTime));

                model.addAttribute("batches", batchPage.getContent());
                model.addAttribute("batchPage", batchPage);
                model.addAttribute("incomeCurrentPage", batchPage.getNumber());
                model.addAttribute("incomeTotalPages", batchPage.getTotalPages());
                model.addAttribute("incomeTotalItems", batchPage.getTotalElements());

                model.addAttribute("guestPayments", guestPaymentPage.getContent());
                model.addAttribute("guestPaymentPage", guestPaymentPage);
                model.addAttribute("guestCurrentPage", guestPaymentPage.getNumber());
                model.addAttribute("guestTotalPages", guestPaymentPage.getTotalPages());
                model.addAttribute("guestTotalItems", guestPaymentPage.getTotalElements());

                model.addAttribute("expenses", expensePageData.getContent());
                model.addAttribute("expensePage", expensePageData);
                model.addAttribute("expenseCurrentPage", expensePageData.getNumber());
                model.addAttribute("expenseTotalPages", expensePageData.getTotalPages());
                model.addAttribute("expenseTotalItems", expensePageData.getTotalElements());

                model.addAttribute("size", PAGE_SIZE);

                return "reports/finance";
        }

        private Long safeLong(Long value) {
                return value != null ? value : 0L;
        }
}