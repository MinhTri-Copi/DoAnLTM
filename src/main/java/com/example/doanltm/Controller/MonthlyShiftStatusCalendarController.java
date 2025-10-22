package com.example.doanltm.Controller;

import com.example.doanltm.DAO.CaLamDAO;
import com.example.doanltm.DAO.DangKyDAO;
import com.example.doanltm.Model.CaLam;
import com.example.doanltm.Model.DangKy;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class MonthlyShiftStatusCalendarController {
    
    @FXML private DatePicker monthPicker;
    @FXML private Label monthYearLabel;
    @FXML private HBox headerRow;
    @FXML private HBox row1; // Sáng
    @FXML private HBox row2; // Trưa
    @FXML private HBox row3; // Chiều
    @FXML private HBox row4; // Tối
    @FXML private VBox calendarContainer;
    
    private final CaLamDAO caLamDAO = new CaLamDAO();
    private final DangKyDAO dangKyDAO = new DangKyDAO();
    
    private LocalDate selectedMonth = LocalDate.now();
    private Map<Integer, Map<String, ShiftDayStatus>> calendarData = new HashMap<>();
    
    // Mapping of shift names to rows
    private final Map<String, HBox> shiftRowMap = new HashMap<>();
    
    @FXML
    public void initialize() {
        shiftRowMap.put("Sáng", row1);
        shiftRowMap.put("Trưa", row2);
        shiftRowMap.put("Chiều", row3);
        shiftRowMap.put("Tối", row4);
        
        // Set month picker to current month
        monthPicker.setValue(LocalDate.now());
        monthPicker.setOnAction(e -> handleMonthChange());
        
        // Load calendar for current month
        loadCalendar(LocalDate.now());
    }
    
    @FXML
    private void handleRefresh() {
        if (monthPicker.getValue() != null) {
            selectedMonth = monthPicker.getValue();
        }
        loadCalendar(selectedMonth);
    }
    
    private void handleMonthChange() {
        if (monthPicker.getValue() != null) {
            selectedMonth = monthPicker.getValue();
            loadCalendar(selectedMonth);
        }
    }
    
    private void loadCalendar(LocalDate monthDate) {
        selectedMonth = monthDate;
        YearMonth yearMonth = YearMonth.from(monthDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();
        
        // Update month label
        String monthName = monthDate.getMonth().toString();
        monthYearLabel.setText("Tháng " + month + ", " + year);
        
        // Clear previous data
        clearCalendar();
        
        // Get all shifts
        List<CaLam> shifts = caLamDAO.getAllCaLam();
        if (shifts.isEmpty()) {
            return;
        }
        
        // Get all registrations for this month
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        List<DangKy> registrations = dangKyDAO.getScheduleWithFilter("", firstDay, lastDay, 1000, 0);
        
        // Build calendar data structure
        calendarData.clear();
        
        // Initialize calendar data
        for (int day = 1; day <= daysInMonth; day++) {
            calendarData.put(day, new HashMap<>());
            for (CaLam shift : shifts) {
                ShiftDayStatus status = new ShiftDayStatus(shift.getMoTa(), 0, shift.getSoLuongToiDa());
                calendarData.get(day).put(shift.getMoTa(), status);
            }
        }
        
        // Count registrations
        for (DangKy reg : registrations) {
            int day = reg.getNgayLam().getDayOfMonth();
            String shiftName = reg.getLoaiCa();
            
            if (calendarData.containsKey(day) && calendarData.get(day).containsKey(shiftName)) {
                calendarData.get(day).get(shiftName).registered++;
            }
        }
        
        // Generate header with day numbers
        generateHeader(daysInMonth);
        
        // Generate calendar rows for each shift
        for (CaLam shift : shifts) {
            HBox shiftRow = shiftRowMap.get(shift.getMoTa());
            if (shiftRow != null) {
                generateShiftRow(shiftRow, shift.getMoTa(), daysInMonth);
            }
        }
    }
    
    private void generateHeader(int daysInMonth) {
        // Add day number header cells
        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setMinWidth(60);
            dayLabel.setMinHeight(40);
            dayLabel.setStyle("-fx-border-color: #111827; -fx-border-width: 0 1 0 0; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #e5e7eb;");
            dayLabel.setAlignment(Pos.CENTER);
            headerRow.getChildren().add(dayLabel);
        }
    }
    
    private void generateShiftRow(HBox row, String shiftName, int daysInMonth) {
        // Clear existing day cells (keep the label)
        while (row.getChildren().size() > 1) {
            row.getChildren().remove(1);
        }
        
        // Add cells for each day
        for (int day = 1; day <= daysInMonth; day++) {
            Region dayCell = createDayCell(day, shiftName);
            row.getChildren().add(dayCell);
        }
    }
    
    private Region createDayCell(int day, String shiftName) {
        Region cell = new Region();
        cell.setMinWidth(60);
        cell.setMinHeight(40);
        
        // Get status for this day and shift
        ShiftDayStatus status = calendarData.get(day).get(shiftName);
        
        // Determine color based on capacity
        String bgColor;
        String statusText;
        
        // Default to green (has space) - show all empty slots as "available"
        if (status.registered >= status.capacity) {
            // Full - red
            bgColor = "#ef4444";
            statusText = "Đầy";
        } else {
            // Has space - green (even if no registrations yet)
            bgColor = "#10b981";
            statusText = "Còn chỗ";
        }
        
        // Add borders and background color
        // All cells have full borders to create a grid effect
        cell.setStyle("-fx-border-color: #333333; -fx-border-width: 1; -fx-background-color: " + bgColor + ";");
        
        // Add tooltip showing details
        Tooltip tooltip = new Tooltip(day + "/" + selectedMonth.getMonthValue() + "\n" +
                shiftName + "\n" +
                statusText + "\n" +
                "Đăng ký: " + status.registered + "/" + status.capacity);
        Tooltip.install(cell, tooltip);
        
        return cell;
    }
    
    private void clearCalendar() {
        // Clear header (keep the label)
        while (headerRow.getChildren().size() > 1) {
            headerRow.getChildren().remove(1);
        }
        
        // Clear rows (keep the labels)
        for (HBox row : shiftRowMap.values()) {
            while (row.getChildren().size() > 1) {
                row.getChildren().remove(1);
            }
        }
    }
    
    /**
     * Inner class to track shift status for a specific day
     */
    private static class ShiftDayStatus {
        String shiftName;
        int registered;
        int capacity;
        
        ShiftDayStatus(String shiftName, int registered, int capacity) {
            this.shiftName = shiftName;
            this.registered = registered;
            this.capacity = capacity;
        }
    }
}
