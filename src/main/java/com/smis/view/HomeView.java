package com.smis.view;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.smis.dbservice.DashboardService;
import com.smis.dbservice.Dbservice;
import com.storedobject.chart.BarChart;
import com.storedobject.chart.CategoryData;
import com.storedobject.chart.Data;
import com.storedobject.chart.DataType;
import com.storedobject.chart.NightingaleRoseChart;
import com.storedobject.chart.Position;
import com.storedobject.chart.RectangularCoordinate;
import com.storedobject.chart.SOChart;
import com.storedobject.chart.Title;
import com.storedobject.chart.Toolbox;
import com.storedobject.chart.XAxis;
import com.storedobject.chart.YAxis;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
@PageTitle("Home")
@Route(value="", layout=MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	DashboardService dservice;
    public HomeView(Dbservice service, DashboardService dservice) {
    	this.service=service;
    	this.dservice=dservice;
    	add(getCards(),getCharts2(), getCharts());
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		getStyle().set("text-align", "center");
		
    }

    private Component getCards() {
		HorizontalLayout cards = new HorizontalLayout();
		cards.setWidthFull();
		
		cards.add(createCard(1), createCard(2), createCard(3), createCard(4), createCard(5));
		return cards;
	}
    private VerticalLayout createCard(int type) {
        // OUTER (size + perspective)
        VerticalLayout card = new VerticalLayout();
        card.addClassName("flip-card");
        card.setPadding(false);
        card.setSpacing(false);

        // INNER (rotates)
        Div inner = new Div();
        inner.addClassName("flip-card-inner");

        // FRONT
        Div front = new Div();
        front.addClassName("flip-card-front");

        // BACK
        Div back = new Div();
        back.addClassName("flip-card-back");

        Icon icon;
        String title;
        String description;

        switch (type) {
            case 1:
                icon = VaadinIcon.CHECK.create();
                title = String.valueOf(service.getTotalWorksCount());
                description = "Total Works Entered";
                break;
            case 2:
                icon = VaadinIcon.ADJUST.create();
                title = String.valueOf(service.getTotalInstallmentCount());
                description = "Total Installments Released";
                break;
            case 3:
                icon = VaadinIcon.CALENDAR.create();
                title = String.valueOf(dservice.getCurrentMonthData());
                description = "Works Entered In The Current Month";
                break;
            case 4:
                icon = VaadinIcon.CALENDAR_CLOCK.create();
                title = String.valueOf(dservice.getMonthData());
                description = "Works Entered In The Previous Month";
                break;
            case 5:
                icon = VaadinIcon.ALARM.create();
                title = String.valueOf(dservice.getMonthData());
                description = "Works Entered In The Previous Month";
                break;
            default:
                icon = VaadinIcon.BARCODE.create();
                title = "-";
                description = "Default Card";
        }

        icon.addClassName("card-icon");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("card-title");

        Span descriptionLabel = new Span(description);
        descriptionLabel.addClassName("card-description");

        // Front
        front.add(icon, titleLabel, descriptionLabel);

        // Back (now using switch)
        switch (type) {
            case 1: {
                Span backTitle = new Span("Works Breakdown");
                backTitle.addClassName("card-back-title");
                long active=service.getActiveWorksCount();
                long deleted = service.getDeletedWorksCount();
                long old = service.getOldWorksCount();
                long recasted = service.getRecastedWorksCount();
                Span activeBadge = new Span("ActiveWorks: " + active);
                activeBadge.getElement().getThemeList().add("badge error");
                activeBadge.addClassName("card-back-text");
                Span oldBadge = new Span("Old Works: " + old);
                oldBadge.getElement().getThemeList().add("badge");
                oldBadge.addClassName("card-back-text");
                Span deletedBadge = new Span("Deleted: " + deleted +" | Recasted: " + recasted);
                deletedBadge.getElement().getThemeList().add("badge error");
                deletedBadge.addClassName("card-back-text");
                back.add(backTitle,activeBadge, oldBadge, deletedBadge);
                break;
            }
            case 2: {
                Span backTitle = new Span("Details");
                backTitle.addClassName("card-back-title");
                long installment1=service.getInstallmentCount(1);
                long installment2=service.getInstallmentCount(2);
                long installment3=service.getInstallmentCount(3);
				BigDecimal amount1 = service.getSumOfInstallments(1);
				BigDecimal amount2 = service.getSumOfInstallments(2);
				BigDecimal amount3 = service.getSumOfInstallments(3);
				BigDecimal amount = service.getSumOfInstallments();
                //long old = service.getOldWorksCount();
                //long recasted = service.getRecastedWorksCount();
                Span activeBadge = new Span("Installment 1: " + installment1 +" nos | Rs. "+amount1);
                activeBadge.getElement().getThemeList().add("badge error");
                activeBadge.addClassName("card-back-text");
                Span oldBadge = new Span("Installment 2: " + installment2 +" nos | Rs. "+amount2);
                oldBadge.getElement().getThemeList().add("badge");
                oldBadge.addClassName("card-back-text");
                Span inst3 = new Span("Installment 3: " + installment3 +" nos | Rs. "+amount3);
                inst3.getElement().getThemeList().add("badge");
                inst3.addClassName("card-back-text");
                
                Span deletedBadge = new Span("Total Amount Released: Rs." + amount);
                deletedBadge.getElement().getThemeList().add("badge error");
                deletedBadge.addClassName("card-back-text");
                back.add(backTitle,activeBadge, oldBadge,inst3, deletedBadge);
                break;
            }
            case 3: {

            	Map<String, Long> counts = service.getActiveWorksCountByMasterSchemeName();

            	counts.forEach((schemeNo, cnt) -> {
            	    Span s = new Span(schemeNo + ": " + cnt);
            	    s.getElement().getThemeList().add("badge");
            	    s.addClassName("card-back-text");
            	    back.add(s);
            	});
            	break;
            }
            default: {
                Span backTitle = new Span("Details");
                backTitle.addClassName("card-back-title");

                Span backText = new Span("Put extra information here (filters, last updated, etc.)");
                backText.addClassName("card-back-text");

                back.add(backTitle, backText);
                break;
            }
        }

        inner.add(front, back);
        card.add(inner);

        // Flip on click
        card.getElement().addEventListener("click", e -> {
            if (card.hasClassName("flipped")) card.removeClassName("flipped");
            else card.addClassName("flipped");
        });

        return card;
    }

   
    
    
    public Component getCharts() {
    	SOChart soChart = new SOChart();
    	SOChart soChart2 = new SOChart();
    	Map<String, Long> counts = service.getActiveWorkCountsByMasterConstituencyName();

    	CategoryData labels = new CategoryData();
    	Data data = new Data();

    	counts.forEach((label, cnt) -> {
    	    labels.add(label);
    	    data.add(cnt);
    	});
        BarChart bc = new BarChart(labels, data);
        RectangularCoordinate rc;
        rc  = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        Position p = new Position();
        //p.setBottom(Size.percentage(55));
        //rc.setPosition(p); // Position it leaving 55% space at the bottom
        bc.plotOn(rc); // Bar chart needs to be plotted on a coordinate system
        bc.setName("Works");
        Toolbox toolbox = new Toolbox();
        toolbox.addButton(new Toolbox.Download(), new Toolbox.Zoom());
        // Let's add some titles.
        Title title = new Title("Constituency Wise Works");
        //title.setSubtext("Please Test");
        // We are going to create a couple of charts. So, each chart should be positioned
        // appropriately.
        // Create a self-positioning chart.
        NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
        nc.setName("Works");
        //Position p = new Position();
        //p.setTop(Size.percentage(50));
        nc.setPosition(p); // Position it leaving 50% space at the top
        //soChart.s
        soChart.add(nc, toolbox);
        soChart2.add(bc, toolbox, title);
        HorizontalLayout getCharts=new HorizontalLayout();
        
        getCharts.addClassName("chartsLayout1");
        getCharts.setWidthFull();
        //getCharts.setHeight("100px");
       soChart2.setWidthFull();
        getCharts.add(soChart2);
        return getCharts;
    }
    
    public Component getCharts2() {
        SOChart soChart = new SOChart();
        SOChart soChart2 = new SOChart();

        // ✅ get the map
        Map<String, Long> counts = Optional.ofNullable(service.getActiveWorkCountsByMasterScheme())
                .orElse(Collections.emptyMap());

        CategoryData labels = new CategoryData();
        Data data = new Data();

        counts.forEach((label, cnt) -> {
            labels.add(label);
            data.add(cnt);
        });

        BarChart bc = new BarChart(labels, data);
        bc.setName("Works");

        RectangularCoordinate rc =
                new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        bc.plotOn(rc);

        Toolbox toolbox = new Toolbox();
        toolbox.addButton(new Toolbox.Download(), new Toolbox.Zoom());

        Title title = new Title("Scheme Wise Works");

        NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
        nc.setName("Works");

        Position p = new Position();
        nc.setPosition(p);

        soChart.add(nc, toolbox);
        soChart2.add(bc, toolbox, title);

        HorizontalLayout getCharts = new HorizontalLayout();
        // ✅ add both charts
        getCharts.add(soChart2, soChart);

        getCharts.addClassName("chartsLayout1");
        getCharts.setWidthFull();
        getCharts.setJustifyContentMode(JustifyContentMode.CENTER);

        // If you intended to center this layout, set style on the layout, not 'this'
        getCharts.getStyle().set("text-align", "center");

        return getCharts;
    }

}
