package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.Expense;
import com.badmintonclub.clubmanagement.entity.PaymentBatch;
import com.badmintonclub.clubmanagement.service.ExpenseService;
import com.badmintonclub.clubmanagement.service.PaymentBatchService;
import com.badmintonclub.clubmanagement.service.PaymentService;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class FinancialReportExportController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentBatchService paymentBatchService;

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/reports/finance/export")
    public void exportFinanceReport(
            @RequestParam String monthKey,
            HttpServletResponse response) throws IOException {

        YearMonth selectedMonth = YearMonth.parse(monthKey);

        String month = selectedMonth.format(
                DateTimeFormatter.ofPattern("MM/yyyy"));

        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        Long totalAmount = paymentService.getTotalAmountByMonth(month);
        Long paidAmount = paymentService.getPaidAmountByMonth(month);
        Long unpaidAmount = paymentService.getUnpaidAmountByMonth(month);
        Long expenseAmount = expenseService.getTotalExpenseAmountBetween(
                startDate,
                endDate);

        Long balance = safeLong(paidAmount) - safeLong(expenseAmount);

        List<PaymentBatch> batches = paymentBatchService.getBatchesByMonth(month);
        List<Expense> expenses = expenseService.getExpensesBetween(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();

        CellStyle moneyStyle = createMoneyStyle(workbook);

        Sheet summarySheet = workbook.createSheet("Tong quan");
        createSummarySheet(
                summarySheet,
                month,
                totalAmount,
                paidAmount,
                unpaidAmount,
                expenseAmount,
                balance,
                moneyStyle);

        Sheet incomeSheet = workbook.createSheet("Khoan thu");
        createIncomeSheet(incomeSheet, batches);

        Sheet expenseSheet = workbook.createSheet("Khoan chi");
        createExpenseSheet(
                expenseSheet,
                expenses,
                moneyStyle);

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=bao-cao-thu-chi-" + monthKey + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void createSummarySheet(
            Sheet sheet,
            String month,
            Long totalAmount,
            Long paidAmount,
            Long unpaidAmount,
            Long expenseAmount,
            Long balance,
            CellStyle moneyStyle) {
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Báo cáo thu chi tháng " + month);

        Row row1 = sheet.createRow(2);
        row1.createCell(0).setCellValue("Tổng phải thu");
        setMoneyCell(row1, 1, totalAmount, moneyStyle);

        Row row2 = sheet.createRow(3);
        row2.createCell(0).setCellValue("Đã thu");
        setMoneyCell(row2, 1, paidAmount, moneyStyle);

        Row row3 = sheet.createRow(4);
        row3.createCell(0).setCellValue("Chưa thu");
        setMoneyCell(row3, 1, unpaidAmount, moneyStyle);

        Row row4 = sheet.createRow(5);
        row4.createCell(0).setCellValue("Đã chi");
        setMoneyCell(row4, 1, expenseAmount, moneyStyle);

        Row row5 = sheet.createRow(6);
        row5.createCell(0).setCellValue("Quỹ còn lại trong tháng");
        setMoneyCell(row5, 1, balance, moneyStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createIncomeSheet(
            Sheet sheet,
            List<PaymentBatch> batches) {
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên khoản thu");
        header.createCell(2).setCellValue("Tháng");
        header.createCell(3).setCellValue("Hạn chót");
        header.createCell(4).setCellValue("Ghi chú");

        int rowIndex = 1;
        int stt = 1;

        for (PaymentBatch batch : batches) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(batch.getTitle());
            row.createCell(2).setCellValue(batch.getMonth());

            row.createCell(3).setCellValue(
                    batch.getDueDate() != null
                            ? batch.getDueDate().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "-");

            row.createCell(4).setCellValue(
                    batch.getNote() != null ? batch.getNote() : "-");
        }

        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createExpenseSheet(
            Sheet sheet,
            List<Expense> expenses,
            CellStyle moneyStyle) {
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tên khoản chi");
        header.createCell(2).setCellValue("Số tiền");
        header.createCell(3).setCellValue("Ngày chi");
        header.createCell(4).setCellValue("Hình thức");
        header.createCell(5).setCellValue("Ghi chú");

        int rowIndex = 1;
        int stt = 1;

        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(expense.getTitle());

            setMoneyCell(row, 2, expense.getAmount(), moneyStyle);

            row.createCell(3).setCellValue(
                    expense.getExpenseDate() != null
                            ? expense.getExpenseDate().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "-");

            row.createCell(4).setCellValue(
                    expense.getPaymentMethod() != null
                            ? expense.getPaymentMethod()
                            : "-");

            row.createCell(5).setCellValue(
                    expense.getNote() != null ? expense.getNote() : "-");
        }

        for (int i = 0; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        DataFormat format = workbook.createDataFormat();

        style.setDataFormat(
                format.getFormat("#,##0 \"VNĐ\";[Red]-#,##0 \"VNĐ\""));

        return style;
    }

    private void setMoneyCell(
            Row row,
            int cellIndex,
            Long value,
            CellStyle moneyStyle) {
        Cell cell = row.createCell(cellIndex);

        cell.setCellValue(value != null ? value : 0);

        cell.setCellStyle(moneyStyle);
    }

    private void setMoneyCell(
            Row row,
            int cellIndex,
            Integer value,
            CellStyle moneyStyle) {
        Cell cell = row.createCell(cellIndex);

        cell.setCellValue(value != null ? value : 0);

        cell.setCellStyle(moneyStyle);
    }

    private Long safeLong(Long value) {
        return value != null ? value : 0L;
    }
}