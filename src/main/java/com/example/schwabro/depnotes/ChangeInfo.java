package com.example.schwabro.depnotes;

import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Objects;

public class ChangeInfo {
    private final VirtualFile file;
    private final String fileName;
    private final Change change;
    private final String content;
    private boolean firstTimeChanged;

    public ChangeInfo(VirtualFile file, String fileName, Change change, String content) {
        this.file = file;
        this.fileName = fileName;
        this.change = change;
        this.content = content;
        this.firstTimeChanged = true;
    }

    public String getFileName() {
        return fileName;
    }

    public VirtualFile getFile() {
        return file;
    }

    public Change getChange() {
        return change;
    }

    public String getContent() {
        return content;
    }

    public boolean isFirstTimeChanged() {
        return firstTimeChanged;
    }

    public void setFirstTimeChanged(boolean firstTimeChanged) {
        this.firstTimeChanged = firstTimeChanged;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeInfo that = (ChangeInfo) o;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, fileName, change, content, firstTimeChanged);
    }
}
