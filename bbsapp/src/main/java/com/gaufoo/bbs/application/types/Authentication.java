package com.gaufoo.bbs.application.types;

public interface Authentication {

    interface CurrentUserResult {}
    interface SignupResult {}
    interface LoginResult {}
    interface LogoutResult {}

    class SignupInput {
        public String nickname;
        public String password;
        public String username;

        @Override
        public String toString() {
            return "SignupInput{" +
                    "nickname='" + nickname + '\'' +
                    ", password='" + password + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    class LoginInput {
        public String password;
        public String username;

        @Override
        public String toString() {
            return "LoginInput{" +
                    "password='" + password + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    interface LoggedInToken extends LoginResult, SignupResult {
        public String getToken();
    }
}
