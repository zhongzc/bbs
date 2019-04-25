package com.gaufoo.bbs.application.types;

public interface PersonalInformation {
    interface PersonalInfo extends
            PersonInfoResult,
            EditPersonInfoResult,
            Authentication.CurrentUserResult
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

        @Override
        public String toString() {
            return "PersonInfoInput{" +
                    "introduction='" + introduction + '\'' +
                    ", major='" + major + '\'' +
                    ", school='" + school + '\'' +
                    ", grade='" + grade + '\'' +
                    ", gender='" + gender + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    interface PersonInfoResult {}
    interface EditPersonInfoResult {}
}
