package com.gaufoo.bbs.application.test;

import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.types.*;
import com.gaufoo.bbs.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class AccountGenerator {
    private static Random random = new Random();

    public static List<String> createAccounts(int number) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            Authentication.SignupResult result = AppAuthentication.signup(DataPool.generateSignUpInput(1).get(0));
            if (result instanceof Authentication.LoggedInToken) {
                list.add(((Authentication.LoggedInToken) result).getToken());
            }
        }
        return list;
    }

    public static void createSchoolHeats(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            SchoolHeat.SchoolHeatInput schoolHeatInput = new SchoolHeat.SchoolHeatInput();
            schoolHeatInput.title = "school heat title" + i;
            schoolHeatInput.content = DataPool.generateContentInput(1).get(0);
            return Tuple.of(i, schoolHeatInput);
        }).forEach(t -> AppSchoolHeat.createSchoolHeat(t.right, tokens.get(random.nextInt(tokens.size()))));
    }

    public static void createEntertainment(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            Entertainment.EntertainmentInput entertainInput = new Entertainment.EntertainmentInput();
            entertainInput.title = "entertainment title" + i;
            entertainInput.content = DataPool.generateContentInput(1).get(0);
            return Tuple.of(i, entertainInput);
        }).forEach(t -> AppEntertainment.createEntertainment(t.right, tokens.get(random.nextInt(tokens.size()))));
    }

    public static void createLearnResource(int number, List<String> tokens) {
        IntStream.range(0, number).mapToObj(i -> {
            LearningResource.LearningResourceInput learnResourceInput = new LearningResource.LearningResourceInput();
            learnResourceInput.title = "entertainment title" + i;
            learnResourceInput.content = DataPool.generateContentInput(1).get(0);
            return Tuple.of(i, learnResourceInput);
        }).forEach(t -> AppLearningResource.createLearningResource(t.right, tokens.get(random.nextInt(tokens.size()))));
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


}
