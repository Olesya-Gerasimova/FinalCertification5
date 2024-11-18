package properties;

import java.io.FileInputStream;
import java.util.Properties;

public class GetProperties {

    protected static Properties PROPERTIES;

    static {
        try {
            PROPERTIES = new Properties();
            PROPERTIES.load(new FileInputStream("src/test/resources/conf.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return PROPERTIES.getProperty(key);
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }
}
