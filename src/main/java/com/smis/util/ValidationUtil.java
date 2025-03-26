package com.smis.util;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class ValidationUtil {
	// private static final String ALLOWED_PATTERN = "[0-9A-Za-z.()@/'&]";
	public static void applyValidation(TextField textField) {
        textField.setAllowedCharPattern("[0-9A-Za-z.()@/'&\\s-]");
        textField.setMinLength(0);
        textField.setMaxLength(50);
    }
    public static void applyTextOnly(TextField textField) {
        textField.setAllowedCharPattern("[0-9A-Za-z ]");
        textField.setMinLength(0);
        textField.setMaxLength(50);
    }
    public static void applyTextAreaValidation(TextArea textArea) {
    	//textArea.setAllowedCharPattern("[0-9A-Za-z.,()@/-'& ]");
    	textArea.setAllowedCharPattern("[0-9A-Za-z.()@/'&\\s-]");
    	textArea.setMinLength(0);
    	textArea.setMaxLength(50);
    }
    public static boolean applyValidation(String string) {
    	//textArea.setAllowedCharPattern("[0-9A-Za-z.,()@/-'& ]");
    	if (string.matches("[0-9A-Za-z.()@/'&\\s-]*")){
    		return true;
    	}
    	return false;    	
    }

}