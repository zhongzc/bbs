package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorCode;
import com.gaufoo.bbs.components.scutMajor.common.MajorValue;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.bbs.components.user.common.UserInfo.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonalInformation {
    private static Logger logger = LoggerFactory.getLogger(PersonalInformation.class);

    public static PersonalInfoResult userInfo(String userId) {
        logger.debug("userInfo, userId: {}", userId);
        return ComponentFactory.user.userInfo(UserId.of(userId))
                .map(PersonalInformation::constructUserInfo)
                .orElseGet(() -> {
                    logger.debug("userInfo - failed, error: {}, userId: {}", "找不到用户", userId);
                    return PersonalInfoError.of("找不到用户");
                });
    }

    private static PersonalInfoResult constructUserInfo(UserInfo info) {
        return new PersonalInfo() {
            @Override
            public String getPictureUrl() {
                logger.debug("userInfo :: getPictureUrl, nickname: {}", info.nickname);
                return factorOutImageUrl(info.profilePicIdentifier);
            }
            @Override
            public String getUsername() {
                logger.debug("userInfo :: getUsername, nickname: {}", info.nickname);
                return info.nickname;
            }
            @Override
            public String getGender() {
                logger.debug("userInfo :: getGender, nickname: {}", info.nickname);
                return info.gender.toString();
            }
            @Override
            public String getGrade() {
                logger.debug("userInfo :: getGrade, nickname: {}", info.nickname);
                return info.grade;
            }
            @Override
            public String getSchool() {
                logger.debug("userInfo :: getSchool, nickname: {}", info.nickname);
                return factorOutSchool(info.majorCode);
            }
            @Override
            public String getMajor() {
                logger.debug("userInfo :: getMajor, nickname: {}", info.nickname);
                return factorOutMajor(info.majorCode);
            }
            @Override
            public String getIntroduction() {
                logger.debug("userInfo :: getIntroduction, nickname: {}", info.nickname);
                return info.introduction;
            }
        };
    }

    private static String factorOutImageUrl(String pictureId) {
        return Optional.ofNullable(pictureId)
                .map(FileId::of)
                .flatMap(ComponentFactory.userProfiles::fileURI)
                .orElse("");
    }

    private static String factorOutSchool(String majorCode) {
        return factorOutMajorCode(majorCode, majorValue -> majorValue.school.toString());
    }
    private static String factorOutMajor(String majorCode) {
        return factorOutMajorCode(majorCode, majorValue -> majorValue.major.toString());
    }
    private static String factorOutMajorCode(String majorCode, Function<MajorValue, String> transformer) {
        return Optional.ofNullable(majorCode)
                .map(MajorCode::of)
                .flatMap(ComponentFactory.major::getMajorValueFromCode)
                .map(transformer)
                .orElseGet(() -> {
                    logger.warn("userInfo :: factorOutMajorCode(majorCode = {}) - failed, error: 转换失败", majorCode);
                    return "";
                });
    }


    public static List<String> allAcademies() {
        logger.debug("allAcademies");
        return Arrays.stream(School.values()).map(Objects::toString).collect(Collectors.toList());
    }

    public static List<String> allMajors() {
        logger.debug("allMajors");
        return Arrays.stream(Major.values()).map(Objects::toString).collect(Collectors.toList());
    }

    public static MajorsInResult majorsIn(String academy) {
        logger.debug("majorsIn, academy: {}", academy);
        return parseAcademy(academy)
                .map(school -> ComponentFactory.major.majorsIn(school)
                        .map(Enum::toString)
                        .collect(Collectors.toList()))
                .map(listOfMajor -> (MajorsInResult)MajorsInPayload.of(listOfMajor))
                .orElseGet(() -> {
                    logger.debug("majorsIn - failed, academy: {}", academy);
                    return MajorsInError.of("无法解析学院");
                });
    }


    public static ModifyPersonInfoResult uploadUserProfile(String userToken, String base64Image) {
        logger.debug("uploadUserProfile, userToken: {}, base64Image: {}", userToken, base64Image);
        try {
            String userId = ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
            byte[] decodedImg = Base64.getDecoder().decode(base64Image);
            return ComponentFactory.userProfiles.createFile(decodedImg, "user-profile-" + userId)
                    .map(fileId -> {
                        boolean success = ComponentFactory.user.changeProfilePicIdentifier(UserId.of(userId), fileId.value);
                        if (success) {
                            logger.debug("uploadUserProfile - success, userId: {}, fileId: {}", userId, fileId.value);
                            return ModifyPersonSuccess.ok();
                        }
                        logger.debug("uploadUserProfile - failed, error: {}, userId: {}, fileId: {}", "更改图片失败", userId, fileId.value);
                        return ModifyPersonInfoError.of("更改图片失败");
                    }).orElseGet(() -> {
                        logger.debug("uploadUserProfile - failed, error: {}, userId: {}, base64Image: {}", "创建图片失败", userId, base64Image);
                        return ModifyPersonInfoError.of("创建图片失败");
                    });


        } catch (AuthenticatorException e) {
            logger.debug("uploadUserProfile - failed, error: {}, userToken: {}, base64Image: {}", e.getMessage(), userToken, base64Image);
            return ModifyPersonInfoError.of(e.getMessage());
        }
    }

    public static ModifyPersonInfoResult changeAcademy(String userToken, String academy) {
        return parseAcademy(academy).map(school ->
                changeCommon(userToken, academy, "school", "changeAcademy","更改学院失败",
                        ((userFactory, userId) ->
                                updateMajorValue(userFactory, userId,
                                        (oldMajorValue -> MajorValue.of(school, oldMajorValue.major))))))
                .orElse(ModifyPersonInfoError.of("无法解析学院"));
    }
    private static Optional<School> parseAcademy(String academy) {
        for (School school : School.values()) {
            if (school.toString().equals(academy))
                return Optional.of(school);
        }
        return Optional.empty();
    }
    public static ModifyPersonInfoResult changeMajor(String userToken, String majorStr) {
        return parseMajor(majorStr).map(major ->
                changeCommon(userToken, majorStr, "major", "changeMajor","更改专业失败",
                        ((userFactory, userId) ->
                                updateMajorValue(userFactory, userId,
                                        oldMajorValue -> MajorValue.of(oldMajorValue.school, major)))))
                .orElse(ModifyPersonInfoError.of("无法解析专业"));
    }
    private static Optional<Major> parseMajor(String majorStr) {
        return Stream.of(Major.values())
                .filter(major -> major.toString().equals(majorStr))
                .findFirst();
    }
    private static Boolean updateMajorValue(UserFactory userFactory, UserId userId, Function<MajorValue, MajorValue> transformer) {
        return userFactory.userInfo(userId)
                .flatMap(userInfo -> Optional.ofNullable(userInfo.majorCode))
                .map(MajorCode::of)
                .flatMap(ComponentFactory.major::getMajorValueFromCode)
                .map(transformer)
                .map(ComponentFactory.major::generateMajorCode)
                .map(newMajorVal -> ComponentFactory.user.changeMajorCode(userId, newMajorVal.value))
                .orElse(false);
    }



    public static ModifyPersonInfoResult changeGender(String userToken, String genderStr) {
        Optional<Gender> oGender = parseGender(genderStr);
        if (!oGender.isPresent()) {
            logger.debug("changeGender - failed, error: {}, userToken: {}, genderStr: {}", "无法解析性别", userToken, genderStr);
            return ModifyPersonInfoError.of("无法解析性别");
        }
        return changeCommon(userToken, genderStr, "gender", "changeGender", "更改性别失败",
                ((userFactory, userId) -> ComponentFactory.user.changeGender(userId, oGender.get())));
    }
    private static Optional<Gender> parseGender(String gender) {
        switch (gender.toLowerCase()) {
            case "male": return Optional.of(Gender.male);
            case "female": return Optional.of(Gender.female);
            case "other": return Optional.of(Gender.other);
            case "secret": return Optional.of(Gender.secret);
            default: return Optional.empty();
        }
    }


    public static ModifyPersonInfoResult changeGrade(String userToken, String grade) {
        return changeCommon(userToken, grade, "grade", "changeGrade", "更改年级失败",
                (userFactory, userId) -> userFactory.changeIntroduction(userId, grade));
    }

    public static ModifyPersonInfoResult changeIntroduction(String userToken, String introduction) {
        return changeCommon(userToken, introduction, "introduction", "changeIntroduction", "更改个人信息失败",
                (userFactory, userId) -> userFactory.changeIntroduction(userId, introduction));
    }

    public static ModifyPersonInfoResult changeNickname(String userToken, String nickname) {
        return changeCommon(userToken, nickname, "nickname", "changeNickname", "更改昵称失败",
                (userFactory, userId) -> userFactory.changeNickname(userId, nickname));
    }

    private static ModifyPersonInfoResult changeCommon(String userToken, String payload, String field, String methodName,
                                                       String errMsg, BiFunction<UserFactory, UserId, Boolean> compFunc) {
        logger.debug("{}, userToken: {}, {}: {}", methodName, userToken, field, payload);
        try {
            String userId = ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
            boolean success = compFunc.apply(ComponentFactory.user, UserId.of(userId));

            if (success) {
                logger.debug("{} - success, userToken: {}, {}: {}", methodName, userToken, field, payload);
                return ModifyPersonSuccess.ok();
            } else {
                logger.debug("{} - failed, error: {} userToken: {}, {}: {}", errMsg, methodName, userToken, field, payload);
                return ModifyPersonInfoError.of(errMsg);
            }
        } catch (AuthenticatorException e) {
            logger.debug("{} - failed, error: {} userToken: {}, {}: {}", methodName, e.getMessage(), userToken, field, payload);
            return ModifyPersonInfoError.of(e.getMessage());
        }
    }


    public static class PersonalInfoError implements PersonalInfoResult {
        private String error;

        public PersonalInfoError(String error) {
            this.error = error;
        }

        public static PersonalInfoError of(String error) {
            return new PersonalInfoError(error);
        }

        public String getError() {
            return error;
        }
    }

    public interface PersonalInfo extends PersonalInfoResult {
        String getPictureUrl();
        String getUsername();
        String getGender();
        String getGrade();
        String getSchool();
        String getMajor();
        String getIntroduction();
    }

    public interface PersonalInfoResult {
    }

    public static class ModifyPersonInfoError implements ModifyPersonInfoResult {
        private String error;

        public ModifyPersonInfoError(String error) {
            this.error = error;
        }

        public static ModifyPersonInfoError of(String error) {
            return new ModifyPersonInfoError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class ModifyPersonSuccess implements ModifyPersonInfoResult {
        private boolean ok;

        public ModifyPersonSuccess() {
            this.ok = true;
        }

        public static ModifyPersonSuccess ok() {
            return new ModifyPersonSuccess();
        }

        public boolean isOk() {
            return ok;
        }
    }

    public interface ModifyPersonInfoResult {
    }

    public static class MajorsInPayload implements MajorsInResult {
        private List<String> majors;

        public MajorsInPayload(List<String> majors) {
            this.majors = majors;
        }

        public static MajorsInPayload of(List<String> majors) {
            return new MajorsInPayload(majors);
        }

        public List<String> getMajors() {
            return majors;
        }
    }

    public static class MajorsInError implements MajorsInResult {
        private String error;

        public MajorsInError(String error) {
            this.error = error;
        }

        public static MajorsInError of(String error) {
            return new MajorsInError(error);
        }

        public String getError() {
            return error;
        }
    }

    public interface MajorsInResult {
    }
}
