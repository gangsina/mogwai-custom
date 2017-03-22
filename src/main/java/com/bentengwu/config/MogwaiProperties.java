package com.bentengwu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author <a href="bentengwu@163.com">thender.xu</a>
 * @Date 2017/3/21 21:25.
 */
public abstract class MogwaiProperties {

    protected final static Logger logger = LoggerFactory.getLogger(MogwaiProperties.class);

    public static final String CONFIG_RESOURCE = "/de/erdesignerng/view-config.properties";

    private static Properties PROPERTIES = null;

    public static String getXMLEncoding() {
        return get("XMLEncoding");
    }

    static{
        load(CONFIG_RESOURCE);
    }

    /**
     * reload the config for tool;
     *
     * when config is change, please, call this method.
     */
    public static final void reLoad() {
        try {
            load(CONFIG_RESOURCE);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }

    /**
     * Loading files to Properties object
     * @param fileName
     */
    private static synchronized void load(String fileName){
        if(PROPERTIES ==null)
            PROPERTIES = new Properties();//Create a Properties object
        /**
         * Get properties file attributes for the current value
         * of the inter-class compiled bytecode
         * in file list of files of the documents
         * called fileName input stream
         */
        InputStream in = MogwaiProperties.class.getResourceAsStream(fileName);
        try {
            PROPERTIES.load(in);//Load the file Properties to flow to the object
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Through the attribute name to get attribute values
     * @param key
     * @return
     */
    public static String get(String key){
        return PROPERTIES.getProperty(key);//Through the key specific get attribute value
    }

    /**
     * Find the value with key, and change the type to Integer
     * @param key
     * @return The Integer value;
     */
    public static Integer getInt(String key) {
        String value = get(key);
        return Integer.parseInt(value);
    }
}
