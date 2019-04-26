package com.gaufoo.bbs.application.util;

public class IdConfig {
    public final String user;
    public final String userProfiles;
    public final String lost;
    public final String found;
    public final String schoolHeat;
    public final String entertainment;
    public final String lecture;
    public final String learningResource;
    public final String comment;
    public final String reply;
    public final String content;
    public final String lostFoundImages;
    public final String contentImages;
    public final String news;
    public final String commentGroup;

    public IdConfig(String user, String userProfiles, String lost, String found, String schoolHeat, String entertainment, String lecture, String learningResource, String comment, String reply, String content, String lostFoundImages, String contentImages, String news, String commentGroup) {
        this.user = user;
        this.userProfiles = userProfiles;
        this.lost = lost;
        this.found = found;
        this.schoolHeat = schoolHeat;
        this.entertainment = entertainment;
        this.lecture = lecture;
        this.learningResource = learningResource;
        this.comment = comment;
        this.reply = reply;
        this.content = content;
        this.lostFoundImages = lostFoundImages;
        this.contentImages = contentImages;
        this.news = news;
        this.commentGroup = commentGroup;
    }

    public static IdConfig defau1t() {
        return new IdConfig("user-id", "user-profile-id", "lost-id", "found-id", "school-heat-id", "entertainment-id", "lecture", "learning-resource-id", "comment-id", "reply-id", "content-id", "lost-found-images", "content-images", "news", "comment-group");
    }
}
