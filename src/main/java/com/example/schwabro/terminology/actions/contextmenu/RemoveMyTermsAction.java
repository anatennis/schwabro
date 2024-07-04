package com.example.schwabro.terminology.actions.contextmenu;

import com.example.schwabro.terminology.AllTermsMap;
import com.example.schwabro.util.TermsUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class RemoveMyTermsAction extends AnAction {
    Icon ICON = AllIcons.General.Warning;
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (Messages.showOkCancelDialog("Do you want to remove ALL your own terms?", "Remove All My Terms",
                "Yes", "No", ICON) == Messages.OK)
        {
            AllTermsMap.removeMyTerms();
            TermsUtil.removeMyTermsFile();
        }

    }
}
