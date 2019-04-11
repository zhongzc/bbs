package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.util.Utils.notNullOrEmpty;

public class LostAndFound {
    private static Logger logger = LoggerFactory.getLogger(LostAndFound.class);

    private enum Type {
        Lost, Found
    }
    private static class PublishException extends RuntimeException {
        PublishException(String errMsg) {
            super(errMsg);
        }
    }
    private interface UndoFunction {
        void undo();
    }
    private static Validator<ItemInfoInput> itemInfoInputNonNull =  Validator.defau1t("itemInfoValidator",
            input -> notNullOrEmpty(input.contact) &&
                    notNullOrEmpty(input.description) &&
                    notNullOrEmpty(input.itemName) &&
                    notNullOrEmpty(input.position) &&
                    notNullOrEmpty(input.imageBase64) &&
                    input.time > 0
    );

    public static ItemInfoResult lostItem(String lostId) {
        logger.debug("lostItem, lostId: {}", lostId);
        return componentFactory.lostFound.lostInfo(LostId.of(lostId))
                .map(lostInfo -> (ItemInfoResult) constructItemInfo(lostInfo))
                .orElseGet(() -> {
                    logger.debug("lostItem - failed, error: {}, lostId: {}", "找不到失物", lostId);
                    return LostFoundError.of("找不到失物");
                });
    }

    private static LostItemInfo constructItemInfo(LostInfo lostInfo) {
        return new LostItemInfo() {
            @Override
            public String getPublisher() {
                return lostInfo.publisher;
            }
            @Override
            public String getName() {
                return lostInfo.objName;
            }
            @Override
            public String getDescription() {
                return lostInfo.description;
            }
            @Override
            public String getPosition() {
                return lostInfo.position;
            }
            @Override
            public String getPictureUrl() {
                return factorOutPictureUrl(lostInfo.imageIdentifier);
            }
            @Override
            public String getContact() {
                return lostInfo.contact;
            }

            @Override
            public Long getCreationTime() {
                return lostInfo.createTime.toEpochMilli();
            }
            @Override
            public Long getLostTime() {
                return lostInfo.lostTime.toEpochMilli();
            }

        };
    }

    public static ItemInfoResult foundItem(String foundId) {
        logger.debug("foundItem, foundId: {}", foundId);
        return componentFactory.lostFound.foundInfo(FoundId.of(foundId))
                .map(foundInfo -> (ItemInfoResult) constructItemInfo(foundInfo))
                .orElseGet(() -> {
                    logger.debug("foundItem - failed, error: {}, foundId: {}", "找不到寻物", foundId);
                    return LostFoundError.of("");
                });
    }

    private static FoundItemInfo constructItemInfo(FoundInfo foundInfo) {
        return new FoundItemInfo() {
            @Override
            public String getPublisher() {
                return foundInfo.publisher;
            }
            @Override
            public String getName() {
                return foundInfo.objName;
            }
            @Override
            public String getDescription() {
                return foundInfo.description;
            }
            @Override
            public String getPosition() {
                return foundInfo.position;
            }
            @Override
            public String getPictureUrl() {
                return factorOutPictureUrl(foundInfo.imageIdentifier);
            }
            @Override
            public String getContact() {
                return foundInfo.contact;
            }

            @Override
            public Long getCreationTime() {
                return foundInfo.createTime.toEpochMilli();
            }
            @Override
            public Long getFoundTime() {
                return foundInfo.foundTime.toEpochMilli();
            }
        };
    }
    private static String factorOutPictureUrl(String imageId) {
        logger.debug("factorOutPictureUrl, imageId: {}", imageId);
        return Optional.ofNullable(imageId)
                .map(FileId::of)
                .flatMap(componentFactory.lostFoundImages::fileURI)
                .map(fileUri -> Utils.makeUrl(fileUri, StaticResourceConfig.FileType.LostFoundImage))
                .orElse("");
    }

    public static PublishItemResult publishFound(String userToken, ItemInfoInput itemInfo) {
        return publishItem(userToken, itemInfo, Type.Found);
    }

    public static PublishItemResult publishLost(String userToken, ItemInfoInput itemInfo) {
        return publishItem(userToken, itemInfo, Type.Lost);
    }

