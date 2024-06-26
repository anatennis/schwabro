package com.example.schwabro.terminology.actions.contextmenu;

import com.example.schwabro.terminology.AllTermsMap;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.jetbrains.annotations.NotNull;

public class ShowAllTermsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MyHtmlDialog dialog = new MyHtmlDialog();
        dialog.show();
    }

    private static class MyHtmlDialog extends DialogWrapper {
        protected MyHtmlDialog() {
            super(true);
            init();
            setTitle("All Terms");
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            String htmlContent = AllTermsMap.toHTML();

            JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
            editorPane.setEditable(false);
            editorPane.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            JScrollPane scrollPane = new JBScrollPane(editorPane);
            editorPane.setCaretPosition(0);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            panel.add(scrollPane, BorderLayout.CENTER);
            return panel;
        }
    }
}