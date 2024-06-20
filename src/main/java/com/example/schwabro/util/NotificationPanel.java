package com.example.schwabro.util;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel implements ToolWindowContent {
    protected final JEditorPane myLabel;

    NotificationPanel() {
        super(new BorderLayout());

        myLabel = new JEditorPane(UIUtil.HTML_MIME, "gagaga");
        myLabel.setEditable(false);
        myLabel.setFont(UIUtil.getToolTipFont());
        myLabel.addHyperlinkListener(BrowserHyperlinkListener.INSTANCE);

        setBorder(JBUI.Borders.empty(1, 15));

        add(myLabel, BorderLayout.CENTER);
        myLabel.setBackground(getBackground());
    }

    public void setText(@Nls String text) {
        myLabel.setText(text);
    }

    public JEditorPane getLabel() {
        return myLabel;
    }

    @Override
    public Color getBackground() {
        Color color = EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.NOTIFICATION_BACKGROUND);
        return color == null ? new JBColor(new Color(0xffffcc), new Color(0xf0f0)) : color;
    }
}