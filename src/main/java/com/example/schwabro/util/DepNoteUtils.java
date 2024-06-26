package com.example.schwabro.util;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DepNoteUtils {
    public static String addInfo(Set<DocumentImpl> files, Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        StringBuilder info = new StringBuilder();

        for (DocumentImpl doc : files) {
            VirtualFile file = FileDocumentManager.getInstance().getFile(doc);
            Change change = changeListManager.getChange(file);
            if (change == null) {
                String changes = doc.getImmutableCharSequence().toString().replace('\n', ',');
                return info.append("    New properties: ").append(changes).toString();
            }
            ContentRevision beforeRevision = change.getBeforeRevision();
            ContentRevision afterRevision = change.getAfterRevision();
            if (beforeRevision != null && afterRevision != null) {
                try {
                    String beforeContent = beforeRevision.getContent();
                    String afterContent = afterRevision.getContent();
                    if (beforeContent != null && afterContent != null) {
                        calculateDifferences(beforeContent, afterContent, change, info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return info.toString();
    }

    private static void calculateDifferences(String beforeContent, String afterContent, Change change, StringBuilder info)
            throws DiffException {
        List<String> beforeLines = Arrays.asList(beforeContent.split("\n"));
        List<String> afterLines = Arrays.asList(afterContent.split("\n"));
        System.out.println("File: " + change.getVirtualFile().getPath());
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .build();
        List<DiffRow> rows = generator.generateDiffRows(beforeLines, afterLines);
        for (DiffRow row : rows) {
            if (!row.getTag().equals(DiffRow.Tag.EQUAL)) {
                if (row.getTag() == DiffRow.Tag.INSERT || row.getTag() == DiffRow.Tag.CHANGE && row.getOldLine().isEmpty())
                    info.append("    New property was added: ")
                            .append(removeSpanTags(row.getNewLine()))
                            .append('\n');
                else if (row.getTag() == DiffRow.Tag.CHANGE && !row.getNewLine().isEmpty())
                    info.append("    This property ")
                            .append(removeSpanTags(row.getOldLine()))
                            .append(" was changed to ")
                            .append(removeSpanTags(row.getNewLine()))
                            .append('\n');
                else
                    info.append("    This property was removed: ")
                            .append(removeSpanTags(row.getOldLine()))
                            .append('\n');
            }
        }
    }

    public static String removeSpanTags(String text) {
        String spanTagPattern = "</?span[^>]*>";
        return text.replaceAll(spanTagPattern, "");
    }

}
