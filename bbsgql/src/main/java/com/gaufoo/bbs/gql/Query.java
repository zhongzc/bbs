package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.LearnResource;
import com.gaufoo.bbs.application.LostAndFound;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.SchoolHeats;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Query implements GraphQLQueryResolver {

    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    PersonalInformation.PersonalInfoResult userInfo(String userId) {
        return PersonalInformation.userInfo(userId);
    }
    List<String> allAcademies() {
        return PersonalInformation.allAcademies();
    }
    List<String> allMajors() {
        return PersonalInformation.allMajors();
    }
    PersonalInformation.MajorsInResult majorsIn(String academy) {
        return PersonalInformation.majorsIn(academy);
    }


    Authentication.GetIdResult loggedId(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(Authentication::getLoggedUserId)
                .orElse(Authentication.GetIdError.of("用户未登录"));
    }

    LostAndFound.AllLostResult allLosts(Long skip, Long first) {
        return LostAndFound.allLosts(skip, first);
    }

    LostAndFound.AllFoundResult allFounds(Long skip, Long first) {
        return LostAndFound.allFounds(skip, first);
    }

    LostAndFound.FoundInfoResult foundItemInfo(String foundId) {
        return LostAndFound.foundInfoResult(foundId);
    }

    LostAndFound.LostInfoResult lostItemInfo(String lostId) {
        return LostAndFound.lostInfoResult(lostId);
    }

    SchoolHeats.AllPostResult allPosts(Long skip, Long first, SchoolHeats.SortedBy sortedBy) {
        return SchoolHeats.allPosts(skip, first, sortedBy);
    }

    SchoolHeats.PostInfoResult postInfo(String postId) {
        return SchoolHeats.postInfo(postId);
    }

    LearnResource.LearnResourceInfoResult searchResource(String resourceId){
        return LearnResource.searchResource(resourceId);
    }
}
