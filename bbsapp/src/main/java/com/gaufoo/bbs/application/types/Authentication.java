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
    }

    class LoginInput {
        public String password;
        public String username;
    }
}
