package com.gaufoo.bbs.components.user.common;

import java.time.Instant;
import java.util.Objects;

final public class UserInfo {
    public final String nickname;
    public final String profilePicIdentifier;
    public final Gender gender;
    public final String grade;
    public final String majorCode;
    public final String introduction;
    public final Instant createTime;

    private UserInfo(String nickname, String profilePicIdentifier, Gender gender, String grade, String majorCode, String introduction, Instant createTime) {
        this.nickname = nickname;
        this.profilePicIdentifier = profilePicIdentifier;
        this.gender = gender;
        this.grade = grade;
        this.majorCode = majorCode;
        this.introduction = introduction;
        this.createTime = createTime;
    }

    public static UserInfo of(String nickname, String profilePicIdentifier, Gender gender, String grade, String majorCode, String introduction, Instant createTime) {
        return new UserInfo(nickname, profilePicIdentifier, gender, grade, majorCode, introduction, createTime);
    }

    public static UserInfo of(String nickname, String profilePicIdentifier, Gender gender, String grade, String majorCode, String introduction) {
        return new UserInfo(nickname, profilePicIdentifier, gender, grade, majorCode, introduction, Instant.now());
    }

    public UserInfo modNickname(String nickname) {
        return new UserInfo(nickname, this.profilePicIdentifier, this.gender, this.grade, this.majorCode, this.introduction, this.createTime);
    }

    public UserInfo modProfilePicIdentifier(String profilePicIdentifier) {
        return new UserInfo(this.nickname, profilePicIdentifier, this.gender, this.grade, this.majorCode, this.introduction, this.createTime);
    }

    public UserInfo modGender(Gender gender) {
        return new UserInfo(this.nickname, this.profilePicIdentifier, gender, this.grade, this.majorCode, this.introduction, this.createTime);
    }

    public UserInfo modGrade(String grade) {
        return new UserInfo(this.nickname, this.profilePicIdentifier, this.gender, grade, this.majorCode, this.introduction, this.createTime);
    }

    public UserInfo modMajorCode(String majorCode) {
        return new UserInfo(this.nickname, this.profilePicIdentifier, this.gender, this.grade, majorCode, this.introduction, this.createTime);
    }

    public UserInfo modIntroduction(String introduction) {
        return new UserInfo(this.nickname, this.profilePicIdentifier, this.gender, this.grade, this.majorCode, introduction, this.createTime);
    }

    public UserInfo modCreateTime(Instant createTime) {
        return new UserInfo(this.nickname, this.profilePicIdentifier, this.gender, this.grade, this.majorCode, this.introduction, createTime);
    }

    @Override
    public String toString() {
        return "UserInfo" + "(" + "'" + this.nickname + "'" + ", " + "'" + this.profilePicIdentifier + "'" + ", " + this.gender + ", " + "'" + this.grade + "'" + ", " + "'" + this.majorCode + "'" + ", " + "'" + this.introduction + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo other = (UserInfo) o;
        return Objects.equals(nickname, other.nickname) &&
                Objects.equals(profilePicIdentifier, other.profilePicIdentifier) &&
                Objects.equals(gender, other.gender) &&
                Objects.equals(grade, other.grade) &&
                Objects.equals(majorCode, other.majorCode) &&
                Objects.equals(introduction, other.introduction) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, profilePicIdentifier, gender, grade, majorCode, introduction, createTime);
    }

    public enum Gender {
        male,
        female,
        other,
        secret
    }
}
