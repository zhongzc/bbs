package com.gaufoo.bbs.application.test;

import com.gaufoo.bbs.application.types.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DataPool {

    private static Random random = new Random();
    private static long signUpCount = 0L;
    private static long lostItemCount = 0L;
    private static List<String> base64s = new ArrayList<>();
    private static List<String> content = new ArrayList<>();
    private static List<String> postContents = new ArrayList<>();
    private static List<String> postTitles = new ArrayList<>();


    static {
        try {
            Path baseDir = Paths.get(System.getProperty("user.dir")).resolve("bbsapp").resolve("resources");

            base64s.addAll(Files.readAllLines(baseDir.resolve("profiles-bs64.txt")));
            content.addAll(Files.readAllLines(baseDir.resolve("lorem.txt")));
            postContents.addAll(Files.readAllLines(baseDir.resolve("ran-ctn.txt")));
            postTitles.addAll(Files.readAllLines(baseDir.resolve("ran-title.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Authentication.SignupInput> generateSignUpInput(int number) {
        signUpCount += 1;
        List<Authentication.SignupInput> result = new LinkedList<>();
        for (int i = 0; i < number; ++i) {
            Authentication.SignupInput input = new Authentication.SignupInput();
            input.username = "user" + signUpCount;
            input.password = "password" + signUpCount;
            input.nickname = "nickname" + signUpCount;
            result.add(input);
        }
        return result;
    }

    public static List<Content.ContentInput> generateContentInput(int number) {
        List<Content.ContentInput> result = new LinkedList<>();
        for (int i = 0; i < number; ++i) {
            Content.ContentInput input = new Content.ContentInput();
            input.elems = generateContentElemInput(5);
            result.add(input);
        }
        return result;
    }

    public static List<Content.ContentElemInput> generateContentElemInput(int number) {
        List<Content.ContentElemInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; ++i) {
            Content.ContentElemInput contentElemInput = new Content.ContentElemInput();
            if (random.nextInt(5) == 0) {
                contentElemInput.type = Content.ElemType.Picture;
                contentElemInput.str = base64s.get(random.nextInt(base64s.size()));
            } else {
                contentElemInput.type = Content.ElemType.Text;
                contentElemInput.str = content.get(random.nextInt(content.size()));
            }
            inputs.add(contentElemInput);
        }
        return inputs;
    }

    public static List<Lost.LostInput> generateLostInput(int number) {
        List<Lost.LostInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            Lost.LostInput input = new Lost.LostInput();
            input.contact = "1391234567";
            input.description = content.get(random.nextInt(content.size()));
            input.itemName = "item" + lostItemCount++;
            input.position = "1600 Amphitheatre Parkway Mountain View, CA 94043 United States";
            input.lostTime = Instant.now().toEpochMilli();
            if (random.nextInt(10) == 0) {
                input.pictureBase64 = null;
            } else {
                input.pictureBase64 = base64s.get(random.nextInt(base64s.size()));
            }
        }
        return inputs;
    }

    public static List<Found.FoundInput> generateFoundInput(int number) {
        List<Found.FoundInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            Found.FoundInput input = new Found.FoundInput();
            input.contact = "1391234567";
            input.description = content.get(random.nextInt(content.size()));
            input.itemName = "item" + lostItemCount++;
            input.position = "CA 94043 United States, 1600 Amphitheatre Parkway Mountain View";
            input.foundTime = Instant.now().toEpochMilli();
            if (random.nextInt(10) == 0) {
                input.pictureBase64 = null;
            } else {
                input.pictureBase64 = base64s.get(random.nextInt(base64s.size()));
            }
        }
        return inputs;
    }

    public static List<Lecture.LectureInput> generateLectureInput(int number) {
        List<Lecture.LectureInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            Lecture.LectureInput input = new Lecture.LectureInput();
            input.lecturer = "lecturer";
            input.note = content.get(random.nextInt(content.size()));
            input.position = "CA 94043 United States";
            input.time = Instant.now().toEpochMilli();
            input.title = "title";
            inputs.add(input);
        }

        return inputs;
    }

    public static void main(String[] args) {
        System.out.println("hello world!" + postTitles.size());
    }
}
