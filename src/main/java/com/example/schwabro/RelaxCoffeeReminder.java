package com.example.schwabro;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RelaxCoffeeReminder implements ApplicationComponent {

    public RelaxCoffeeReminder() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(() -> {
            @NotNull Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            addPopUp(openProjects[0]);
        }, 1, TimeUnit.MINUTES); //todo change
    }

    private void addPopUp(Project project){
        JFrame frame = WindowManager.getInstance().getFrame(project);
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("You have been working for two hours! Time to relax and go for a cup of coffee :)",
                        MessageType.INFO, null)
                .setFadeoutTime(10000)
                .createBalloon()
                .show(RelativePoint.getCenterOf(frame.getLayeredPane()), Balloon.Position.atRight);
    }
}
