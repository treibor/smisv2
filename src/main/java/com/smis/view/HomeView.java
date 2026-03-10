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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
    	FlexLayout cardContainer = new FlexLayout();
    	cardContainer.setWidthFull();
    	cardContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
    	cardContainer.getStyle().set("gap", "16px");

    	VerticalLayout card1 = createCard(1);
    	VerticalLayout card2 = createCard(2);
    	VerticalLayout card3 = createCard(3);
    	VerticalLayout card4 = createCard(4);

    	cardContainer.add(card1, card2, card3, card4);

    	// responsive widths
    	card1.getStyle().set("flex", "1 1 240px");
    	card2.getStyle().set("flex", "1 1 240px");
    	card3.getStyle().set("flex", "1 1 240px");
    	card4.getStyle().set("flex", "1 1 240px");
		return cardContainer;
	}
    private VerticalLayout createCard(int type) {
        // OUTER
        VerticalLayout card = new VerticalLayout();
        card.addClassName("flip-card");
        card.setPadding(false);
        card.setSpacing(false);
        card.setMargin(false);
        card.setWidthFull();

        // INNER
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
                break;
        }

        icon.addClassName("card-icon");

        Span titleLabel = new Span(title);
        titleLabel.addClassName("card-title");

        Span descriptionLabel = new Span(description);
        descriptionLabel.addClassName("card-description");

        front.add(icon, titleLabel, descriptionLabel);

        // Back title + content wrapper
        Div backContent = new Div();
        backContent.addClassName("card-back-content");

        switch (type) {
            case 1: {
                Span backTitle = new Span("Works Breakdown");
                backTitle.addClassName("card-back-title");

                long active = service.getActiveWorksCount();
                long deleted = service.getDeletedWorksCount();
                long old = service.getOldWorksCount();
                long recasted = service.getRecastedWorksCount();

                Span activeBadge = new Span("Active Works: " + active);
                activeBadge.getElement().getThemeList().add("badge success");
                activeBadge.addClassName("card-back-text");

                Span oldBadge = new Span("Old Works: " + old);
                oldBadge.getElement().getThemeList().add("badge");
                oldBadge.addClassName("card-back-text");

                Span deletedBadge = new Span("Deleted: " + deleted);
                deletedBadge.getElement().getThemeList().add("badge error");
                deletedBadge.addClassName("card-back-text");

                Span recastedBadge = new Span("Recasted: " + recasted);
                recastedBadge.getElement().getThemeList().add("badge contrast");
                recastedBadge.addClassName("card-back-text");

                backContent.add(activeBadge, oldBadge, deletedBadge, recastedBadge);
                back.add(backTitle, backContent);
                break;
            }

            case 2: {
                Span backTitle = new Span("Installment Details");
                backTitle.addClassName("card-back-title");

                long installment1 = service.getInstallmentCount(1);
                long installment2 = service.getInstallmentCount(2);
                long installment3 = service.getInstallmentCount(3);

                BigDecimal amount1 = service.getSumOfInstallments(1);
                BigDecimal amount2 = service.getSumOfInstallments(2);
                BigDecimal amount3 = service.getSumOfInstallments(3);
                BigDecimal totalAmount = service.getSumOfInstallments();

                Span inst1 = new Span("Installment 1: " + installment1 + " nos | Rs. " + amount1);
                inst1.getElement().getThemeList().add("badge");
                inst1.addClassName("card-back-text");

                Span inst2 = new Span("Installment 2: " + installment2 + " nos | Rs. " + amount2);
                inst2.getElement().getThemeList().add("badge");
                inst2.addClassName("card-back-text");

                Span inst3 = new Span("Installment 3: " + installment3 + " nos | Rs. " + amount3);
                inst3.getElement().getThemeList().add("badge");
                inst3.addClassName("card-back-text");

                Span total = new Span("Total Amount Released: Rs. " + totalAmount);
                total.getElement().getThemeList().add("badge success");
                total.addClassName("card-back-text");

                backContent.add(inst1, inst2, inst3, total);
                back.add(backTitle, backContent);
                break;
            }

            case 3: {
                Span backTitle = new Span("Scheme Breakdown");
                backTitle.addClassName("card-back-title");

                Map<String, Long> counts = service.getActiveWorksCountByMasterSchemeName();

                if (counts == null || counts.isEmpty()) {
                    Span empty = new Span("No data available");
                    empty.addClassName("card-back-text");
                    backContent.add(empty);
                } else {
                    counts.forEach((schemeNo, cnt) -> {
                        Span s = new Span(schemeNo + ": " + cnt);
                        s.getElement().getThemeList().add("badge");
                        s.addClassName("card-back-text");
                        backContent.add(s);
                    });
                }

                back.add(backTitle, backContent);
                break;
            }

            case 4: {
                Span backTitle = new Span("Previous Month");
                backTitle.addClassName("card-back-title");

                Span backText = new Span("Additional previous month details can go here.");
                backText.addClassName("card-back-text");

                backContent.add(backText);
                back.add(backTitle, backContent);
                break;
            }

            case 5: {
                Span backTitle = new Span("Alerts / Pending");
                backTitle.addClassName("card-back-title");

                Span backText = new Span("Add alert or pending work summary here.");
                backText.addClassName("card-back-text");

                backContent.add(backText);
                back.add(backTitle, backContent);
                break;
            }

            default: {
                Span backTitle = new Span("Details");
                backTitle.addClassName("card-back-title");

                Span backText = new Span("Put extra information here.");
                backText.addClassName("card-back-text");

                backContent.add(backText);
                back.add(backTitle, backContent);
                break;
            }
        }

        inner.add(front, back);
        card.add(inner);

        // Flip on click
        card.getElement().addEventListener("click", e -> {
            if (card.hasClassName("flipped")) {
                card.removeClassName("flipped");
            } else {
                card.addClassName("flipped");
            }
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
