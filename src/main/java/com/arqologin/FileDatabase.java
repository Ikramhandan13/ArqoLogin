package com.arqologin;

import java.io.File;

public class FileDatabase {
    public static void ensureParentDirectory(String path) {
        File file = new File(path);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }
}
