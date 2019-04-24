package com.gaufoo.bbs.application.types;

public interface Authentication {

    public interface CurrentUserResult {}
    public interface SignupResult {}

    class SignupInput {
        public String nickname;
        public String password;
        public String username;
    }
}
