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

    private String serverAddress = "http://localhost";
    @Value("${server.port}")
    private String serverPort;

    @Value("${user-profile-images-mapping}")
    private String profileImgMapping;

    @Value("${lost-and-found-images-mapping}")
    private String lostFoundMapping;

    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    PersonalInformation.PersonalInfoResult userInfo(String userId) {
        return PersonalInformation.userInfo(userId,
                Utils.urlMakerProducer(String.format("%s:%s%s", serverAddress, serverPort, profileImgMapping)));
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

    LostAndFound.ItemInfoResult foundItemInfo(String foundId) {
        return LostAndFound.foundItem(foundId);
    }

    LostAndFound.ItemInfoResult lostItemInfo(String lostId) {
        return LostAndFound.lostItem(lostId);
    }
}
