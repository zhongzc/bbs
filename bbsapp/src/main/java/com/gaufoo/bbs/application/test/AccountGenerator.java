package com.gaufoo.bbs.application.test;

import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.types.*;
import com.gaufoo.bbs.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AccountGenerator {
    private static Random random = new Random();

    public static List<String> createAccounts(int number) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            Authentication.SignupResult result = AppAuthentication.signup(DataPool.generateSignUpInput(1).get(0));
            if (result instanceof Authentication.LoggedInToken) {
                String token = ((Authentication.LoggedInToken) result).getToken();
                list.add(token);
                AppPersonalInformation.editPersonInfo(DataPool.generatePersonInfo(), token);
            }
        }
        return list;
    }

    public static void createSchoolHeats(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            SchoolHeat.SchoolHeatInput schoolHeatInput = new SchoolHeat.SchoolHeatInput();
            schoolHeatInput.title = DataPool.generateTitle();
            schoolHeatInput.content = DataPool.generateContentInput(5);
            return Tuple.of(i, schoolHeatInput);
        }).forEach(t -> {
            SchoolHeat.CreateSchoolHeatResult schoolHeat = AppSchoolHeat.createSchoolHeat(t.right, tokens.get(random.nextInt(tokens.size())));
            String id = ((SchoolHeat.SchoolHeatInfo) schoolHeat).getId();
            IntStream.range(0, random.nextInt(5) + 1).forEach(i -> {
                SchoolHeat.SchoolHeatCommentInput input = new SchoolHeat.SchoolHeatCommentInput();
                input.content = DataPool.generateContentInput(2);
                input.postIdCommenting = id;
                AppSchoolHeat.createSchoolHeatComment(input, tokens.get(random.nextInt(tokens.size())));
            });
        });
    }

    public static void createEntertainment(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            Entertainment.EntertainmentInput entertainInput = new Entertainment.EntertainmentInput();
            entertainInput.title = DataPool.generateTitle();
            entertainInput.content = DataPool.generateContentInput(5);
            return Tuple.of(i, entertainInput);
        }).forEach(t -> {
            Entertainment.CreateEntertainmentResult entertainment = AppEntertainment.createEntertainment(t.right, tokens.get(random.nextInt(tokens.size())));
            String id = ((Entertainment.EntertainmentInfo) entertainment).getId();
            IntStream.range(0, random.nextInt(5) + 1).forEach(i -> {
                Entertainment.EntertainmentCommentInput input = new Entertainment.EntertainmentCommentInput();
                input.content = DataPool.generateContentInput(2);
                input.postIdCommenting = id;
                AppEntertainment.createEntertainmentComment(input, tokens.get(random.nextInt(tokens.size())));
            });
        });
    }

    public static void createLearnResource(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            LearningResource.LearningResourceInput learnResourceInput = new LearningResource.LearningResourceInput();
            learnResourceInput.title = DataPool.generateTitle();
            learnResourceInput.content = DataPool.generateContentInput(5);
            learnResourceInput.course = AppPersonalInformation.allCourses().get(random.nextInt(AppPersonalInformation.allCourses().size()));
            return Tuple.of(i, learnResourceInput);
        }).forEach(t -> {
            LearningResource.CreateLearningResourceResult learningResource = AppLearningResource.createLearningResource(t.right, tokens.get(random.nextInt(tokens.size())));
            String id = ((LearningResource.LearningResourceInfo) learningResource).getId();
            IntStream.range(0, random.nextInt(5) + 1).forEach(i -> {
                LearningResource.LearningResourceCommentInput input = new LearningResource.LearningResourceCommentInput();
                input.content = DataPool.generateContentInput(2);
                input.postIdCommenting = id;
                AppLearningResource.createLearningResourceComment(input, tokens.get(random.nextInt(tokens.size())));
            });
        });
    }

    public static void createLost(int number, List<String> tokens) {
        DataPool.generateLostInput(number).forEach(input -> {
            AppLost.createLost(input, tokens.get(random.nextInt(tokens.size())));
        });
    }

    public static void createFound(int number, List<String> tokens) {
        DataPool.generateFoundInput(number).forEach(input -> {
            AppFound.createFound(input, tokens.get(random.nextInt(tokens.size())));
        });
    }

    public static void createLectures(int number, String admin) {
        DataPool.generateLectureInput(number).forEach(l -> {
            AppLecture.createLecture(l, admin);
        });
    }

    public static void createNews(String admin) {
        DataPool.newsInput.forEach(n -> {
            AppNews.createNews(n, admin);
        });
    }
}
