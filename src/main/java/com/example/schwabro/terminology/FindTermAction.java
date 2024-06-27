package com.example.schwabro.terminology;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.schwabro.terminology.AllTermsMap.ALL_TERMS_MAP;

public class FindTermAction extends AnAction {
    private final Map<String, TermEntity> wordsToTooltips = ALL_TERMS_MAP;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) return;

        Document document = editor.getDocument();
        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.removeAllHighlighters();
        HashMap<Integer, Set<TermEntity>> lineToTerms = new HashMap<>();

        for (Map.Entry<String, TermEntity> entry : wordsToTooltips.entrySet()) {
            String term = entry.getKey();
            TermEntity termEntity = entry.getValue();
            String regex = term.replaceAll("\\s+", "[\\\\s\\\\W_\\\\-]*");
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(document.getText());
            while (matcher.find()) {
                int lineNumber = document.getLineNumber(matcher.start());
                lineToTerms.computeIfAbsent(lineNumber, k -> new HashSet<>()).add(termEntity);
            }

        }
        int currentLine = 0;
        for (int line : lineToTerms.keySet().stream().sorted().collect(Collectors.toList())) {
            if (currentLine == 0) {
                currentLine = line;
                continue;
            }

            if (line - currentLine < 6) {
                lineToTerms.computeIfAbsent(currentLine, k -> new HashSet<>()).addAll(lineToTerms.get(line));
                lineToTerms.remove(line);
            } else {
                currentLine = line;
            }
        }

        for (Map.Entry<Integer, Set<TermEntity>> entry : lineToTerms.entrySet()) {
            int lineNumber = entry.getKey();
            RangeHighlighter highlighter = markupModel.addLineHighlighter(
                    lineNumber,
                    0,
                    null
            );

            MyGutterIconRenderer renderer = new MyGutterIconRenderer(entry.getValue(), lineNumber);
            highlighter.setGutterIconRenderer(renderer);
        }
    }
}