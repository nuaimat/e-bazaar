package business.externalinterfaces;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;
import middleware.DbConfigProperties;

public class RulesConfigProperties {

    private static final String PROPERTIES = "/rulesconfig.properties";
    private static final Logger LOG = Logger.getLogger("");
    private static final String PROPS = PROPERTIES;
    public static Properties props;

    static {
        if (props == null) {
            readProps();
        }
    }

    public String getProperty(String key) {
        return props.getProperty(key);

    }

    private static void readProps() {
        readProps(PROPS);

    }

    /**
     * This method allows a client of this properties configurator to point to a
     * different location for the properties file.
     *
     * @param propsLoc
     */
    public static void readProps(String loc) {
        LOG.info("Location from which readProps will read (in RulesConfigProperties): " + loc);
        Properties ret = new Properties();
        try {
            ret.load(RulesConfigProperties.class.getResourceAsStream(loc));
        } catch (IOException e) {
            LOG.warning("Unable to read properties file for Ebazaar");
        } finally {
            props = ret;
        }

    }
}
