package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Schedule;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

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

                LocalDate today = LocalDate.now();

                LocalDateTime startOfToday = today.atStartOfDay();
                LocalDateTime endOfToday = today.plusDays(1).atStartOfDay();

                YearMonth currentMonth = YearMonth.now();

                LocalDate startOfMonth = currentMonth.atDay(1);
                LocalDate endOfMonth = currentMonth.atEndOfMonth();

                LocalDateTime startOfMonthTime = startOfMonth.atStartOfDay();
                LocalDateTime endOfMonthTime = endOfMonth.plusDays(1).atStartOfDay();

                String month = currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy"));

                List<Schedule> todaySchedules = scheduleService.getAllSchedules()
                                .stream()
                                .filter(schedule -> schedule.getPlayTime() != null)
                                .filter(schedule -> !schedule.getPlayTime().isBefore(startOfToday))
                                .filter(schedule -> schedule.getPlayTime().isBefore(endOfToday))
                                .sorted(Comparator.comparing(Schedule::getPlayTime))
                                .toList();

                List<Schedule> upcomingSchedules = scheduleService.getAllSchedules()
                                .stream()
                                .filter(schedule -> schedule.getPlayTime() != null)
                                .filter(schedule -> schedule.getPlayTime().isAfter(LocalDateTime.now()))
                                .sorted(Comparator.comparing(Schedule::getPlayTime))
                                .limit(5)
                                .toList();

                for (Schedule schedule : todaySchedules) {
                        schedule.setCurrentPlayers(registrationService.countParticipants(schedule));
                }

                for (Schedule schedule : upcomingSchedules) {
                        schedule.setCurrentPlayers(registrationService.countParticipants(schedule));
                }

                Long allMemberPaidAmount = safeLong(paymentService.getPaidAmount());
                Long allGuestPaidAmount = safeLong(guestPaymentService.getPaidGuestAmount());
                Long allExpenseAmount = safeLong(expenseService.getTotalExpenseAmount());

                Long currentFund = allMemberPaidAmount + allGuestPaidAmount - allExpenseAmount;

                Long monthMemberPaidAmount = safeLong(paymentService.getPaidAmountByMonth(month));
                Long monthMemberUnpaidAmount = safeLong(paymentService.getUnpaidAmountByMonth(month));

                Long monthGuestPaidAmount = safeLong(
                                guestPaymentService.getPaidGuestAmountBetween(
                                                startOfMonthTime,
                                                endOfMonthTime));

                Long monthGuestUnpaidAmount = safeLong(
                                guestPaymentService.getUnpaidGuestAmountBetween(
                                                startOfMonthTime,
                                                endOfMonthTime));

                Long monthPaidAmount = monthMemberPaidAmount + monthGuestPaidAmount;
                Long monthUnpaidAmount = monthMemberUnpaidAmount + monthGuestUnpaidAmount;

                Long monthExpenseAmount = safeLong(
                                expenseService.getTotalExpenseAmountBetween(
                                                startOfMonth,
                                                endOfMonth));

                Long monthBalance = monthPaidAmount - monthExpenseAmount;

                var todayGuestPayments = guestPaymentService.getGuestPaymentsBetween(
                                startOfToday,
                                endOfToday);

                model.addAttribute("totalUsers", userService.countUsers());
                model.addAttribute("activeUsers", userService.countActiveUsers());
                model.addAttribute("lockedUsers", userService.countLockedUsers());

                model.addAttribute("todaySchedules", todaySchedules);
                model.addAttribute("todaySchedulesCount", todaySchedules.size());

                model.addAttribute("upcomingSchedules", upcomingSchedules);

                model.addAttribute("todayGuestCount", todayGuestPayments.size());

                model.addAttribute(
                                "todayGuestPaidAmount",
                                safeLong(guestPaymentService.getPaidGuestAmountBetween(startOfToday, endOfToday)));

                model.addAttribute(
                                "todayGuestUnpaidAmount",
                                safeLong(guestPaymentService.getUnpaidGuestAmountBetween(startOfToday, endOfToday)));

                model.addAttribute("currentFund", currentFund);

                model.addAttribute("month", month);
                model.addAttribute("monthPaidAmount", monthPaidAmount);
                model.addAttribute("monthUnpaidAmount", monthUnpaidAmount);
                model.addAttribute("monthExpenseAmount", monthExpenseAmount);
                model.addAttribute("monthBalance", monthBalance);

                return "dashboard";
        }

        private Long safeLong(Long value) {
                return value != null ? value : 0L;
        }
}