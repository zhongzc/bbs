package com.gaufoo.bbs.components.lecture.common;

import java.time.Instant;
import java.util.Objects;

public class LectureInfo {
    public final String title;
    public final String content;
    public final String position;
    public final Instant time;
    public final String lecturer;
    public final String note;

    private LectureInfo(String title, String content, String position, Instant time, String lecturer, String note) {
        this.title = title;
        this.content = content;
        this.position = position;
        this.time = time;
        this.lecturer = lecturer;
        this.note = note;
    }

    public static LectureInfo of(String title, String content, String position, Instant time, String lecturer, String note) {
        return new LectureInfo(title, content, position, time, lecturer, note);
    }

    public LectureInfo modTitle(String title) {
        return new LectureInfo(title, this.content, this.position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modContent(String content) {
        return new LectureInfo(this.title, content, this.position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modPosition(String position) {
        return new LectureInfo(this.title, this.content, position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modTime(Instant time) {
        return new LectureInfo(this.title, this.content, this.position, time, this.lecturer, this.note);
    }

    public LectureInfo modLecturer(String lecturer) {
        return new LectureInfo(this.title, this.content, this.position, this.time, lecturer, this.note);
    }

    public LectureInfo modNote(String note) {
        return new LectureInfo(this.title, this.content, this.position, this.time, this.lecturer, note);
    }

    @Override
    public String toString() {
        return "LectureInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.position + "'" + ", " + this.time + ", " + "'" + this.lecturer + "'" + ", " + "'" + this.note + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureInfo other = (LectureInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(content, other.content) &&
                Objects.equals(position, other.position) &&
                Objects.equals(time, other.time) &&
                Objects.equals(lecturer, other.lecturer) &&
                Objects.equals(note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, position, time, lecturer, note);
    }
}
