package indexer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import config.Config;
import data.*;
import org.apache.commons.cli.*;
import util.DataUtl;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Indexer {

    private static Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private static Gson gsonUgly = new Gson();

    public static void main(String[] args) throws SQLException, IOException, ParseException, IllegalAccessException {
        parseArg(args);

        createIndexWithMappings();

        indexArticles();
        indexOrganizations();
        indexJournal();
    }

    private static void parseArg(String[] args) throws ParseException, IllegalAccessException {
        Options options = new Options();

        String[] opts = {"db_host", "db_port", "db_name", "db_user", "db_pass", "es_host", "es_port", "es_index", "es_bulk_size"};
        for (String opt : opts) {
            Option option = new Option(opt, true, opt);
            options.addOption(option);
        }

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Config.DB.HOST = cmd.getOptionValue("db_host", Config.DB.NAME);
        Config.DB.PORT = Integer.parseInt(cmd.getOptionValue("db_port", String.valueOf(Config.DB.PORT)));
        Config.DB.USERNAME = cmd.getOptionValue("db_user", Config.DB.USERNAME);
        Config.DB.PASSWORD = cmd.getOptionValue("db_pass", Config.DB.PASSWORD);
        Config.DB.NAME = cmd.getOptionValue("db_name", Config.DB.NAME);

        Config.ES.HOST = cmd.getOptionValue("es_host", Config.ES.HOST);
        Config.ES.PORT = Integer.parseInt(cmd.getOptionValue("es_port", String.valueOf(Config.ES.PORT)));
        Config.ES.INDEX = cmd.getOptionValue("es_index", Config.ES.INDEX);
        Config.ES.BULK_SIZE = Integer.parseInt(cmd.getOptionValue("es_bulk_size", String.valueOf(Config.ES.BULK_SIZE)));

        Field[] fields = Config.class.getDeclaredFields();
        Config temp = new Config();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                System.out.println(f.getName() + ": " + f.get(temp).toString() );
            }
        }

    }

    private static void createIndexWithMappings() throws IOException {
        File jsonMappings = new File("mappings.json");
        BufferedReader reader = new BufferedReader(new FileReader(jsonMappings));

        String line;
        StringBuilder mappings = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            mappings.append(line);
        }
        System.out.println(mappings.toString());

        byte[] putData = mappings.toString().getBytes(StandardCharsets.UTF_8);
        URL url = new URL(Config.ES.HOST + ":" + Config.ES.PORT + "/" + Config.ES.INDEX);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/x-ndjson");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(putData.length));
        conn.setDoOutput(true);
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.write(putData);
            out.flush();
        }

        System.out.println("Create index: Response code: " + conn.getResponseCode() + "     " + conn.getResponseMessage());
    }

    /**
     * Index articles into ES
     * First, for each articles, get all of its author IDs, organization IDs and representative IDs
     * Then, iterate through all those
     *
     * @throws SQLException
     * @throws IOException
     */
    public static void indexArticles() throws SQLException, IOException {
        String query = "SELECT ar.*, GROUP_CONCAT(DISTINCT ao.author_id SEPARATOR ', '), GROUP_CONCAT(DISTINCT ao.organize_id SEPARATOR ', ') FROM articles ar " +
                "JOIN articles_authors aa ON aa.article_id = ar.id " +
                "JOIN authors_organizes ao ON ao.author_id = aa.author_id " +
                "JOIN organizes org ON org.id = ao.organize_id " +
                "GROUP BY ar.id";
        ResultSet rs = DataUtl.queryDB(Config.DB.NAME, query);

        StringBuilder output = new StringBuilder();
        String[] temp = new String[0];

        int counter = 0;
        while (rs.next()) {
            int articleID = rs.getInt(1);
            Article article = new Article(articleID);

            int journalID = rs.getInt(17);
            Journal journal = Journal.getJournals(journalID);
            article.setJournal_id(journalID);
            article.setJournal_data(journal);

            Organization[] organizations = Organization.getOrganizations(rs.getString(40));
            article.setOrganizes_data(organizations);

            Map<Integer, Organization> mapOrganizations = new HashMap<Integer, Organization>();
            for (Organization organization : organizations) {
                mapOrganizations.put(organization.getId(), organization);
            }

            Author[] authors = Author.getAuthors(rs.getString(39), mapOrganizations);
            article.setAuthors(authors);
            article.setArticles_authors_data(authors);

            article.setRepresentatives_data(Representative.getRepresentatives(rs.getString(40)));

            article.setTitle(rs.getString(2));
            article.setSlug(rs.getString(28));
            article.setAbstracts(rs.getString(3));
            article.setVolume(rs.getString(5));
            article.setNumber(rs.getString(6));
            article.setYear(rs.getString(7));
            article.setReference(rs.getString(11));
            article.setCites_count(rs.getInt(15));
            article.setLanguage(rs.getString(19));
            article.setKeyword(rs.getString(20));
            article.setDoi(rs.getString(27));
            article.setDocument_type(rs.getString(32));
            article.setIs_scopus(rs.getInt(33));
            article.setIs_isi(rs.getInt(34));
            article.setIs_vci(rs.getInt(35));
            article.setIs_international(rs.getInt(31));
            article.setCitations(temp);
            article.setCitations_new(temp);
            article.setCitations_per_year(temp);
            article.setSubjects(temp);
            article.setSubjects_id(temp);

            output.append("{ \"index\" : { \"_index\" : \"" + Config.ES.INDEX + "\", \"_type\" : \"article\", \"_id\" : \"" + articleID + "\" } }\n");
            output.append(gsonUgly.toJson(article));
            output.append('\n');

            if (++counter % Config.ES.BULK_SIZE == 0) {
                putBulk(output.toString());

                output = new StringBuilder();
            }
        }

        // POST the remainder
        putBulk(output.toString());

        System.out.println("Done indexing " + counter + " articles");
    }

    public static void indexOrganizations() throws SQLException, IOException {
        String query = "SELECT id, name, name_en, address, _lft, _rgt, parent_id FROM organizes";
        ResultSet rs = DataUtl.queryDB(Config.DB.NAME, query);
        StringBuilder output = new StringBuilder();

        int counter = 0;
        while (rs.next()) {
            Organization organization = new Organization(rs.getInt(1));
            organization.setName(rs.getString(2));
            organization.setName_en(rs.getString(3));
            organization.setAddress(rs.getString(4));
            organization.set_lft(rs.getInt(5));
            organization.set_rgt(rs.getInt(6));
            organization.setParent_id(rs.getInt(7));
            organization.setFullname(rs.getString(2));

            output.append("{ \"index\" : { \"_index\" : \"" + Config.ES.INDEX + "\", \"_type\" : \"organize\", \"_id\" : \"" + organization.getId() + "\" } }\n");
            output.append(gsonUgly.toJson(organization));
            output.append('\n');

            if (++counter % Config.ES.BULK_SIZE == 0) {
                putBulk(output.toString());

                output = new StringBuilder();
            }
        }

        // POST the remainder
        putBulk(output.toString());

        System.out.println("Done indexing " + counter + " organizations");
    }

    public static void indexJournal() throws SQLException, IOException {
        String query = "SELECT name, name_en, description, slug, website, address, issn, proprietor, is_scopus, is_isi, is_vci, type, is_international, id FROM journals";
        ResultSet rs = DataUtl.queryDB(Config.DB.NAME, query);
        StringBuilder output = new StringBuilder();

        int counter = 0;
        while (rs.next()) {
            Journal journal = new Journal();
            journal.setName(rs.getString(1));
            journal.setName_en(rs.getString(2));
            journal.setDescription(rs.getString(3));
            journal.setSlug(rs.getString(4));
            journal.setWebsite(rs.getString(5));
            journal.setAddress(rs.getString(6));
            journal.setIssn(rs.getString(7));
            journal.setProprietor(rs.getString(8));
            journal.setIs_scopus(rs.getInt(9));
            journal.setIs_isi(rs.getInt(10));
            journal.setIs_vci(rs.getInt(11));
            journal.setType(rs.getString(12));
            journal.setIs_international(rs.getInt(13));

            output.append("{ \"index\" : { \"_index\" : \"" + Config.ES.INDEX + "\", \"_type\" : \"journal\", \"_id\" : \"" + rs.getInt(14) + "\" } }\n");
            output.append(gsonUgly.toJson(journal));
            output.append('\n');

            if (++counter % Config.ES.BULK_SIZE == 0) {
                putBulk(output.toString());

                output = new StringBuilder();
            }
        }

        // POST the remainder
        putBulk(output.toString());

        System.out.println("Done indexing " + counter + " journals");
    }

    /**
     * POST vs PUT: POST can be executed without id
     * @param data
     * @throws IOException
     */
    private static void putBulk(String data) throws IOException {
        byte[] postData = data.getBytes(StandardCharsets.UTF_8);
        URL url = new URL(Config.ES.BULK_INDEX_URL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/x-ndjson");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setDoOutput(true);
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.write(postData);
            out.flush();
        }

        System.out.println("Bulk index: Response code: " + conn.getResponseCode() + "    " + conn.getResponseMessage());
    }
}
