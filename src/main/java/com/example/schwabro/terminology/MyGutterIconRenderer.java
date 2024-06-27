package com.example.schwabro.terminology;

import com.example.schwabro.terminology.actions.contextmenu.HideIconAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyGutterIconRenderer extends GutterIconRenderer {
    Icon ICON = AllIcons.General.Information;

    private final Set<TermEntity> terms;
    private final int lineNumber;

    public MyGutterIconRenderer(Set<TermEntity> terms, int line) {
        this.terms = terms;
        this.lineNumber = line;
    }

    @Override
    public @NotNull Icon getIcon() {
        return ICON;
    }

    @Override
    public ActionGroup getPopupMenuActions() {
        HideIconAction.setLastClickedLine(lineNumber);
        return (ActionGroup) ActionManager.getInstance().getAction("TerminologyContextMenu");
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return ICON.hashCode();
    }

    @Override
    @Nullable
    public String getTooltipText() {
        String tooltip = String.valueOf(terms
                .stream()
                .map(TermEntity::toHtml)
                .collect(Collectors.toList()))
                .replace("[", "")
                .replace("]","")
                .replace("<br>,","<tr><br></tr>");

        return "<html><table>" + tooltip + "</table></html>";
    }
}