package com.giorgi.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DynamicClassLoader extends ClassLoader {
    private final String directory;

    public DynamicClassLoader(String directory) {
        this.directory = directory;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {

            String path = directory + "/" + name.replace('.', '/') + ".class";
            File file = new File(path);
            byte[] bytes = new byte[(int) file.length()];

            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(bytes);
            }

            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Could not load class: " + name, e);
        }
    }
}
