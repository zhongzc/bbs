package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorCode;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.bbs.util.TaskChain.Fail;
import com.gaufoo.bbs.util.TaskChain.Procedure;
import com.gaufoo.bbs.util.TaskChain.Result;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.Commons.fetchPersonalInfo;
import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.PersonalInformation.*;

public class PersonalInformation {
    public static PersonInfoResult personInfo(String id) {
        return fetchPersonalInfo(UserId.of(id))
                .reduce(Error::of, r -> r);
    }

    public static EditPersonInfoResult editPersonInfo(PersonInfoInput input, String userToken) {
        UserFactory users = componentFactory.user;
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> Procedure.fromOptional(users.userInfo(userId), ErrorCode.UserNonExist)
                        .then(oldUserInfo -> Result.<ErrorCode, PersonalInfo>of(null)
                        .then(ig0 -> {
                            if (input.gender == null) return Result.of(null);
                            return updateGender(userId, input.gender, oldUserInfo.gender);
                        }).then(ig1 -> {
                            if (input.username == null) return Result.of(null);
                            return Result.of(users.changeNickname(userId, input.username), () -> users.changeNickname(userId, oldUserInfo.nickname));
                        }).then(ig2 -> {
                            if (input.grade == null) return Result.of(null);
                            return Result.of(users.changeGrade(userId, input.grade), () -> users.changeGrade(userId, oldUserInfo.grade));
                        }).then(ig3 -> {
                            if (input.introduction == null) return Result.of(null);
                            return Result.of(users.changeIntroduction(userId, input.introduction), () -> users.changeIntroduction(userId, oldUserInfo.introduction));
                        }).then(ig4 -> {
                            if (input.major == null) return Result.of(null);
                            return updateMajor(userId, input.major, MajorCode.of(oldUserInfo.majorCode));
                        }).then(ig5 -> {
                            if (input.school == null) return Result.of(null);
                            return updateSchool(userId, input.school, MajorCode.of(oldUserInfo.majorCode));
                        }).then(ig6 -> {
                            if (input.pictureBase64 == null) return Result.of(null);
                            return updateImage(userId, input.pictureBase64, oldUserInfo.profilePicIdentifier);
                        }).then(updatedFileId -> Result.of(modOldPersonInfo(userId, oldUserInfo, input, updatedFileId)))))
                .reduce(Error::of, r -> r);
    }

    public static List<String> allMajors() {
        return Arrays.stream(Major.values()).map(Enum::toString).collect(Collectors.toList());
    }

    public static List<String> allSchools() {
        return Arrays.stream(School.values()).map(Enum::toString).collect(Collectors.toList());
    }

    public static List<String> allCourses() {
        return componentFactory.course.allCourses().map(Enum::toString).collect(Collectors.toList());
    }

    public static List<String> majorsIn(String school) {
        return parseSchool(school).map(s ->
                componentFactory.major.majorsIn(s)
                        .map(Enum::toString)
                        .collect(Collectors.toList())
        ).orElse(new LinkedList<>());
    }

    static PersonalInfo consPersonalInfo(UserId userId, UserInfo userInfo) {
        return new PersonalInfo() {
            public String getIntroduction() { return userInfo.introduction; }
            public String getMajor()        { return Optional.ofNullable(userInfo.majorCode).map(c -> factorOutMajor(MajorCode.of(c))).orElse(null); }
            public String getSchool()       { return Optional.ofNullable(userInfo.majorCode).map(c -> factorOutSchool(MajorCode.of(c))).orElse(null); }
            public String getGrade()        { return userInfo.grade; }
            public String getGender()       { return Optional.ofNullable(userInfo.gender).map(Objects::toString).orElse(null); }
            public String getUsername()     { return userInfo.nickname; }
            public String getUserId()       { return userId.value; }
            public String getPictureURL()   { return Optional.ofNullable(userInfo.profilePicIdentifier).map(u -> factorOutPictureUrl(FileId.of(u))).orElse(null) ; }
        };
    }

    private static String factorOutMajor(MajorCode majorCode) {
        return componentFactory.major.getMajorValueFromCode(majorCode)
                    .map(v -> v.major.toString())
                    .orElse(null);
    }

    private static String factorOutSchool(MajorCode majorCode) {
        return componentFactory.major.getMajorValueFromCode(majorCode)
                .map(v -> v.school.toString())
                .orElse(null);
    }

    private static String factorOutPictureUrl(FileId fileId) {
        return Commons.fetchFileUrl(componentFactory.userProfiles, StaticResourceConfig.FileType.UserProfileImage, fileId)
                .reduce(e -> null, i -> i);
    }

    private static Procedure<ErrorCode, Boolean> updateGender(UserId userId, String newGenderStr, UserInfo.Gender oldGender) {
        return Procedure.fromOptional(parseGender(newGenderStr), ErrorCode.ParseGenderError)
                .then(gender -> {
                    boolean ok = componentFactory.user.changeGender(userId, gender);
                    if (ok) return Result.of(true, () -> componentFactory.user.changeGender(userId, oldGender));
                    else return Fail.of(ErrorCode.ChangeGenderError);
                });
    }

    private static Procedure<ErrorCode, Boolean> updateMajor(UserId userId, String majorStr, MajorCode oldMajorCode) {
        return Procedure.fromOptional(parseMajor(majorStr), ErrorCode.ParseMajorFailed)
                .then(major -> {
                    Optional<Boolean> changeRes = componentFactory.major.getMajorValueFromCode(oldMajorCode).map(majorValue ->
                        componentFactory.major.generateMajorCode(majorValue.modMajor(major))
                    ).map(newMajorCode ->
                        componentFactory.user.changeMajorCode(userId, newMajorCode.toString())
                    ).map(success -> success ? true : null);
                    return Procedure.fromOptional(changeRes, ErrorCode.ChangeMajorFailed);
                });
    }

    private static Procedure<ErrorCode, ?> updateSchool(UserId userId, String schoolStr, MajorCode oldMajorCode) {
        return Procedure.fromOptional(parseSchool(schoolStr), ErrorCode.ParseSchoolFailed)
                .then(school -> {
                    Optional<Boolean> changeRes = componentFactory.major.getMajorValueFromCode(oldMajorCode).map(majorValue ->
                            componentFactory.major.generateMajorCode(majorValue.modSchool(school))
                    ).map(newMajorCode ->
                            componentFactory.user.changeMajorCode(userId, newMajorCode.toString())
                    ).map(success -> success ? true : null);
                    return Procedure.fromOptional(changeRes, ErrorCode.ChangeSchoolFailed);
                });
    }

    private static Procedure<ErrorCode, FileId> updateImage(UserId userId, String newPicBase64, String oldPicId) {
        byte[] image = Base64.getDecoder().decode(newPicBase64);

        Optional<FileId> res = componentFactory.userProfiles.createFile(image, UUID.randomUUID().toString())
                .map(fileId -> componentFactory.user.changeProfilePicIdentifier(userId, fileId.toString()) ? fileId : null);
        return Procedure.fromOptional(res, ErrorCode.ChangeImageFailed).then(fileId -> {
            componentFactory.userProfiles.remove(FileId.of(oldPicId));
            return Result.of(fileId);
        });
    }

    private static PersonalInfo modOldPersonInfo(UserId userId, UserInfo oldUserInfo, PersonInfoInput in, FileId nullableUpdatedId) {
        return new PersonalInfo() {
            public String getIntroduction() { return preferNew(in.introduction, oldUserInfo.introduction); }
            public String getMajor()        { return preferNew(in.major, oldUserInfo.majorCode, oc -> factorOutMajor(MajorCode.of(oc))); }
            public String getSchool()       { return preferNew(in.school, oldUserInfo.majorCode, oc -> factorOutSchool(MajorCode.of(oc))); }
            public String getGrade()        { return preferNew(in.grade, oldUserInfo.grade); }
            public String getGender()       { return preferNew(in.gender, oldUserInfo.gender, Object::toString); }
            public String getUsername()     { return preferNew(in.username, oldUserInfo.nickname); }
            public String getPictureURL() {
                return Optional.ofNullable(in.pictureBase64)
                        .map(pictureIsModified -> factorOutPictureUrl(nullableUpdatedId))
                        .orElseGet(() -> Optional.ofNullable(oldUserInfo.profilePicIdentifier)
                                .map(oldPicture -> factorOutPictureUrl(FileId.of(oldPicture)))
                                .orElse(null));
            }
            public String getUserId()       { return userId.value; }
        };
    }

    private static <T, U> T preferNew(T newItem, U oldItem, Function<U ,T> oldTransformer) {
        if (newItem == null) {
            if (oldItem == null) return null;
            else return oldTransformer.apply(oldItem);
        } else return newItem;
    }

    private static <T> T preferNew(T newItem, T oldItem) {
        return preferNew(newItem, oldItem, i -> i);
    }


    private static Optional<UserInfo.Gender> parseGender(String genderStr) {
        UserInfo.Gender result = null;
        switch (genderStr) {
            case "男": result = UserInfo.Gender.male; break;
            case "女": result = UserInfo.Gender.female; break;
            case "秘密": result = UserInfo.Gender.secret; break;
            case "其他": result = UserInfo.Gender.other; break;
            default: {
                for (UserInfo.Gender g : UserInfo.Gender.values()) {
                    if (g.toString().equals(genderStr)) {
                        result = g;
                    }
                }
            }
        }
        return Optional.ofNullable(result);
    }

    private static Optional<Major> parseMajor(String majorStr) {
        return Arrays.stream(Major.values()).filter(m -> m.toString().equals(majorStr)).findFirst();
    }

    private static Optional<School> parseSchool(String schoolStr) {
        return Arrays.stream(School.values()).filter(m -> m.toString().equals(schoolStr)).findFirst();
    }
}
