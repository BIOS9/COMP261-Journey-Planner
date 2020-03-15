package gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class AutoCompleteComboBox extends JComboBox<String> {
    public AutoCompleteComboBox() {
        setEditor(new BasicComboBoxEditor());
        setEditable(true);
    }
}
