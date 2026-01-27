package com.smis.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightCondition;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class SideNavItemWithHelperText extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	    public SideNavItemWithHelperText(String label,
	                                     String helperText,
	                                     Class<? extends Component> navigationTarget,
	                                     Component icon) {

	        setPadding(false);
	        setSpacing(false);
	        setMargin(false);
	        setAlignItems(Alignment.CENTER);
	        setWidthFull();

	        icon.getElement().getStyle()
	        .set("font-size", "16px")
	        .set("color", "inherit");   // ✅ important

	Span title = new Span(label);
	title.getStyle()
	        .set("font-size", "var(--lumo-font-size-xs)")
	        .set("font-weight", "600")
	        .set("color", "inherit")    // ✅ important (or remove the color line)
	        .set("text-align", "center");
	        // ---- Vertical stack ----
	        VerticalLayout tile = new VerticalLayout(icon, title);
	        tile.setPadding(false);
	        tile.setSpacing(false);
	        tile.setMargin(false);
	        tile.setAlignItems(Alignment.CENTER);

	        // ---- Clickable link ----
	        RouterLink link = new RouterLink();
	        link.setRoute(navigationTarget);
	        link.setHighlightCondition(HighlightConditions.sameLocation());

	        link.add(tile);
	        link.addClassName("drawer-tile-item");
	        
	        link.getStyle()
	                .set("text-decoration", "none")
	                .set("color", "inherit")
	                .set("display", "flex")
	                .set("justify-content", "center");
	        
	        add(link);

	        link.addClassName("drawer-tile-item");
	    }
	}
