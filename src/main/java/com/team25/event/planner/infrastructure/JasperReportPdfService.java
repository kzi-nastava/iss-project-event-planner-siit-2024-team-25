package com.team25.event.planner.infrastructure;

import com.team25.event.planner.common.dto.ReviewStatsResponseDTO;
import com.team25.event.planner.common.exception.ReportGenerationFailedException;
import com.team25.event.planner.event.dto.ActivityResponseDTO;
import com.team25.event.planner.event.dto.EventResponseDTO;
import com.team25.event.planner.event.service.EventReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class JasperReportPdfService implements EventReportService {
    private static final Logger logger = LoggerFactory.getLogger(JasperReportPdfService.class);

    @Override
    public Resource generateEventDetailsReport(EventResponseDTO event, List<ActivityResponseDTO> agenda) throws ReportGenerationFailedException {
        try {
            InputStream reportInputStream = new ClassPathResource("jasper-reports/event-details.jrxml").getInputStream();
            JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);

            JasperReport report = JasperCompileManager.compileReport(jasperDesign);

            JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(new EventResponseDTO[]{event});

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("LOGO", new ClassPathResource("email-resources/logo.png").getInputStream());
            parameters.put("ACTIVITIES_DATA", new JRBeanCollectionDataSource(agenda));

            JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(print);

            return new ByteArrayResource(pdfBytes);

        } catch (JRException e) {
            logger.error("Failed to generate Jasper Report for details of event {}", event.getId(), e);
            throw new ReportGenerationFailedException("Failed to generate Jasper Report", e);
        } catch (IOException e) {
            logger.error("Failed to read Jasper Report template for event details.", e);
            throw new ReportGenerationFailedException("Failed to read Jasper Report template", e);
        }
    }

    @Override
    public Resource generateEventStatsReport(EventResponseDTO event, ReviewStatsResponseDTO reviewStats, int numAttendees)
            throws ReportGenerationFailedException {
        try {
            InputStream reportInputStream = new ClassPathResource("jasper-reports/event-stats.jrxml").getInputStream();
            JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
            JasperReport report = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("LOGO", new ClassPathResource("email-resources/logo.png").getInputStream());

            Map<String, Object> reportData = new HashMap<>();
            reportData.put("eventName", event.getName());
            reportData.put("reviewCount", reviewStats.getReviewCount());
            reportData.put("averageRating", reviewStats.getAverageRating());
            reportData.put("numAttendees", numAttendees);
            reportData.put("reviewCounts", new TreeMap<>(reviewStats.getReviewCounts()));

            JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(new Map[]{reportData});

            JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(print);

            return new ByteArrayResource(pdfBytes);
        } catch (JRException e) {
            logger.error("Failed to generate Jasper Report for stats of event {}", event.getId(), e);
            throw new ReportGenerationFailedException("Failed to generate Jasper Report", e);
        } catch (IOException e) {
            logger.error("Failed to read Jasper Report template for event stats.", e);
            throw new ReportGenerationFailedException("Failed to read Jasper Report template", e);
        }
    }
}
