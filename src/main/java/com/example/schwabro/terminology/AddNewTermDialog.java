package com.example.schwabro.terminology;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import java.awt.*;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class AddNewTermDialog extends DialogWrapper {
    private JTextField term;
    private JTextField description;
    private JTextField hyperlink;

    public AddNewTermDialog() {
        super(true); // use current window as parent
        init();
        setTitle("Add New Term");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new GridLayout(6, 1));
        dialogPanel.setPreferredSize(new Dimension(400, 300));

        dialogPanel.add(new JLabel("Term:"));
        term = new JTextField();
        term.setPreferredSize(new Dimension(300, 20));
        dialogPanel.add(term);

        dialogPanel.add(new JLabel("Description:"));
        description = new JTextField();
        description.setPreferredSize(new Dimension(300, 100));
        dialogPanel.add(description);

        dialogPanel.add(new JLabel("Info Hyperlink:"));
        hyperlink = new JTextField();
        hyperlink.setPreferredSize(new Dimension(300, 20));
        dialogPanel.add(hyperlink);
        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        if (term.getText().isEmpty() || description.getText().isEmpty() || hyperlink.getText().isEmpty()) {
            Messages.showErrorDialog("All fields must be filled out.", "Validation Error");
        } else {
            super.doOKAction();
        }
    }

    public String getTerm() {
        return term.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public String getHyperlink() {
        return hyperlink.getText();
    }
}
