package CloneProject.InstagramClone.InstagramService.config;

import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpringConst {
    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*5;
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*10;
    public static final String ACCESS_SECRET_KEY = "472B4B6250655368566D597133743677397A244326462948404D635166546A57";
    public static final String REFRESH_SECRET_KEY = "28482B4D6251655468576D5A7134743677397A24432646294A404E635266556A";
    public static final String[] PERMITTED_URIS = {"/users/register","/login/failure","/access-token/re-allocation","/users/logout","/posts/comments/add","/login/success","/service","/posts/add"};
    /*public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final int AFTER_BEARER = 7;
    public static final String JWT_TOKEN_PREFIX = "Bearer ";*/
}
