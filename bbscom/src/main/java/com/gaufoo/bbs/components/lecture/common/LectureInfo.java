package com.gaufoo.bbs.components.lecture.common;

import java.time.Instant;
import java.util.Objects;

public class LectureInfo {
    public final String title;
    public final String contentId;
    public final String position;
    public final Instant time;
    public final String lecturer;
    public final String note;

    private LectureInfo(String title, String contentId, String position, Instant time, String lecturer, String note) {
        this.title = title;
        this.contentId = contentId;
        this.position = position;
        this.time = time;
        this.lecturer = lecturer;
        this.note = note;
    }

    public static LectureInfo of(String title, String contentId, String position, Instant time, String lecturer, String note) {
        return new LectureInfo(title, contentId, position, time, lecturer, note);
    }

    public LectureInfo modTitle(String title) {
        return new LectureInfo(title, this.contentId, this.position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modContentId(String contentId) {
        return new LectureInfo(this.title, contentId, this.position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modPosition(String position) {
        return new LectureInfo(this.title, this.contentId, position, this.time, this.lecturer, this.note);
    }

    public LectureInfo modTime(Instant time) {
        return new LectureInfo(this.title, this.contentId, this.position, time, this.lecturer, this.note);
    }

    public LectureInfo modLecturer(String lecturer) {
        return new LectureInfo(this.title, this.contentId, this.position, this.time, lecturer, this.note);
    }

    public LectureInfo modNote(String note) {
        return new LectureInfo(this.title, this.contentId, this.position, this.time, this.lecturer, note);
    }

    @Override
    public String toString() {
        return "LectureInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.position + "'" + ", " + this.time + ", " + "'" + this.lecturer + "'" + ", " + "'" + this.note + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureInfo other = (LectureInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(contentId, other.contentId) &&
                Objects.equals(position, other.position) &&
                Objects.equals(time, other.time) &&
                Objects.equals(lecturer, other.lecturer) &&
                Objects.equals(note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contentId, position, time, lecturer, note);
    }
}
