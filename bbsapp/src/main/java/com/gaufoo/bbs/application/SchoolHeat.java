package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.content.common.ContentElem;
import com.gaufoo.bbs.components.content.common.ContentFig;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.content.common.ContentParag;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;
import com.gaufoo.bbs.util.TaskChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.SchoolHeat.*;

public class SchoolHeat {
    public static Logger log = LoggerFactory.getLogger(SchoolHeat.class);

//    public static AllSchoolHeatsResult allSchoolHeats(Long skip, Long first) {
//        final long fSkip = skip == null ? 0L : skip;
//        final long fFirst = first == null ? Long.MAX_VALUE : first;
//
//        return null;
//    }
//
//    public static CreateSchoolHeatResult createSchoolHeat(SchoolHeatInput input, String loginToken) {
//        Commons.fetchUserId(UserToken.of(loginToken))
//                .then(userId -> TaskChain.Procedure.fromOptional(componentFactory.commentGroup.cons(), ErrorCode.EEEE))
//                .then(cgId -> TaskChain.Procedure.fromOptional(componentFactory.content.cons(AppContent.consContent(input.content)), ErrorCode.EEEE, componentFactory.commentGroup.removeComments(cgId)))
//                .then(ctId -> )
//
//    }


}
