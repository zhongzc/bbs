package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class LostAndFound {
    private static Logger logger = LoggerFactory.getLogger(LostAndFound.class);

    //lostId
    public static ItemInfoResult lostItem(String lostId) {
        logger.debug("lostItem, lostId: {}", lostId);
        return componentFactory.lostFound.lostInfo(LostId.of(lostId))
                .map(lostInfo -> (ItemInfoResult) constructItemInfo(lostInfo))
                .orElseGet(() -> {
                    logger.debug("lostItem - failed, error: {}, lostId: {}", "找不到失物", lostId);
                    return ItemInfoError.of("");
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
                return componentFactory.lostFoundImages.fileURI(FileId.of(lostInfo.imageIdentifier))
                        .orElse("");
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
                    return ItemInfoError.of("");
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
                return componentFactory.lostFoundImages.fileURI(FileId.of(foundInfo.imageIdentifier))
                        .orElse("");
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

    public static ItemInfoResult publishFound(String userToken, ItemInfoInput itemInfo) {
        logger.debug("publishFound, userToken: {}, ItemInfoInput: {}", userToken, itemInfo);
        try {
            String userId = componentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;

            byte[] image = Base64.getDecoder().decode(itemInfo.imageBase64);
            return componentFactory.lostFoundImages.createFile(image, UUID.randomUUID().toString())
                    .flatMap(componentFactory.lostFoundImages::fileURI)
                    .map(imageUri ->
                            FoundInfo.of(userId, itemInfo.itemName, Instant.ofEpochMilli(itemInfo.time),
                            itemInfo.position, itemInfo.description, imageUri, itemInfo.contact))
                    .flatMap(componentFactory.lostFound::pubFound)
                    .flatMap(componentFactory.lostFound::foundInfo)
                    .map(foundInfo -> (ItemInfoResult)constructItemInfo(foundInfo))
                    .orElseGet(() -> {
                        logger.debug("publishFound - failed, userToken: {}, ItemInfoInput: {}", userToken, itemInfo);
                        return ItemInfoError.of("寻物发布失败");
                    });
        } catch (AuthenticatorException e) {
            logger.debug("publishFound - failed, error: {}, userToken: {}, ItemInfoInput: {}", e.getMessage(), userToken, itemInfo);
            return ItemInfoError.of(e.getMessage());
        }
    }

    public static ItemInfoResult publishLost(String userToken, ItemInfoInput itemInfo) {
        logger.debug("publishLost, userToken: {}, ItemInfoInput: {}", userToken, itemInfo);
        try {
            String userId = componentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;

            byte[] image = Base64.getDecoder().decode(itemInfo.imageBase64);
            return componentFactory.lostFoundImages.createFile(image, UUID.randomUUID().toString())
                    .flatMap(componentFactory.lostFoundImages::fileURI)
                    .map(imageUri ->
                            LostInfo.of(userId, itemInfo.itemName, Instant.ofEpochMilli(itemInfo.time),
                                    itemInfo.position, itemInfo.description, imageUri, itemInfo.contact))
                    .flatMap(componentFactory.lostFound::pubLost)
                    .flatMap(componentFactory.lostFound::lostInfo)
                    .map(lostInfo -> (ItemInfoResult)constructItemInfo(lostInfo))
                    .orElseGet(() -> {
                        logger.debug("publishLost - failed, userToken: {}, ItemInfoInput: {}", userToken, itemInfo);
                        return ItemInfoError.of("失物发布失败");
                    });
        } catch (AuthenticatorException e) {
            logger.debug("publishLost - failed, error: {}, userToken: {}, ItemInfoInput: {}", e.getMessage(), userToken, itemInfo);
            return ItemInfoError.of(e.getMessage());
        }
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

    public static class ItemInfoError implements ItemInfoResult {
        private String error;

        public ItemInfoError(String error) {
            this.error = error;
        }

        public static ItemInfoError of(String error) {
            return new ItemInfoError(error);
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
        Long getCreationTime();
        Long getLostTime();
    }

    public interface FoundItemInfo extends ItemInfoResult {
        String getPublisher();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureUrl();
        Long getCreationTime();
        Long getFoundTime();
    }

    public interface ItemInfoResult {
    }
}
