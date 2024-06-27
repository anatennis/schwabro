package com.example.schwabro;

import com.example.schwabro.util.GitUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.ui.ClickListener;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

public class WorkTimerWidget extends JComponent implements CustomStatusBarWidget,
        StatusBarWidget.TextPresentation, ActionListener {

    private final JLabel myLabel = new JLabel("00:00:00", AllIcons.General.Information, SwingConstants.LEADING);
    private final ImageIcon icon = new ImageIcon("src/main/resources/META-INF/pluginIcon.svg");
    private final HashMap<String, Integer> workInBranches = new HashMap<>();
    private int secondsElapsed;
    private final Project project;

    public WorkTimerWidget(@NotNull Project project) {
        secondsElapsed = 0;
        this.project = project;
        Timer timer = new Timer(1000, this);
        timer.start();
    }


    @Override
    public JComponent getComponent() {
        return myLabel;
    }

    @NotNull
    @Override
    public String ID() {
        return "Timer";
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent e, int clickCount) {
                JFrame jFrame = new JFrame();
                jFrame.setSize(400, 200);
                JOptionPane.showMessageDialog(jFrame, createWorkInfoPanel(statusBar.getProject()),
                        "Work Statistics", JOptionPane.INFORMATION_MESSAGE, icon);
                return true;
            }
        }.installOn(getComponent());
    }

    @Override
    public void dispose() {

    }

    @Override
    public @NotNull @NlsContexts.Label String getText() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG);
        return LocalDateTime.now().format(dateTimeFormatter);
    }

    @Override
    public float getAlignment() {
        return 0;
    }

    @Override
    public @Nullable @NlsContexts.Tooltip String getTooltipText() {
        return null;
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    public void resetTimer(String branch) {
        Integer branchTime = workInBranches.get(branch);
        secondsElapsed = branchTime == null ? 0 : branchTime;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentGitBranch = GitUtils.getCurrentGitBranch(project);
        if (!currentGitBranch.equals(GitUtils.NO_GIT_INFO))
            createWorkInfoPanel(project);
        else
            return;
        secondsElapsed++;
        myLabel.setText(getTime(secondsElapsed));
    }

    private JPanel createWorkInfoPanel(Project project) {
        String currentGitBranch = GitUtils.getTicketName(project);
        workInBranches.put(currentGitBranch, secondsElapsed);

        JPanel panel = new JPanel();
        JLabel jLabel = new JLabel("Today you are working on:", AllIcons.General.Mouse, SwingConstants.LEADING);
        panel.add(jLabel);
        panel.add(Box.createHorizontalStrut(50));

        JPanel jPanel = new JPanel();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Branch");
        model.addColumn("Time");
        for (Map.Entry<String, Integer> entry : workInBranches.entrySet())
            model.addRow(new Object[]{entry.getKey(), getTime(entry.getValue())});
        JTable table = new JTable(model);
        jPanel.add(table);
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn branchColumn = columnModel.getColumn(0);
        branchColumn.setPreferredWidth(150);
        branchColumn.setCellRenderer((table1, value, isSelected, hasFocus, row, column)
                -> new JLabel((String) value, AllIcons.Actions.Profile, SwingConstants.LEADING));

        panel.add(jPanel);
        return panel;
    }

    private static String getTime(int seconds) {
        int hours = seconds / 3600;
        int min = seconds % 3600 / 60;
        int sec = seconds % 3600 % 60 % 60;
        return getTimeFormat(hours) + ":" + getTimeFormat(min) + ':' + getTimeFormat(sec);
    }

    private static String getTimeFormat(int time) {
        return time / 10 == 0 ? ("0" + time) : String.valueOf(time);
    }
}
