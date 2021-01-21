package fr.kinderrkill.launcher.utils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    public static int width, height;
    private static Properties properties = null;

    public static void init() {
        width = getInt("WINDOW_WIDTH");
        height = getInt("WINDOW_HEIGHT");
    }

    public static String get(String key) {
        if (properties == null) {
            properties = new Properties();
            try {
                InputStream is = Config.class.getResourceAsStream("/Launcher.properties");
                properties.load(is);
                is.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            String result = properties.getProperty(key);
            if (result.startsWith("%") && result.endsWith("%")) {
                String var = result.replaceAll("%", "");
                String varValue = get(var);
                if (varValue != null) {
                    return varValue;
                }
            }
        }
        return properties != null ? properties.getProperty(key) : null;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static Color getColor(String key) {
        return Color.decode(get(key));
    }

    private static int[] getRGB(String key) {
        int[] ret = new int[3];
        for (int i = 0; i < 3; i++) {
            ret[i] = hexToInt(get(key).charAt(i * 2), get(key).charAt(i * 2 + 1));
        }
        return ret;
    }

    private static int hexToInt(char a, char b) {
        int x = a < 65 ? a - 48 : a - 55;
        int y = b < 65 ? b - 48 : b - 55;
        return x * 16 + y;
    }

    public static int getRelativeInt(String key) {
        if (get(key).endsWith("%w%")) {
            return (int) ((float) width / 100f * (float) Integer.parseInt(get(key).replaceAll("%w%", "")));
        } else if (get(key).endsWith("%h%")) {
            return (int)
                    ((float) height / 100f * (float) Integer.parseInt(get(key).replaceAll("%h%", "")));
        } else {
            return getInt(key);
        }
    }

    public static Alignment getAlignment(String key) {
        String value = get(key);
        return Alignment.valueOf(value);
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }
}