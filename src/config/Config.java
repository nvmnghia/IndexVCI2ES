package config;

public class Config {
    public static final class DB {
        public static String HOST = "localhost";
        public static int PORT = 3306;

        public static String USERNAME = "root";
        public static String PASSWORD = "";

        public static String NAME = "vci_scholar";
    }

    public static final class ES {
        public static String HOST = "http://localhost";
        public static int PORT = 9200;

        public static int BULK_SIZE = 400;
        public static String BULK_INDEX_URL = Config.ES.HOST + ":" + Config.ES.PORT + "/_bulk";

        public static String INDEX = "test_nghia_0308";
    }
}
