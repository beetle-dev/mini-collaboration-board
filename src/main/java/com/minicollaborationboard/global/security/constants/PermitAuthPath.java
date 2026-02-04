package com.minicollaborationboard.global.security.constants;

import java.util.List;

public final class PermitAuthPath {

    public static final String LOGIN = "/login";
    public static final String SIGNUP = "/signup";

    public static final List<String> permitAuthPath = List.of("/", LOGIN, SIGNUP);
}
