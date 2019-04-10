package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.learningResource.common.ResourceId;
import com.gaufoo.bbs.components.learningResource.common.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
/*
    还没测试
 */
public class LearnResource {
    private  static Logger logger=LoggerFactory.getLogger(LearnResource.class);
    public static LearnResourceInfoResult searchResource(String resourceId){
        logger.debug("searchResource,resourceId:{}",resourceId);
        return componentFactory.learnResource.resourceInfo(ResourceId.of(resourceId))
                .map(resourceInfo -> (LearnResourceInfoResult)constructLearnResourceInfo(resourceInfo))
                .orElseGet(()->{
                    logger.debug("searchResource-failed,error:{},resourceId:{}","找不到资源",resourceId);
                    return LearnResourceInfoError.of("");
                });
    }

    public static LearnResourceInfoResult publishLearnResource(String userToken,LearnResourceInput resourceInfoInput){
        logger.debug("publishLearnResource,userToken:{},learnResourceInput:{}",userToken,resourceInfoInput);
        try{
            String userId=componentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;

            return Optional.of(ResourceInfo.of(resourceInfoInput.sharer,
                    resourceInfoInput.majorCode,resourceInfoInput.title,resourceInfoInput.content,resourceInfoInput.attachedFileIdentifier,
                    Instant.ofEpochMilli(resourceInfoInput.time))).flatMap(componentFactory.learnResource::pubResource)
                    .flatMap(componentFactory.learnResource::resourceInfo)
                    .map(resourceInfo -> (LearnResourceInfoResult)constructLearnResourceInfo(resourceInfo))
                    .orElseGet(()->{
                        logger.debug("publishLearnResource - failed, userToken: {}, resourceInfoInput: {}",userToken,resourceInfoInput);
                        return LearnResourceInfoError.of("发布学习资源失败");
                    });
        }catch(AuthenticatorException e){
            logger.debug("publishLearnResource - failed,userToken: {}, learnResourceInput: {}",userToken,resourceInfoInput);
            return LearnResourceInfoError.of(e.getMessage());
        }
    }
 //   public static LearnResourceInfoResult searchResource()

    public static LearnResourceInfo constructLearnResourceInfo(ResourceInfo resourceInfo){
        return new LearnResourceInfo() {
            @Override
            public String getSharer() { return resourceInfo.sharer; }
            @Override
            public String getMajorCode() { return resourceInfo.majorCode; }
            @Override
            public String getTitle() { return resourceInfo.title; }
            @Override
            public String getContent() { return resourceInfo.content; }
            @Override
            public String getAttachedFileIdentifier() { return resourceInfo.attachedFileIdentifier; }
            @Override
            public Long getCreationTime() { return resourceInfo.createTime.toEpochMilli(); }
        };
    }

    public static class LearnResourceInput{
        public String sharer;
        public String majorCode;
        public String title;
        public String content;
        public String attachedFileIdentifier;
        public Long time;
        public void setSharer(String sharer) {
            this.sharer = sharer;
        }

        public void setMajorCode(String majorCode) {
            this.majorCode = majorCode;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setAttachedFileIdentifier(String attachedFileIdentifier) {
            this.attachedFileIdentifier = attachedFileIdentifier;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "LearnResourceInput{" +
                    "sharer='" + sharer + '\'' +
                    ", majorCode='" + majorCode + '\'' +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", attachedFileIdentifier='" + attachedFileIdentifier + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    public static class LearnResourceInfoError implements  LearnResourceInfoResult{
        private String error;
        public LearnResourceInfoError(String error){this.error=error;}
        public static LearnResourceInfoError of(String error){return new LearnResourceInfoError(error);}
        public String getError(){return error;}
    }
    public interface  LearnResourceInfo extends  LearnResourceInfoResult{
        String getSharer();
        String getMajorCode();
        String getTitle();
        String getContent();
        String getAttachedFileIdentifier();
        Long getCreationTime();
    }
    public interface  LearnResourceInfoResult{

    }
}