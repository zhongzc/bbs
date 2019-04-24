package com.gaufoo.bbs.application.types;

public interface PersonalInformation {
    interface PersonalInfo extends
            PersonInfoResult,
            EditPersonInfoResult,
            Authentication.CurrentUserResult,
            Authentication.SignupResult,
            Authentication.LoginResult
    {
        String getIntroduction();
        String getMajor();
        String getSchool();
        String getGrade();
        String getGender();
        String getUsername();
        String getPictureUrl();
        String getUserId();
    }

    class PersonInfoInput {
        public String pictureBase64;
        public String introduction;
        public String major;
        public String school;
        public String grade;
        public String gender;
        public String username;
    }

    interface PersonInfoResult {}
    interface EditPersonInfoResult {}
}
