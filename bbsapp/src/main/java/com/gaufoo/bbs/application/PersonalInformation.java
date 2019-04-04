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
import com.gaufoo.bbs.components.user.common.UserInfo.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PersonalInformation {
    private static Logger logger = LoggerFactory.getLogger(PersonalInformation.class);

    public static UserInfoResult userInfo(String userId) {
        logger.debug("userInfo, userId: {}", userId);
        return ComponentFactory.user.userInfo(UserId.of(userId))
                .map(info -> (UserInfoResult)new UserInfo() {
                    @Override
                    public String getPictureBase64() {
                        logger.debug("userInfo :: getPictureBase64, userId: {}", userId);
                        return factorOutImgBase64(info.profilePicIdentifier);
                    }

                    @Override
                    public String getUsername() {
                        logger.debug("userInfo :: getUsername, userId: {}", userId);
                        return info.nickname;
                    }

                    @Override
                    public String getGender() {
                        logger.debug("userInfo :: getGender, userId: {}", userId);
                        return info.gender.toString();
                    }

                    @Override
                    public String getGrade() {
                        logger.debug("userInfo :: getGrade, userId: {}", userId);
                        return info.grade;
                    }

                    @Override
                    public String getSchool() {
                        logger.debug("userInfo :: getSchool, userId: {}", userId);
                        return factorOutSchool(info.majorCode);
                    }

                    @Override
                    public String getMajor() {
                        logger.debug("userInfo :: getMajor, userId: {}", userId);
                        return factorOutMajor(info.majorCode);
                    }

                    @Override
                    public String getIntroduction() {
                        logger.debug("userInfo :: getIntroduction, userId: {}", userId);
                        return info.introduction;
                    }
                }).orElseGet(() -> {
                    logger.debug("userInfo - failed, error: {}, userId: {}", "找不到用户", userId);
                    return UserInfoError.of("找不到用户");
                });
    }

    private static String factorOutImgBase64(String pictureId) {
        return ComponentFactory.file.fileURI(FileId.of(pictureId))
                .map(uri -> {
                    try {
                        Path imgPath = Paths.get(new URI(uri));
                        byte[] imgBytes = Files.readAllBytes(imgPath);
                        return Base64.getEncoder().encodeToString(imgBytes);
                    } catch (URISyntaxException | IOException e) {
                        logger.warn("userInfo :: getPictureBase64 - failed, error: {}", e.getMessage());
                        return "";
                    }
                }).orElseGet(() -> {
                    logger.debug("userInfo :: factorOutImgBase64 - failed, error: {}, pictureId: {}", "找不到pictureURI", pictureId);
                    return "";
                });
    }

    private static String factorOutSchool(String majorCode) {
        return ComponentFactory.major.getMajorValueFromCode(MajorCode.of(majorCode))
                .map(majorVal -> majorVal.school.toString()).orElseGet(() -> {
                    logger.warn("userInfo :: getSchool(majorCode = {}) - failed, error: 转换失败", majorCode);
                    return "";
                });
    }

    private static String factorOutMajor(String majorCode) {
        return ComponentFactory.major.getMajorValueFromCode(MajorCode.of(majorCode))
                .map(majorVal -> majorVal.major.toString()).orElseGet(() -> {
                    logger.warn("userInfo :: getMajor(majorCode = {}) - failed, error: 转换失败", majorCode);
                    return "";
                });
    }

    public static ModifyPersonInfoError uploadUserProfile(String userToken, String base64Image) {
        logger.debug("uploadUserProfile, userToken: {}, base64Image: {}", userToken, base64Image);
        try {
            String userId = ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
            byte[] decodedImg = Base64.getDecoder().decode(base64Image);
            return ComponentFactory.file.createFile(decodedImg, "user-profile-" + userId)
                    .map(fileId -> {
                        boolean success = ComponentFactory.user.changeProfilePicIdentifier(UserId.of(userId), fileId.value);
                        if (success) {
                            logger.debug("uploadUserProfile - success, userId: {}, fileId: {}", userId, fileId.value);
                            return null;
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

    public static ModifyPersonInfoError changeAcademy(String userToken, String academy) {
        Optional<School> oSchool = parseAcademy(academy);
        if (!oSchool.isPresent()) return ModifyPersonInfoError.of("无法解析学院");
        School school = oSchool.get();

        return changeCommon(userToken, academy, "school", "changeAcademy","更改学院失败",
                ((userFactory, userId) ->
                        updateMajorValue(userFactory, userId,
                                (oldMajorValue -> MajorValue.of(school, oldMajorValue.major)))));
    }
    private static Optional<School> parseAcademy(String academy) {
        for (School school : School.values()) {
            if (school.toString().equals(academy))
                return Optional.of(school);
        }
        return Optional.empty();
    }
    public static ModifyPersonInfoError changeMajor(String userToken, String majorStr) {
        Optional<Major> oMajor = parseMajor(majorStr);
        if (!oMajor.isPresent()) return ModifyPersonInfoError.of("无法解析专业");
        Major major = oMajor.get();

        return changeCommon(userToken, majorStr, "major", "changeMajor","更改专业失败",
                ((userFactory, userId) ->
                        updateMajorValue(userFactory, userId,
                                (oldMajorValue -> MajorValue.of(oldMajorValue.school, major)))));
    }
    private static Optional<Major> parseMajor(String majorStr) {
        for (Major major : Major.values()) {
            if (major.toString().equals(majorStr))
                return Optional.of(major);
        }
        return Optional.empty();
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



    public static ModifyPersonInfoError changeGender(String userToken, String genderStr) {
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


    public static ModifyPersonInfoError changeGrade(String userToken, String grade) {
        return changeCommon(userToken, grade, "grade", "changeGrade", "更改年级失败",
                (userFactory, userId) -> userFactory.changeIntroduction(userId, grade));
    }

    public static ModifyPersonInfoError changeIntroduction(String userToken, String introduction) {
        return changeCommon(userToken, introduction, "introduction", "changeIntroduction", "更改个人信息失败",
                (userFactory, userId) -> userFactory.changeIntroduction(userId, introduction));
    }

    public static ModifyPersonInfoError changeNickname(String userToken, String nickname) {
        return changeCommon(userToken, nickname, "nickname", "changeNickname", "更改昵称失败",
                (userFactory, userId) -> userFactory.changeNickname(userId, nickname));
    }

    private static ModifyPersonInfoError changeCommon(String userToken, String payload, String field, String methodName,
                                                      String errMsg, BiFunction<UserFactory, UserId, Boolean> compFunc) {
        logger.debug("{}, userToken: {}, {}: {}", methodName, userToken, field, payload);
        try {
            String userId = ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
            boolean success = compFunc.apply(ComponentFactory.user, UserId.of(userId));

            if (success) {
                logger.debug("{} - success, userToken: {}, {}: {}", methodName, userToken, field, payload);
                return null;
            } else {
                logger.debug("{} - failed, error: {} userToken: {}, {}: {}", errMsg, methodName, userToken, field, payload);
                return ModifyPersonInfoError.of(errMsg);
            }
        } catch (AuthenticatorException e) {
            logger.debug("{} - failed, error: {} userToken: {}, {}: {}", methodName, e.getMessage(), userToken, field, payload);
            return ModifyPersonInfoError.of(e.getMessage());
        }
    }


    public static class UserInfoError implements UserInfoResult {
        private String error;

        public UserInfoError(String error) {
            this.error = error;
        }

        public static UserInfoError of(String error) {
            return new UserInfoError(error);
        }

        public String getError() {
            return error;
        }
    }

    public interface UserInfo extends UserInfoResult {
        String getPictureBase64();
        String getUsername();
        String getGender();
        String getGrade();
        String getSchool();
        String getMajor();
        String getIntroduction();
    }

    public interface UserInfoResult {
    }

    public static class ModifyPersonInfoError {
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


}