    private static PublishItemResult publishItem(String userToken, ItemInfoInput input, Type type) {
        logger.debug("publishItem, userToken: {}, ItemInfoInput: {}, type: {}", userToken, input, type);
        List<UndoFunction> undoOperations = new LinkedList<>();

        try {
            checkInputValidation(input);
            UserId userId = fetchUserId(userToken);

            FileId fileId = storeLostFoundImage(input.imageBase64);
            logger.debug("publishItem storeLostFoundImage, fileId: {}", fileId);

            undoOperations.add(() -> deleteLostFoundImage(fileId));

            String itemId = "";
            switch (type) {
                case Found: {
                    FoundInfo foundInfo = buildFoundInfoWith(userId.value, input, fileId);
                    logger.debug("publishItem buildFoundInfoItem, foundInfo: {}", foundInfo);

                    FoundId foundId = storeFoundInfo(foundInfo);
                    logger.debug("publishItem after storeFoundInfo, foundId: {}", foundId);
                    undoOperations.add(() -> deleteFoundInfo(foundId));

                    itemId = foundId.value;
                    break;
                }
                case Lost: {
                    LostInfo lostInfo = buildLostInfoWith(userId.value, input, fileId);
                    LostId lostId = storeLostInfo(lostInfo);
                    undoOperations.add(() -> deleteLostInfo(lostId));

                    itemId = lostId.value;
                    break;
                }
            }
            return PublishItemSuccess.of(itemId);

        } catch (AuthenticatorException | PublishException e) {
            undoOperations.forEach(UndoFunction::undo);

            logger.debug("publishItem - failed, error: {}, userToken: {}", e.getMessage(), userToken);
            return LostFoundError.of(e.getMessage());
        }
    }

    private static void checkInputValidation(ItemInfoInput input) {
        if (!itemInfoInputNonNull.validate(input)) {
            logger.debug("publishItem - failed, error: {}, ItemInfoInput: {}", "信息不完整", input);
            throw new PublishException("信息不完整");
        }
    }
    private static UserId fetchUserId(String userToken) throws AuthenticatorException {
        String userIdStr = componentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
        return UserId.of(userIdStr);
    }
    private static FileId storeLostFoundImage(String imageBase64) {
        byte[] image = Base64.getDecoder().decode(imageBase64);
        return componentFactory.lostFoundImages.createFile(image, UUID.randomUUID().toString())
                .orElseThrow(() -> {
                    logger.debug("storeLostFoundImage - failed");
                    return new PublishException("图片保存失败");
                });
    }
    private static void deleteLostFoundImage(FileId imageId) {
        componentFactory.lostFoundImages.Remove(imageId);
    }
    private static FoundInfo buildFoundInfoWith(String userId, ItemInfoInput input, FileId imageId) {
        Instant foundTime = Instant.ofEpochMilli(input.time);
        return FoundInfo.of(userId, input.itemName, foundTime, input.position, input.description, imageId.value, input.contact);
    }
    private static LostInfo buildLostInfoWith(String userId, ItemInfoInput input, FileId imageId) {
        Instant foundTime = Instant.ofEpochMilli(input.time);
        return LostInfo.of(userId, input.itemName, foundTime, input.position, input.description, imageId.value, input.contact);
    }
    private static String fetchFileURI(FileId fileId) {
        return componentFactory.lostFoundImages.fileURI(fileId).orElseThrow(() -> {
            logger.debug("buildFoundInfoWith :: fileURI - failed, imageId: {}", fileId);
            return new PublishException("找不到图片URI");
        });
    }

    private static FoundId storeFoundInfo(FoundInfo foundInfo) {
        return componentFactory.lostFound.pubFound(foundInfo).orElseThrow(() -> {
            logger.debug("storeFoundInfo - failed, foundInfo: {}", foundInfo);
            return new PublishException("发布寻物失败");
        });
    }
    private static LostId storeLostInfo(LostInfo lostInfo) {
        return componentFactory.lostFound.pubLost(lostInfo).orElseThrow(() -> {
            logger.debug("storeLostInfo - failed, foundInfo: {}", lostInfo);
            return new PublishException("发布失物失败");
        });
    }
    private static void deleteFoundInfo(FoundId foundId) {
        componentFactory.lostFound.removeFound(foundId);
    }
    private static void deleteLostInfo(LostId lostId) {
        componentFactory.lostFound.removeLost(lostId);
    }


    public static class ItemInfoInput {
        public String itemName;
        public String description;
        public String position;
        public String contact;
        public String imageBase64;
        public Long time;

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public void setImageBase64(String imageBase64) {
            this.imageBase64 = imageBase64;
        }

        @Override
        public String toString() {
            return String.format("{itemName: %s, description: %s, position: %s, contact: %s, time: %s}",
                    itemName, description, position, contact, time);
        }
    }

    public static class LostFoundError implements ItemInfoResult, PublishItemResult {
        private String error;

        public LostFoundError(String error) {
            this.error = error;
        }

        public static LostFoundError of(String error) {
            return new LostFoundError(error);
        }

        public String getError() {
            return error;
        }
    }

    public interface LostItemInfo extends ItemInfoResult {
        String getPublisher();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureUrl();
        String getContact();
        Long getCreationTime();
        Long getLostTime();
    }

    public interface FoundItemInfo extends ItemInfoResult {
        String getPublisher();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureUrl();
        String getContact();
        Long getCreationTime();
        Long getFoundTime();
    }

    public interface ItemInfoResult {
    }

    public static class PublishItemSuccess implements PublishItemResult {
        private String itemId;

        public PublishItemSuccess(String itemId) {
            this.itemId = itemId;
        }

        public static PublishItemSuccess of(String itemId) {
            return new PublishItemSuccess(itemId);
        }

        public String getItemId() {
            return itemId;
        }
    }

    public interface PublishItemResult {
    }
}
