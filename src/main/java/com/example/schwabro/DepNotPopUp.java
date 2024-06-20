package com.example.schwabro;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;

public class DepNotPopUp {

    private JComponent component;

//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
//        Document document = editor.getDocument();
//        component = editor.getContentComponent();
//        document.addDocumentListener(new DocumentListener() {
//            @Override
//            public void documentChanged(@NotNull DocumentEvent event) {
//                DocumentListener.super.documentChanged(event);
//                Document document = event.getDocument();
//                if (document.getModificationStamp() != 0 ) {
//                    addPopUp();
//                }
//            }
//
//        });
//    }

//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
//        Document document = editor.getDocument();
//        component = editor.getContentComponent();
//        document.addDocumentListener(new DocumentListener() {
//            @Override
//            public void documentChanged(@NotNull DocumentEvent event) {
//                DocumentListener.super.documentChanged(event);
//                Document document = event.getDocument();
//                if (document.getModificationStamp() != 0 ) {
//                    addPopUp();
//                }
//            }
//        });
//    }

    private void addPopUp(){
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("You have been working for two hours! Recommend to have a break ", MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(component), Balloon.Position.atRight);
    }

//    private static void createNewFile(FileSystemTree fileSystemTree, final FileType fileType, final String initialContent) {
//        final VirtualFile file = fileSystemTree.getNewFileParent();
//        if (file == null || !file.isDirectory()) return;
//
//        String newFileName;
//        while (true) {
//            newFileName = Messages.showInputDialog(UIBundle.message("create.new.file.enter.new.file.name.prompt.text"),
//                    UIBundle.message("new.file.dialog.title"), Messages.getQuestionIcon());
//            if (newFileName == null) {
//                return;
//            }
//            if ("".equals(newFileName.trim())) {
//                Messages.showMessageDialog(UIBundle.message("create.new.file.file.name.cannot.be.empty.error.message"),
//                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
//                continue;
//            }
//            Exception failReason = ((FileSystemTreeImpl)fileSystemTree).createNewFile(file, newFileName, fileType, initialContent);
//            if (failReason != null) {
//                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
//                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
//                continue;
//            }
//            return;
//        }
//    }

}
