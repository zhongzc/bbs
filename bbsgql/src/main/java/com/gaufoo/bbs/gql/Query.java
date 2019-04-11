package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.LostAndFound;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.NetworkInterface;
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

    List<LostAndFound.LostItemInfo> losts(int skip, int first) {
        return LostAndFound.losts(skip, first);
    }

    List<LostAndFound.FoundItemInfo> founds(int skip, int first) {
        return LostAndFound.founds(skip, first);
    }

    LostAndFound.ItemInfoResult foundItemInfo(String foundId) {
        return LostAndFound.foundItem(foundId);
    }

    LostAndFound.ItemInfoResult lostItemInfo(String lostId) {
        return LostAndFound.lostItem(lostId);
    }
}
