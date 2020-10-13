package fr.kinderrkill.launcher.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties properties = null;

    public static String get(String key) {
        if(properties == null) {
            properties = new Properties();
            try {
                InputStream is = Config.class.getResourceAsStream("/Launcher.properties");
                properties.load(is);
                is.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return properties != null ? properties.getProperty(key) : null;
    }
}
