package com.example.schwabro.terminology.actions.contextmenu;

import com.example.schwabro.terminology.MyGutterIconRenderer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import org.jetbrains.annotations.NotNull;

public class HideAllIconsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        RangeHighlighter[] highlighters = editor.getMarkupModel().getAllHighlighters();
        for (RangeHighlighter highlighter : highlighters) {
            if (highlighter.getGutterIconRenderer() instanceof MyGutterIconRenderer) {
                highlighter.dispose();
            }
        }
    }
}