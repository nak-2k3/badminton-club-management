package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.service.ExpenseService;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;
import com.badmintonclub.clubmanagement.service.PaymentService;
import com.badmintonclub.clubmanagement.service.RegistrationService;
import com.badmintonclub.clubmanagement.service.ScheduleService;
import com.badmintonclub.clubmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

        @Autowired
        private UserService userService;

        @Autowired
        private ScheduleService scheduleService;

        @Autowired
        private RegistrationService registrationService;

        @Autowired
        private PaymentService paymentService;

        @Autowired
        private ExpenseService expenseService;

        @Autowired
        private GuestPaymentService guestPaymentService;

        @GetMapping("/dashboard")
        public String dashboard(Model model) {

                // Tổng quan CLB
                model.addAttribute("totalUsers", userService.countUsers());
                model.addAttribute("totalSchedules", scheduleService.countSchedules());
                model.addAttribute("activeUsers", userService.countActiveUsers());
                model.addAttribute("lockedUsers", userService.countLockedUsers());
                model.addAttribute("totalRegistrations", registrationService.countRegistrations());
                model.addAttribute("latestSchedules", scheduleService.getLatestSchedules());

                // Tiền thu từ thành viên chính thức
                Long memberTotalAmount = safeLong(paymentService.getTotalAmount());
                Long memberPaidAmount = safeLong(paymentService.getPaidAmount());
                Long memberUnpaidAmount = safeLong(paymentService.getUnpaidAmount());

                // Tiền thu từ khách vãng lai
                Long guestTotalAmount = safeLong(guestPaymentService.getTotalGuestAmount());
                Long guestPaidAmount = safeLong(guestPaymentService.getPaidGuestAmount());
                Long guestUnpaidAmount = safeLong(guestPaymentService.getUnpaidGuestAmount());

                // Tổng tiền thu = tiền thành viên + tiền khách vãng lai
                Long totalIncomeAmount = memberTotalAmount + guestTotalAmount;
                Long paidIncomeAmount = memberPaidAmount + guestPaidAmount;
                Long unpaidIncomeAmount = memberUnpaidAmount + guestUnpaidAmount;

                // Tổng chi
                Long expenseAmount = safeLong(expenseService.getTotalExpenseAmount());

                // Quỹ hiện có = tiền đã thu - tiền đã chi
                Long currentFund = paidIncomeAmount - expenseAmount;

                // Đẩy dữ liệu tài chính ra dashboard
                model.addAttribute("totalPaymentAmount", totalIncomeAmount);
                model.addAttribute("paidPaymentAmount", paidIncomeAmount);
                model.addAttribute("unpaidPaymentAmount", unpaidIncomeAmount);

                model.addAttribute(
                                "paidPaymentCount",
                                paymentService.countPaid() + guestPaymentService.countPaid());

                model.addAttribute(
                                "unpaidPaymentCount",
                                paymentService.countUnpaid() + guestPaymentService.countUnpaid());

                model.addAttribute("totalExpenseAmount", expenseAmount);
                model.addAttribute("currentFund", currentFund);
                model.addAttribute("totalExpenses", expenseService.countExpenses());

                return "dashboard";
        }

        private Long safeLong(Long value) {
                return value != null ? value : 0L;
        }
}