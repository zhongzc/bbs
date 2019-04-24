package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.BError;
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

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.Commons.fetchPersonalInfo;
import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.PersonalInformation.*;

public class PersonalInformation {
    public static PersonInfoResult personInfo(String id) {
        Procedure<ErrorCode, PersonalInfo> personInfoProc = fetchPersonalInfo(UserId.of(id));

        if (personInfoProc.isSuccessful()) return personInfoProc.retrieveResult().get();
        else return BError.of(personInfoProc.retrieveError().get());
    }

    public static EditPersonInfoResult editPersonInfo(PersonInfoInput input, String userToken) {
        Procedure<ErrorCode, PersonalInfo> proc = Commons.fetchUserId(UserToken.of(userToken)).then(userId ->
                Procedure.fromOptional(componentFactory.user.userInfo(userId), ErrorCode.UserNonExist).then(oldUserInfo -> {
                    UserFactory users = componentFactory.user;
                    if (input.gender == null) return Result.of(null);
                    return updateGender(userId, input.gender, oldUserInfo.gender)
                            .then(ig1 -> {
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
                            }).then(updatedFileId -> {
                                return Result.of(modOldPersonInfo(userId, oldUserInfo, input, updatedFileId));
                            });
                }));
        if (proc.isSuccessful()) return proc.retrieveResult().get();
        else return BError.of(proc.retrieveError().get());
    }

    public static List<String> allMajors() {
        return Arrays.stream(Major.values()).map(Enum::toString).collect(Collectors.toList());
    }

    public static List<String> allSchools() {
        return Arrays.stream(School.values()).map(Enum::toString).collect(Collectors.toList());
    }

    public static List<String> majorsIn(String school) {
        return parseSchool(school).map(s ->
                componentFactory.major.majorsIn(s)
                        .map(Enum::toString)
                        .collect(Collectors.toList())
        ).orElse(new LinkedList<>());
    }

    public static PersonalInfo consPersonalInfo(UserId userId, UserInfo userInfo) {
        return new PersonalInfo() {
            public String getIntroduction() { return nil2Emp(userInfo.introduction); }
            public String getMajor()        { return factorOutMajor(MajorCode.of(nil2Emp(userInfo.majorCode))); }
            public String getSchool()       { return factorOutSchool(MajorCode.of(nil2Emp(userInfo.majorCode))); }
            public String getGrade()        { return nil2Emp(userInfo.grade); }
            public String getGender()       { return Optional.ofNullable(userInfo.gender).orElse(UserInfo.Gender.secret).toString(); }
            public String getUsername()     { return nil2Emp(userInfo.nickname); }
            public String getUserId()       { return userId.toString(); }
            public String getPictureUrl()   { return factorOutPictureUrl(FileId.of(nil2Emp(userInfo.profilePicIdentifier))); }
        };
    }

    private static String factorOutMajor(MajorCode majorCode) {
        return componentFactory.major.getMajorValueFromCode(majorCode)
                    .map(v -> v.major.toString())
                    .orElse("");
    }

    private static String factorOutSchool(MajorCode majorCode) {
        return componentFactory.major.getMajorValueFromCode(majorCode)
                .map(v -> v.school.toString())
                .orElse("");
    }

    private static String factorOutPictureUrl(FileId fileId) {
        String fileUri = componentFactory.userProfiles.fileURI(fileId).orElse("");
        if (fileUri.isEmpty()) return "";
        return componentFactory.staticResourceConfig.makeUrl(StaticResourceConfig.FileType.UserProfileImage, URI.create(fileUri));
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
            componentFactory.userProfiles.Remove(FileId.of(oldPicId));
            return Result.of(fileId);
        });
    }

    private static PersonalInfo modOldPersonInfo(UserId userId, UserInfo oldUserInfo, PersonInfoInput in, FileId updatedId) {
        return new PersonalInfo() {
            public String getIntroduction() { return Optional.ofNullable(in.introduction).orElse(oldUserInfo.introduction); }
            public String getMajor()        { return Optional.ofNullable(in.major).orElse(factorOutMajor(MajorCode.of(oldUserInfo.majorCode))); }
            public String getSchool()       { return Optional.ofNullable(in.school).orElse(factorOutSchool(MajorCode.of(oldUserInfo.majorCode))); }
            public String getGrade()        { return Optional.ofNullable(in.grade).orElse(oldUserInfo.grade); }
            public String getGender()       { return Optional.ofNullable(in.gender).orElse(oldUserInfo.gender.toString()); }
            public String getUsername()     { return Optional.ofNullable(in.username).orElse(oldUserInfo.nickname); }
            public String getPictureUrl() {
                return factorOutPictureUrl(Optional.ofNullable(in.pictureBase64)
                        .map(ig -> FileId.of(oldUserInfo.profilePicIdentifier))
                        .orElse(updatedId)
                );
            }
            public String getUserId()       { return userId.value; }
        };
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

    private static String nil2Emp(String nullableStr) {
        return Optional.ofNullable(nullableStr).orElse("");
    }




    public static void main(String[] args) {
        System.out.println(parseGender("male").get());
        System.out.println(parseGender("男").get());
        System.out.println(parseGender("其他").get());
        System.out.println(parseGender("secret").get());
    }
}
