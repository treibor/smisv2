package com.smis.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.Constituency;

import com.smis.entity.Installment;
import com.smis.entity.Scheme;
import com.smis.entity.Work;
import com.smis.entity.Year;
import com.smis.util.NotificationUtil;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.PermitAll;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@PageTitle("Reports")
@Route(value = "printing", layout = MainLayout.class)
@PermitAll
public class ReportView extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ComboBox<Block> block = new ComboBox<Block>("Block/MB");
	ComboBox<Scheme> scheme = new ComboBox<Scheme>("Scheme");
	ComboBox<Year> year = new ComboBox<Year>("Financial Year");
	ComboBox<Constituency> consti = new ComboBox<Constituency>("Constituency");
	ComboBox<String> reportTypemla = new ComboBox<String>("Select Report Type");
	ComboBox<String> reportTypemp = new ComboBox<String>("Select Report Type");
	ComboBox<Year> yearmp = new ComboBox<Year>("Year");
	
	//ComboBox<Impldistrict> implDistrict = new ComboBox<Impldistrict>("Implementing District");
	DatePicker fromDate = new DatePicker("Print By Dates");
	DatePicker toDate = new DatePicker();
	Notification notify = new Notification();
	private Dbservice service;
	
	HorizontalLayout hl4 = new HorizontalLayout();
	DatePicker fromDatep = new DatePicker("Print By Dates");
	DatePicker toDatep = new DatePicker();
	StreamResource resourcerange;
	PdfViewer pdfViewerrange;
	String user;
	
	public ReportView(Dbservice service) {
		
		this.service = service;
		initializeMlaItems();
		
		reportTypemla.setItems("General Report", "Detailed Report");
		reportTypemp.setItems("General Report", "Detailed Report");
		// candi.addValueChangeListener(e-> removePdfViewer());
		
		add(createMlaPanel(), hl4);
	}

	

	public void initializeMlaItems() {
		block.setItems(service.getBlocksByUser());
		scheme.setItems(service.getSchemesByUser());
		consti.setItems(service.getConstituenciesByUser());
		year.setItems(service.getAllYears());
		block.setItemLabelGenerator(Block::getBlockLabel);
		scheme.setItemLabelGenerator(Scheme::getSchemeLabel);
		year.setItemLabelGenerator(Year::getYearLabel);
		consti.setItemLabelGenerator(consti-> consti.getConstituencyLabel()+" - "+consti.getConstituencyMLA());
		block.addValueChangeListener(e -> removePdfViewer());
		scheme.addValueChangeListener(e -> removePdfViewer());
		year.addValueChangeListener(e -> removePdfViewer());
		consti.addValueChangeListener(e -> removePdfViewer());
		block.setClearButtonVisible(true);
		scheme.setClearButtonVisible(true);
		block.setClearButtonVisible(true);
		consti.setClearButtonVisible(true);
	}

	private void removePdfViewer() {

		if (hl4 != null) {
			hl4.removeAll();
		}

	}

	

	public Component createMlaPanel() {
		FormLayout fl1 = new FormLayout();
		Button printMla = new Button("Print");
		printMla.addClickListener(e -> printReport());
		fl1.add(reportTypemla, 2);
		fl1.add(consti, 2);
		fl1.add(block, 2);
		fl1.add(scheme, 2);
		fl1.add(year, 2);
		fl1.add(printMla, 2);
		fl1.setSizeFull();
		fl1.setResponsiveSteps(new ResponsiveStep("0", 12),
				// Use two columns, if layout's width exceeds 500px
				new ResponsiveStep("500px", 12));
		// Details details=new Details("Election Personnel", fl1);
		// details.setOpened(false);
		// return details;
		return fl1;
	}

	

	
	private void printReport() {

		if (reportTypemla.getValue() == null || reportTypemla.getValue() == "") {
			//notify.show("Please Select The Type of Report", 5000, Position.TOP_CENTER);
			NotificationUtil.showError("Please Select The Type of Report");
		} else {
			removePdfViewer();
			try {
				//String reportPath = "D:";
				URL res = getClass().getClassLoader().getResource("report/Detailsmp.jrxml");
				File file = Paths.get(res.toURI()).toFile();
				String absolutePath = file.getAbsolutePath();
				String reportPath = absolutePath.substring(0, absolutePath.length() - 15);
				//System.out.println("A");
				if (reportTypemla.getValue() == "Detailed Report") {
					//System.out.println("Z");
					List<Installment> installment = service.getInstallmentForReport(scheme.getValue(), year.getValue(),
							consti.getValue(), block.getValue());
					Resource resource = new ClassPathResource("report/Detailsmla.jrxml");
					InputStream employeeReportStream = resource.getInputStream();
					JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
					JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(installment);
					Map<String, Object> parameters = new HashMap<>();
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
							jrBeanCollectionDataSource);
					//System.out.println("B");
					JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath + "//detailreport.pdf");
					File a = new File(reportPath + "//detailreport.pdf");
					StreamResource resourcerange = new StreamResource("DetailedReport.pdf", () -> createResource(a));
					PdfViewer pdfViewerrange = new PdfViewer();
					pdfViewerrange.setSrc(resourcerange);
					hl4.setVisible(true);
					hl4.setSizeFull();
					hl4.add(pdfViewerrange);
				} else if (reportTypemla.getValue() == "General Report") {
					List<Work> works = service.getReportWorks(scheme.getValue(), consti.getValue(), block.getValue(),
							year.getValue());
					//System.out.println("Works Total:"+works.size());
					Resource resource = new ClassPathResource("report/Generalmla.jrxml");
					InputStream employeeReportStream = resource.getInputStream();
					JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
					JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(works);
					Map<String, Object> parameters = new HashMap<>();
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
							jrBeanCollectionDataSource);
					JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath + "//generalreport.pdf");
					File a = new File(reportPath + "//generalreport.pdf");
					StreamResource resourcerange = new StreamResource("GeneralReport.pdf", () -> createResource(a));
					PdfViewer pdfViewerrange = new PdfViewer();
					pdfViewerrange.setSrc(resourcerange);
					hl4.setVisible(true);
					hl4.setSizeFull();
					hl4.add(pdfViewerrange);
				}

				// removePdfViewer();

			} catch (Exception e) {
				NotificationUtil.showError("Please Select The Type of Report. Error: "+e);
				
			}
		}
	}

	private InputStream createResource(File path) {// get generated pdf file and create Resource
		try {
			return FileUtils.openInputStream(path);
		} catch (Exception ex) {
		}
		return null;
	}

}