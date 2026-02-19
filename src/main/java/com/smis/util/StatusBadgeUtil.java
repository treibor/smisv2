package com.smis.util;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

public final class StatusBadgeUtil {


    private StatusBadgeUtil() {}
    /**
     * Status rule (priority):
     * 1) Deleted
     * 2) Recasted
     * 3) Active
     */
   
    public static Component workStatusBadge(Boolean isDeleted, Boolean isRecasted) {
        if (Boolean.TRUE.equals(isDeleted) && Boolean.TRUE.equals(isRecasted)) {
            return badge("Deleted + Recasted",
                    "var(--lumo-error-text-color)",
                    "var(--lumo-error-color-10pct)");
        }
        if (Boolean.TRUE.equals(isDeleted)) {
            return badge("Deleted",
                    "var(--lumo-error-text-color)",
                    "var(--lumo-error-color-10pct)");
        }
        if (Boolean.TRUE.equals(isRecasted)) {
            return badge("Recasted",
                    "var(--lumo-primary-text-color)",
                    "var(--lumo-primary-color-10pct)");
        }
        return badge("Active",
                "var(--lumo-success-text-color)",
                "var(--lumo-success-color-10pct)");
    }

    /**
     * Generic Yes / No badge
     */
    public static Component yesNo(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return badge("Yes",
                    "var(--lumo-success-text-color)",
                    "var(--lumo-success-color-10pct)");
        }
        return badge("No",
                "var(--lumo-error-text-color)",
                "var(--lumo-error-color-10pct)");
    }

    /**
     * Active / Inactive badge
     */
    public static Component activeInactive(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return badge("Active",
                    "var(--lumo-success-text-color)",
                    "var(--lumo-success-color-10pct)");
        }
        return badge("Inactive",
                "var(--lumo-error-text-color)",
                "var(--lumo-error-color-10pct)");
    }

    /**
     * Enabled / Disabled badge
     */
    public static Component enabledDisabled(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return badge("Enabled",
                    "var(--lumo-success-text-color)",
                    "var(--lumo-success-color-10pct)");
        }
        return badge("Disabled",
                "var(--lumo-error-text-color)",
                "var(--lumo-error-color-10pct)");
    }

    private static Component badge(String text, String color, String bg) {
        Span s = new Span(text);
        s.getStyle()
                .set("display", "inline-block")
                .set("padding", "0.15rem 0.55rem")
                .set("border-radius", "999px")   // pill style
                .set("font-weight", "600")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("line-height", "1.2")
                .set("white-space", "nowrap")
                .set("color", color)
                .set("background", bg);
        return s;
    }
}