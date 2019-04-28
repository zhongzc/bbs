package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.LearningResource;
import com.gaufoo.bbs.application.types.Lecture;
import com.gaufoo.bbs.application.types.Lost;
import com.gaufoo.bbs.application.types.SchoolHeat;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

import static com.gaufoo.bbs.application.types.Authentication.CurrentUserResult;
import static com.gaufoo.bbs.application.types.Found.AllFoundsResult;
import static com.gaufoo.bbs.application.types.Found.FoundInfoResult;
import static com.gaufoo.bbs.application.types.PersonalInformation.PersonInfoResult;
import static com.gaufoo.bbs.application.types.Lost.*;

public class Query implements GraphQLQueryResolver {
    public PersonInfoResult personInfo(String id) {
        return PersonalInformation.personInfo(id);
    }

    public List<String> allMajors() {
        return PersonalInformation.allMajors();
    }

    public List<String> allSchools() {
        return PersonalInformation.allSchools();
    }

    public List<String> allCourses() {
        return PersonalInformation.allCourses();
    }

    public List<String> majorsIn(String school) {
        return PersonalInformation.majorsIn(school);
    }

    public CurrentUserResult currentUser(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                Authentication::currentUser
        ).orElse(authError);
    }

    public AllFoundsResult allFounds(Long skip, Long first) {
        return AppFound.allFounds(skip, first);
    }

    public FoundInfoResult foundInfo(String id) {
        return AppFound.foundInfo(id);
    }

    public AllLostsResult allLosts(Long skip, Long first) {
        return AppLost.allLosts(skip, first);
    }

    public LostInfoResult lostInfo(String id) {
        return AppLost.lostInfo(id);
    }

    public SchoolHeat.AllSchoolHeatsResult allSchoolHeats(Long skip, Long first, Commons.SortedBy sortedBy) {
        return AppSchoolHeat.allSchoolHeats(skip, first, sortedBy);
    }

    public SchoolHeat.SchoolHeatsOfAuthorResult schoolHeatsOfAuthor(String id, Long skip, Long first) {
        return AppSchoolHeat.schoolHeatsOfAuthor(id, skip, first);
    }

    public SchoolHeat.SchoolHeatInfoResult schoolHeatInfo(String id) {
        return AppSchoolHeat.schoolHeatInfo(id);
    }

    public Lecture.AllLecturesResult allLectures(Long skip, Long first) {
        return AppLecture.allLectures(skip, first);
    }

    public Lecture.LectureInfoResult lectureInfo(String lectureId) {
        return AppLecture.lectureInfo(lectureId);
    }

    public LearningResource.AllLearningResourceResult allLearningResources(Long skip, Long first, String course, Commons.SortedBy sortedBy) {
        return AppLearningResource.allLearningResources(skip, first, course, sortedBy);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
