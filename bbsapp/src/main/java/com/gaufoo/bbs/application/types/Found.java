package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Found {

    interface AllFoundsResult {}
    interface FoundInfoResult {}
    interface SearchFoundsResult {}
    interface CreateFoundResult {}
    interface DeleteFoundResult {}
    interface ClaimFoundResult {}

    interface FoundInfo extends
            FoundInfoResult,
            CreateFoundResult,
            DeleteFoundResult,
            ClaimFoundResult
    {
        String getId();
        PersonalInformation.PersonalInfo getPublisher();
        String getName();
        String getDescription();
        String getPosition();
        String getPictureURL();
        String getContact();
        Long getCreateTime();
        Long getFoundTime();
    }

    interface MultiFoundInfos extends
            AllFoundsResult,
            SearchFoundsResult
    {
        Long getTotalCount();
        List<FoundInfo> getFounds();
    }

    class FoundInput {
        public String itemName;
        public String description;
        public String position;
        public String pictureBase64;
        public String contact;
        public Long foundTime;
    }
}
