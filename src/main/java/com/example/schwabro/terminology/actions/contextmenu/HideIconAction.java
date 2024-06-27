package com.example.schwabro.terminology.actions.contextmenu;

import com.example.schwabro.terminology.MyGutterIconRenderer;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class HideIconAction extends AnAction {
    private static int lastClickedLine = -1;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        if (lastClickedLine == -1) {
            InputEvent inputEvent = e.getInputEvent();
            if (!(inputEvent instanceof MouseEvent)) {
                return;
            }

            MouseEvent mouseEvent = (MouseEvent) inputEvent;
            Point lastClickedPoint = mouseEvent.getPoint();
            LogicalPosition logicalPosition = editor.xyToLogicalPosition(lastClickedPoint);
            lastClickedLine = logicalPosition.line;
        }

        RangeHighlighter[] highlighters = editor.getMarkupModel().getAllHighlighters();
        for (RangeHighlighter highlighter : highlighters) {
            GutterMark gutterIconRenderer = highlighter.getGutterIconRenderer();
            if (gutterIconRenderer instanceof MyGutterIconRenderer) {
                MyGutterIconRenderer myRenderer = (MyGutterIconRenderer) gutterIconRenderer;
                if (myRenderer.getLineNumber() == lastClickedLine) {
                    highlighter.dispose();
                    break;
                }
            }
        }
    }

    public static void setLastClickedLine(int lastClickedLine) {
        HideIconAction.lastClickedLine = lastClickedLine;
    }
}