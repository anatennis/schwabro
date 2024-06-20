package com.example.schwabro;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileSystemTree;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.UIBundle;
import org.jetbrains.annotations.NotNull;

public class FindThisConfluence extends AnAction {
    private static final String CONFLUENCE_LINK = "https://confluence.associatesys.local/dosearchsite.action?cql=siteSearch+~+%22";
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();

        final FileType fileType = HtmlFileType.INSTANCE;
        final String initialContent = "content";
      //  final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(editor.getProject()).getContentRoots();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(editor.getProject(), fileChooserDescriptor);
        fileSystemTree.createNewFile(contentRoots[0], "DN_example.yaml", PlainTextFileType.INSTANCE, initialContent);

//        if (fileType != null && initialContent != null) {
//            createNewFile(fileSystemTree, fileType, initialContent);
//        }

        String query = selectedText.replace(' ', '+').concat("%22");
        BrowserUtil.browse(CONFLUENCE_LINK + query);
    }

    @Override
    public void update(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
    }

    private static void createNewFile(FileSystemTree fileSystemTree, final FileType fileType, final String initialContent) {
        final VirtualFile file = fileSystemTree.getNewFileParent();
        if (file == null || !file.isDirectory()) return;

        String newFileName;
        while (true) {
            newFileName = Messages.showInputDialog(UIBundle.message("create.new.file.enter.new.file.name.prompt.text"),
                    UIBundle.message("new.file.dialog.title"), Messages.getQuestionIcon());
            if (newFileName == null) {
                return;
            }
            if ("".equals(newFileName.trim())) {
                Messages.showMessageDialog(UIBundle.message("create.new.file.file.name.cannot.be.empty.error.message"),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
                continue;
            }
            Exception failReason = ((FileSystemTreeImpl)fileSystemTree).createNewFile(file, newFileName, fileType, initialContent);
            if (failReason != null) {
                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
                continue;
            }
            return;
        }
    }
}
