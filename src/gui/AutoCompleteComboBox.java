package gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Combobox that acts as an auto-complete text field.
 * @author Matthew Corfiatis
 */
public class AutoCompleteComboBox extends JComboBox<String> {
    public AutoCompleteComboBox() {
        setEditor(new BasicComboBoxEditor());
        setEditable(true);
    }
}
