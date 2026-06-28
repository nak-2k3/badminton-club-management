package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.GuestPayment;
import com.badmintonclub.clubmanagement.entity.Schedule;
import com.badmintonclub.clubmanagement.entity.enums.ScheduleStatus;
import com.badmintonclub.clubmanagement.service.FeeSettingService;
import com.badmintonclub.clubmanagement.service.GuestPaymentService;
import com.badmintonclub.clubmanagement.service.RegistrationService;
import com.badmintonclub.clubmanagement.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Controller
public class GuestPaymentController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private GuestPaymentService guestPaymentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FeeSettingService feeSettingService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/guest-payments")
    public String listGuestPayments(
            @RequestParam(defaultValue = "today") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) String dateKey,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<GuestPayment> guestPaymentPage;

        Long totalGuestAmount;
        Long paidGuestAmount;
        Long unpaidGuestAmount;

        String pageTitle;
        List<Schedule> schedulesForFilter = List.of();
        Long selectedScheduleId = null;

        if ("month".equals(filter)) {
            YearMonth currentMonth = YearMonth.now();

            LocalDateTime startDateTime = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime endDateTime = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

            schedulesForFilter = getSchedulesBetween(startDateTime, endDateTime);

            Schedule selectedSchedule = getValidSelectedSchedule(scheduleId, schedulesForFilter);

            if (selectedSchedule != null) {
                selectedScheduleId = selectedSchedule.getId();

                guestPaymentPage = guestPaymentService.getGuestPaymentsPageBySchedule(
                        selectedSchedule,
                        pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmountBySchedule(selectedSchedule);
                paidGuestAmount = guestPaymentService.getPaidGuestAmountBySchedule(selectedSchedule);
                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmountBySchedule(selectedSchedule);

                pageTitle = "Khách vãng lai tháng này - " + selectedSchedule.getTitle();
            } else {
                guestPaymentPage = guestPaymentService.getGuestPaymentsPageBetween(
                        startDateTime,
                        endDateTime,
                        pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                paidGuestAmount = guestPaymentService.getPaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                pageTitle = "Quản lý khách vãng lai tháng này";
            }

            dateKey = "";

        } else if ("all".equals(filter)) {
            LocalDate selectedDate = parseDateKey(dateKey);

            if (selectedDate != null) {
                LocalDateTime startDateTime = selectedDate.atStartOfDay();
                LocalDateTime endDateTime = selectedDate.plusDays(1).atStartOfDay();

                guestPaymentPage = guestPaymentService.getGuestPaymentsPageBetween(
                        startDateTime,
                        endDateTime,
                        pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                paidGuestAmount = guestPaymentService.getPaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                pageTitle = "Khách vãng lai ngày " + formatDateDisplay(selectedDate);
                dateKey = selectedDate.toString();
            } else {
                guestPaymentPage = guestPaymentService.getAllGuestPaymentsPage(pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmount();
                paidGuestAmount = guestPaymentService.getPaidGuestAmount();
                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmount();

                pageTitle = "Tất cả khách vãng lai";
                dateKey = "";
            }

        } else {
            filter = "today";

            LocalDate today = LocalDate.now();

            LocalDateTime startDateTime = today.atStartOfDay();
            LocalDateTime endDateTime = today.plusDays(1).atStartOfDay();

            schedulesForFilter = getSchedulesBetween(startDateTime, endDateTime);

            Schedule selectedSchedule = getValidSelectedSchedule(scheduleId, schedulesForFilter);

            if (selectedSchedule != null) {
                selectedScheduleId = selectedSchedule.getId();

                guestPaymentPage = guestPaymentService.getGuestPaymentsPageBySchedule(
                        selectedSchedule,
                        pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmountBySchedule(selectedSchedule);
                paidGuestAmount = guestPaymentService.getPaidGuestAmountBySchedule(selectedSchedule);
                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmountBySchedule(selectedSchedule);

                pageTitle = "Khách vãng lai hôm nay - " + selectedSchedule.getTitle();
            } else {
                guestPaymentPage = guestPaymentService.getGuestPaymentsPageBetween(
                        startDateTime,
                        endDateTime,
                        pageable);

                totalGuestAmount = guestPaymentService.getTotalGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                paidGuestAmount = guestPaymentService.getPaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                unpaidGuestAmount = guestPaymentService.getUnpaidGuestAmountBetween(
                        startDateTime,
                        endDateTime);

                pageTitle = "Quản lý khách vãng lai hôm nay";
            }

            dateKey = "";
        }

        model.addAttribute("guestPaymentPage", guestPaymentPage);
        model.addAttribute("guestPayments", guestPaymentPage.getContent());

        model.addAttribute("totalGuestAmount", safeLong(totalGuestAmount));
        model.addAttribute("paidGuestAmount", safeLong(paidGuestAmount));
        model.addAttribute("unpaidGuestAmount", safeLong(unpaidGuestAmount));

        model.addAttribute("filter", filter);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("currentPage", guestPaymentPage.getNumber());
        model.addAttribute("totalPages", guestPaymentPage.getTotalPages());
        model.addAttribute("totalItems", guestPaymentPage.getTotalElements());

        model.addAttribute("size", PAGE_SIZE);
        model.addAttribute("schedulesForFilter", schedulesForFilter);
        model.addAttribute("selectedScheduleId", selectedScheduleId);
        model.addAttribute("dateKey", dateKey);

        return "guest-payments/list";
    }

    @GetMapping("/guest-payments/create")
    public String showCreateForm(Model model) {

        GuestPayment guestPayment = new GuestPayment();

        guestPayment.setAmount(
                feeSettingService.getCurrentSetting().getGuestSessionFee());

        LocalDateTime now = LocalDateTime.now();

        List<Schedule> availableSchedules = scheduleService.getAllSchedules()
                .stream()
                .filter(schedule -> schedule.getStatus() == ScheduleStatus.OPEN)
                .filter(schedule -> schedule.getPlayTime() != null)
                .filter(schedule -> schedule.getPlayTime().isAfter(now))
                .filter(schedule -> !registrationService.isScheduleFull(schedule))
                .sorted(Comparator.comparing(Schedule::getPlayTime))
                .toList();

        model.addAttribute("guestPayment", guestPayment);
        model.addAttribute("schedules", availableSchedules);

        return "guest-payments/create";
    }

    @PostMapping("/guest-payments/save")
    public String saveGuestPayment(
            @RequestParam Long scheduleId,
            @ModelAttribute GuestPayment guestPayment) {

        Schedule schedule = scheduleService.getScheduleById(scheduleId);

        if (schedule == null) {
            return "redirect:/guest-payments/create?scheduleNotFound";
        }

        if (registrationService.isScheduleFull(schedule)) {
            return "redirect:/guest-payments/create?full";
        }

        guestPayment.setSchedule(schedule);

        guestPaymentService.saveGuestPayment(guestPayment);

        return "redirect:/guest-payments";
    }

    @GetMapping("/guest-payments/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        GuestPayment guestPayment = guestPaymentService.getGuestPaymentById(id);

        if (guestPayment == null) {
            return "redirect:/guest-payments";
        }

        model.addAttribute("guestPayment", guestPayment);
        model.addAttribute("schedules", scheduleService.getAllSchedules());

        return "guest-payments/edit";
    }

    @PostMapping("/guest-payments/update")
    public String updateGuestPayment(
            @RequestParam Long id,
            @RequestParam Long scheduleId,
            @ModelAttribute GuestPayment formGuestPayment) {

        GuestPayment guestPayment = guestPaymentService.getGuestPaymentById(id);

        if (guestPayment == null) {
            return "redirect:/guest-payments";
        }

        Schedule newSchedule = scheduleService.getScheduleById(scheduleId);

        if (newSchedule == null) {
            return "redirect:/guest-payments/edit/" + id + "?scheduleNotFound";
        }

        Schedule oldSchedule = guestPayment.getSchedule();

        boolean changingSchedule = oldSchedule == null
                || oldSchedule.getId() == null
                || !oldSchedule.getId().equals(newSchedule.getId());

        if (changingSchedule && registrationService.isScheduleFull(newSchedule)) {
            return "redirect:/guest-payments/edit/" + id + "?full";
        }

        guestPayment.setSchedule(newSchedule);
        guestPayment.setGuestName(formGuestPayment.getGuestName());
        guestPayment.setPhone(formGuestPayment.getPhone());
        guestPayment.setAmount(formGuestPayment.getAmount());
        guestPayment.setNote(formGuestPayment.getNote());

        guestPaymentService.saveGuestPayment(guestPayment);

        return "redirect:/guest-payments";
    }

    @PostMapping("/guest-payments/paid/{id}")
    public String markAsPaid(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Tiền mặt") String paymentMethod,
            @RequestParam(defaultValue = "today") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) String dateKey) {

        guestPaymentService.markAsPaid(id, paymentMethod);

        return buildRedirect(filter, page, scheduleId, dateKey);
    }

    @PostMapping("/guest-payments/unpaid/{id}")
    public String markAsUnpaid(
            @PathVariable Long id,
            @RequestParam(defaultValue = "today") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) String dateKey) {

        guestPaymentService.markAsUnpaid(id);

        return buildRedirect(filter, page, scheduleId, dateKey);
    }

    @GetMapping("/guest-payments/delete/{id}")
    public String deleteGuestPayment(
            @PathVariable Long id,
            @RequestParam(defaultValue = "today") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) String dateKey) {

        guestPaymentService.deleteGuestPayment(id);

        return buildRedirect(filter, page, scheduleId, dateKey);
    }

    private List<Schedule> getSchedulesBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {

        return scheduleService.getAllSchedules()
                .stream()
                .filter(schedule -> schedule.getPlayTime() != null)
                .filter(schedule -> !schedule.getPlayTime().isBefore(startDateTime))
                .filter(schedule -> schedule.getPlayTime().isBefore(endDateTime))
                .sorted(Comparator.comparing(Schedule::getPlayTime))
                .toList();
    }

    private Schedule getValidSelectedSchedule(
            Long scheduleId,
            List<Schedule> schedulesForFilter) {

        if (scheduleId == null) {
            return null;
        }

        return schedulesForFilter
                .stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst()
                .orElse(null);
    }

    private LocalDate parseDateKey(String dateKey) {
        if (dateKey == null || dateKey.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(dateKey);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String formatDateDisplay(LocalDate date) {
        return String.format(
                "%02d/%02d/%04d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }

    private String buildRedirect(
            String filter,
            int page,
            Long scheduleId,
            String dateKey) {

        if (filter == null || filter.isBlank()) {
            filter = "today";
        }

        if (page < 0) {
            page = 0;
        }

        StringBuilder redirect = new StringBuilder("redirect:/guest-payments?filter=");
        redirect.append(filter);
        redirect.append("&page=").append(page);

        if (scheduleId != null && !"all".equals(filter)) {
            redirect.append("&scheduleId=").append(scheduleId);
        }

        if (dateKey != null && !dateKey.isBlank() && "all".equals(filter)) {
            redirect.append("&dateKey=").append(dateKey);
        }

        return redirect.toString();
    }

    private Long safeLong(Long value) {
        return value != null ? value : 0L;
    }
}