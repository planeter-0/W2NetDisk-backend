package com.west2java.netdisk.util;

import com.west2java.netdisk.entity.Directory;

import java.util.ArrayList;
import java.util.List;

public class ListBuilder {
    public List<Directory> list = new ArrayList<>();
    public  int toList(Directory root) {
        if (root != null) {
            if (root.getChildren() != null) {
                List<Directory> dirs = root.getChildren();
                for (Directory dir : dirs) {
                    list.add(dir);
                    toList(dir);
                }
            } else {
                return 0;
            }
        }
        return 0;
    }
}
