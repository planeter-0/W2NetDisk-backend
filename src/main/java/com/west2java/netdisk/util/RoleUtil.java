package com.west2java.netdisk.util;

import java.util.Arrays;
import java.util.List;

public class RoleUtil {
    public static List<String> getRoleList(String str){
        String[] listArr = str.split(",");
        return Arrays.asList(listArr);
    }
}
