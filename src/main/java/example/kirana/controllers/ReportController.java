package example.kirana.controllers;

import example.kirana.models.Report;
import example.kirana.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class for handling report-related endpoints in the Kirana application.
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * Constructs a new ReportController with the specified ReportService.
     *
     * @param reportService The service responsible for generating reports.
     */
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates and returns the weekly reports.
     *
     * @return List of weekly reports.
     */
    @GetMapping("/weekly")
    public List<Report> generateWeeklyReports() {
        return reportService.generateWeeklyReports();
    }

    /**
     * Generates and returns the monthly reports.
     *
     * @return List of monthly reports.
     */
    @GetMapping("/monthly")
    public List<Report> generateMonthlyReports() {
        return reportService.generateMonthlyReports();
    }

    /**
     * Generates and returns the yearly reports.
     *
     * @return List of yearly reports.
     */
    @GetMapping("/yearly")
    public List<Report> generateYearlyReports() {
        return reportService.generateYearlyReports();
    }
}
