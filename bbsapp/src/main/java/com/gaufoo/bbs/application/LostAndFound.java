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
import java.util.stream.Collectors;

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
    private static class ModifyException extends RuntimeException {
        ModifyException(String errMsg) {
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
                    input.time != null && input.time > 0
    );

    public static LostInfoResult lostInfoResult(String lostId) {
        logger.debug("lostInfoResult, lostId: {}", lostId);
        return componentFactory.lostFound.lostInfo(LostId.of(lostId))
                .map(i -> (LostInfoResult) constructItemInfo(LostId.of(lostId), i))
                .orElseGet(() -> {
                    logger.debug("lostInfoResult - failed, error: {}, lostId: {}", "找不到失物", lostId);
                    return LostFoundError.of("找不到失物");
                });
    }

    private static LostItemInfo constructItemInfo(LostId id, LostInfo lostInfo) {
        return new LostItemInfo() {
            public PersonalInformation.PersonalInfo getPublisher() {
                return PersonalInformation.personalInfo(UserId.of(lostInfo.publisher))
                        .orElse(null);
            }
            public String getId()           { return id.value;                                      }
            public String getName()         { return lostInfo.objName;                              }
            public String getDescription()  { return lostInfo.description;                          }
            public String getPosition()     { return lostInfo.position;                             }
            public String getPictureUrl()   { return factorOutPictureUrl(lostInfo.imageIdentifier); }
            public String getContact()      { return lostInfo.contact;                              }
            public Long   getCreationTime() { return lostInfo.createTime.toEpochMilli();            }
            public Long   getLostTime()     { return lostInfo.lostTime.toEpochMilli();              }
        };
    }

    public static FoundInfoResult foundInfoResult(String foundId) {
        logger.debug("foundInfoResult, foundId: {}", foundId);
        return componentFactory.lostFound.foundInfo(FoundId.of(foundId))
                .map(foundInfo -> (FoundInfoResult) constructItemInfo(FoundId.of(foundId), foundInfo))
                .orElseGet(() -> {
                    logger.debug("foundInfoResult - failed, error: {}, foundId: {}", "找不到寻物", foundId);
                    return LostFoundError.of("");
                });
    }

    private static FoundItemInfo constructItemInfo(FoundId id, FoundInfo foundInfo) {
        return new FoundItemInfo() {
            public PersonalInformation.PersonalInfo getPublisher() {
                return PersonalInformation.personalInfo(UserId.of(foundInfo.publisher))
                        .orElse(null);
            }
            public String getId()           { return id.value;                                       }
            public String getName()         { return foundInfo.objName;                              }
            public String getDescription()  { return foundInfo.description;                          }
            public String getPosition()     { return foundInfo.position;                             }
            public String getPictureUrl()   { return factorOutPictureUrl(foundInfo.imageIdentifier); }
            public String getContact()      { return foundInfo.contact;                              }
            public Long   getCreationTime() { return foundInfo.createTime.toEpochMilli();            }
            public Long   getFoundTime()    { return foundInfo.foundTime.toEpochMilli();             }
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

    private static LostItemInfo lazyConsLostItemInfo(LostId lostId) {
        return new LostItemInfo() {
            private LostItemInfo info = null;
            private LostItemInfo getInfo() {
                if (info != null) return info;
                info = componentFactory.lostFound.lostInfo(lostId)
                        .map(i -> LostAndFound.constructItemInfo(lostId, i)).get();
                return info;
            }
            public PersonalInformation.PersonalInfo getPublisher()    { return getInfo().getPublisher();    }
            public String                           getId()           { return getInfo().getId();           }
            public String                           getName()         { return getInfo().getName();         }
            public String                           getDescription()  { return getInfo().getDescription();  }
            public String                           getPosition()     { return getInfo().getPosition();     }
            public String                           getPictureUrl()   { return getInfo().getPictureUrl();   }
            public String                           getContact()      { return getInfo().getContact();      }
            public Long                             getCreationTime() { return getInfo().getCreationTime(); }
            public Long                             getLostTime()     { return getInfo().getLostTime();     }
        };
    }

    private static FoundItemInfo lazyConsFoundItemInfo(FoundId foundId) {
        return new FoundItemInfo() {
            private FoundItemInfo info = null;
            private FoundItemInfo getInfo() {
                if (info != null) return info;
                info = componentFactory.lostFound.foundInfo(foundId)
                        .map(i -> LostAndFound.constructItemInfo(foundId, i)).get();
                return info;
            }
            public PersonalInformation.PersonalInfo getPublisher()    { return getInfo().getPublisher();    }
            public String                           getId()           { return getInfo().getId();           }
            public String                           getName()         { return getInfo().getName();         }
            public String                           getDescription()  { return getInfo().getDescription();  }
            public String                           getPosition()     { return getInfo().getPosition();     }
            public String                           getPictureUrl()   { return getInfo().getPictureUrl();   }
            public String                           getContact()      { return getInfo().getContact();      }
            public Long                             getCreationTime() { return getInfo().getCreationTime(); }
            public Long                             getFoundTime()    { return getInfo().getFoundTime();    }
        };
    }

    public static PublishFoundResult publishFound(String userToken, ItemInfoInput itemInfo) {
        return (PublishFoundResult) publishItem(userToken, itemInfo, Type.Found);
    }

    public static PublishLostResult publishLost(String userToken, ItemInfoInput itemInfo) {
        return (PublishLostResult) publishItem(userToken, itemInfo, Type.Lost);
    }

    private static Object publishItem(String userToken, ItemInfoInput input, Type type) {
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
            return (type.equals(Type.Found)) ? foundInfoResult(itemId) : lostInfoResult(itemId);

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

    public static ModifyLostResult modifyLostItem(String userToken, String itemId, ItemInfoInput input) {
        try {
            UserId userId = fetchUserId(userToken);
            checkLostPublisher(userId, itemId);

            optionallyUpdateLostItem(itemId, input);

            logger.debug("modifyLostItem - successful, userToken: {}, input: {}", userToken, input);
            return lazyConsLostItemInfo(LostId.of(itemId));
        } catch (AuthenticatorException | ModifyException e) {
            logger.debug("modifyLostItem, error: {}, input: {}", e.getMessage(), input);
            return LostFoundError.of(e.getMessage());
        }
    }

    private static void checkLostPublisher(UserId userId, String itemId) {
        componentFactory.lostFound.lostInfo(LostId.of(itemId))
                .map(lostInfo -> {
                    logger.debug("checkLostPublisher, userId: {}, publisher: {}", userId, itemId);
                    return lostInfo.publisher;
                })
                .filter(userId.value::equals)
                .orElseThrow(() -> {
                    logger.debug("checkLostPublisher - failed, userId: {}, itemId: {}", userId, itemId);
                    return new ModifyException("无更改权限");
                });
    }

    // fixme: partially updated
    private static void optionallyUpdateLostItem(String itemId, ItemInfoInput input) {
        LostId lostId = LostId.of(itemId);
        if (input.itemName != null) {
            componentFactory.lostFound.changeObjName(lostId, input.itemName);
        }
        if (input.contact != null) {
            componentFactory.lostFound.changeContact(lostId, input.contact);
        }
        if (input.time != null) {
            componentFactory.lostFound.changeLostTime(lostId, Instant.ofEpochMilli(input.time));
        }
        if (input.description != null) {
            componentFactory.lostFound.changeDescription(lostId, input.description);
        }
        if (input.position != null) {
            componentFactory.lostFound.changePosition(lostId, input.position);
        }
        if (input.imageBase64 != null) {
            String oldFileId = componentFactory.lostFound.lostInfo(lostId)
                    .map(lostInfo -> lostInfo.imageIdentifier)
                    .orElse("");
            logger.debug("optionallyUpdateLostItem oldFileId: {}", oldFileId);

            String newFileId = updateImage(oldFileId, input.imageBase64);
            componentFactory.lostFound.changeImageIdentifier(lostId, newFileId);
        }
    }

    private static String updateImage(String oldFileId, String newImageBase64) {
        componentFactory.lostFoundImages.Remove(FileId.of(oldFileId));

        byte[] image = Base64.getDecoder().decode(newImageBase64);
        return componentFactory.lostFoundImages.createFile(image, UUID.randomUUID().toString())
                .map(fileId -> fileId.value)
                .orElseThrow(() -> {
                    logger.debug("updateImage - failed, error: {}", "更新图片失败");
                    return new ModifyException("更新图片失败");
                });
    }

    public static ModifyFoundResult modifyFoundItem(String userToken, String itemId, ItemInfoInput input) {
        try {
            UserId userId = fetchUserId(userToken);
            checkFoundPublisher(userId, itemId);

            optionallyUpdateFoundItem(itemId, input);

            logger.debug("modifyFoundItem - successful, userToken: {}, input: {}", userToken, input);
            return lazyConsFoundItemInfo(FoundId.of(itemId));
        } catch (AuthenticatorException e) {
            logger.debug("modifyFoundItem, error: {}, input: {}", e.getMessage(), input);
            return LostFoundError.of(e.getMessage());
        }
    }

    private static void checkFoundPublisher(UserId userId, String itemId) {
        componentFactory.lostFound.foundInfo(FoundId.of(itemId))
                .map(foundInfo -> foundInfo.publisher)
                .filter(userId.value::equals)
                .orElseThrow(() -> {
                    logger.debug("checkFoundPublisher - failed, userId: {}, itemId: {}", userId, itemId);
                    return new ModifyException("无更改权限");
                });
    }

    private static void optionallyUpdateFoundItem(String itemId, ItemInfoInput input) {
        FoundId foundId = FoundId.of(itemId);
        if (input.description != null) {
            componentFactory.lostFound.changeDescription(foundId, input.description);
        }
        if (input.contact != null) {
            componentFactory.lostFound.changeContact(foundId, input.contact);
        }
        if (input.time != null) {
            componentFactory.lostFound.changeFoundTime(foundId, Instant.ofEpochMilli(input.time));
        }
        if (input.itemName != null) {
            componentFactory.lostFound.changeObjName(foundId, input.itemName);
        }
        if (input.position != null) {
            componentFactory.lostFound.changePosition(foundId, input.position);
        }
        if (input.imageBase64 != null) {
            String oldFileId = componentFactory.lostFound.foundInfo(foundId)
                    .map(lostInfo -> lostInfo.imageIdentifier)
                    .orElse("");
            logger.debug("optionallyUpdateFoundItem oldFileId: {}", oldFileId);

            String newFileId = updateImage(oldFileId, input.imageBase64);
            componentFactory.lostFound.changeImageIdentifier(foundId, newFileId);
        }
    }

    public static AllLostResult allLosts(Long skip, Long first) {
        logger.debug("allLosts, skip: {}, first: {}", skip, first);

        return new AllLostResult() {
            @Override
            public Long getTotalCount() {
                return componentFactory.lostFound.allLostCounts();
            }

            @Override
            public List<LostItemInfo> getLostInfos() {
                Long sk = Optional.ofNullable(skip).orElse(0L);
                Long ft = Optional.ofNullable(first).orElse(getTotalCount());

                return componentFactory.lostFound.allLosts()
                        .map(lostId -> componentFactory.lostFound.lostInfo(lostId).map(i -> constructItemInfo(lostId, i)))
                        .map(Optional::get)
                        .skip(sk).limit(ft)
                        .collect(Collectors.toList());
            }
        };
    }

    public static AllFoundResult allFounds(Long skip, Long first) {
        logger.debug("allFounds, skip: {}, first: {}", skip, first);

        return new AllFoundResult() {
            @Override
            public Long getTotalCount() {
                return componentFactory.lostFound.allFoundCounts();
            }

            @Override
            public List<FoundItemInfo> getFoundInfos() {
                Long sk = Optional.ofNullable(skip).orElse(0L);
                Long ft = Optional.ofNullable(first).orElse(getTotalCount());

                return componentFactory.lostFound.allFounds()
                        .map(foundId -> componentFactory.lostFound.foundInfo(foundId).map(i -> constructItemInfo(foundId, i)))
                        .map(Optional::get)
                        .skip(sk).limit(ft)
                        .collect(Collectors.toList());
            }
        };
    }

    // for test
    public static void reset() {
        logger.debug("reset");
        componentFactory.lostFound.allFounds().forEach(foundId -> {
            FoundInfo info = componentFactory.lostFound.foundInfo(foundId).orElse(null);
            componentFactory.lostFound.removeFound(foundId);
            if (info == null) return;
            componentFactory.lostFoundImages.Remove(FileId.of(info.imageIdentifier));
        });
        componentFactory.lostFound.allLosts().forEach(lostId -> {
            LostInfo info = componentFactory.lostFound.lostInfo(lostId).orElse(null);
            componentFactory.lostFound.removeLost(lostId);
            if (info == null) return;
            componentFactory.lostFoundImages.Remove(FileId.of(info.imageIdentifier));
        });
    }

    public static class ItemInfoInput {
        String itemName;
        String description;
        String position;
        String contact;
        String imageBase64;
        Long time;

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

    public static class LostFoundError implements LostInfoResult, FoundInfoResult , PublishFoundResult, PublishLostResult, ModifyFoundResult, ModifyLostResult {
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

    public interface LostItemInfo extends LostInfoResult, PublishLostResult, ModifyLostResult {
        PersonalInformation.PersonalInfo getPublisher();
        String getId();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureUrl();
        String getContact();
        Long   getCreationTime();
        Long   getLostTime();
    }

    public interface FoundItemInfo extends FoundInfoResult, PublishFoundResult, ModifyFoundResult {
        PersonalInformation.PersonalInfo getPublisher();
        String getId();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureUrl();
        String getContact();
        Long   getCreationTime();
        Long   getFoundTime();
    }

    public interface LostInfoResult     { }
    public interface FoundInfoResult    { }
    public interface PublishFoundResult { }
    public interface PublishLostResult  { }
    public interface ModifyFoundResult  { }
    public interface ModifyLostResult   { }

    public interface AllLostResult {
        Long getTotalCount();
        List<LostItemInfo> getLostInfos();
    }

    public interface AllFoundResult {
        Long getTotalCount();
        List<FoundItemInfo> getFoundInfos();
    }
}
