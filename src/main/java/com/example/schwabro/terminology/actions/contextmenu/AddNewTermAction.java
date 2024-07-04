package com.example.schwabro.terminology.actions.contextmenu;

import com.example.schwabro.terminology.AddNewTermDialog;
import com.example.schwabro.terminology.AllTermsMap;
import com.example.schwabro.terminology.TermEntity;
import com.example.schwabro.util.TermsUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class AddNewTermAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AddNewTermDialog dialog = new AddNewTermDialog();
        if (dialog.showAndGet()) {
            String term = dialog.getTerm();
            String description = dialog.getDescription();
            String hyperlink = dialog.getHyperlink();
            TermEntity termEntity = new TermEntity(term, description, hyperlink);
            termEntity.setMyTermTrue();

            AllTermsMap.addNewTerm(termEntity);
            TermsUtil.writeNewTerm(termEntity);
        }
    }
}
