package com.youcode.test.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityHelpers {
    public static String retrieveUsername(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal != null && principal instanceof UserDetails){
            System.out.println("USERNAME IS " +((UserDetails) principal).getUsername());
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
