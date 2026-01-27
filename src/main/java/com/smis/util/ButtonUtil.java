package com.smis.util;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ButtonUtil {

    private ButtonUtil() {
        // utility class
    }

    // ---------- COMMON BASE ----------
    private static void applyBase(Button button, String text, VaadinIcon icon) {
        button.setText(text);

        if (icon != null) {
            Icon ic = icon.create();
            ic.getStyle().set("margin-right", "var(--lumo-space-xs)");
            button.setIcon(ic);
        }

        button.addClassName("app-btn");
    }

    // ---------- BUTTON STYLES ----------

    public static void applySaveStyle(Button button) {
        applyBase(button, "Save", VaadinIcon.CHECK);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public static void applyDeleteStyle(Button button) {
        applyBase(button, "Delete", VaadinIcon.TRASH);
        button.addThemeVariants(
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY
        );
    }

    public static void applyNewStyle(Button button) {
        applyBase(button, "New", VaadinIcon.PLUS);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public static void applyCancelStyle(Button button) {
        applyBase(button, "Cancel", VaadinIcon.CLOSE);
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
    }
    public static void applyCloseStyle(Button button) {
        applyBase(button, "Close", VaadinIcon.CLOSE);
        button.addThemeVariants(ButtonVariant.LUMO_ICON);
    }
}