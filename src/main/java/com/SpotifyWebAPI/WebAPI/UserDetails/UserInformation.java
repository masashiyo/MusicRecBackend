package com.SpotifyWebAPI.WebAPI.UserDetails;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class UserInformation {
    public String getUserCode(Cookie[] cookies, String cookieValue) {
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(cookieValue)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
