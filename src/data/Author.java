package data;

import config.Config;
import indexer.Indexer;
import util.DataUtl;
import util.StringUtl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Author {
    private int id, organize_id, citation_self_author = 0;
    private String name, email;

    public Author() {
    }

    public Author(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setOrganize_id(int organize_id) {
        this.organize_id = organize_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Index authors of an article, given a list of authors' IDs and a map of organization
     * The organization ID in this object is the ID of the first organization to have isVN true
     * Therefore a list of organization is needed, avoiding subsequent query just to get organizations' information
     * But iterating through a list is painfully slow, so...
     */
    private static Connection connSelectAuthors = null;
    private static Statement stmSelectAuthors = null;
    public static Author[] getAuthors(String listAuthorIDs, Map<Integer, Organization> mapOrganizations) throws SQLException {
        if (stmSelectAuthors == null) {
            if (connSelectAuthors == null) {
                connSelectAuthors = DataUtl.createNewConnection();
            }

            stmSelectAuthors = connSelectAuthors.createStatement();
        }

        int[] authorIDs = StringUtl.listStrToIntArr(listAuthorIDs);
        Author[] authors = new Author[authorIDs.length];

        String query = "SELECT au.id, au.name, au.email, GROUP_CONCAT(DISTINCT ao.organize_id SEPARATOR ', ') FROM authors au " +
                "JOIN authors_organizes ao ON ao.author_id = au.id " +
                "WHERE au.id IN (" + listAuthorIDs + ") " +
                "GROUP BY au.id";
        ResultSet rs = DataUtl.queryDB(stmSelectAuthors, Config.DB.NAME, query);

        int i = 0;
        while (rs.next()) {
            Author author = new Author(rs.getInt(1));
            author.setName(rs.getString(2));
            author.setEmail(rs.getString(3));
            author.setOrganize_id(getFirstVietnamOrganization(StringUtl.listStrToIntArr(rs.getString(4)), mapOrganizations));

            authors[i++] = author;
        }

        return authors;
    }

    /**
     * Given an array of organization IDs for one author, and a list (a map in fact) of organizations for the whole article,
     * returns the first organization to have isVN true
     * The reason for using a map was stated in the previous comment
     *
     * @param organizationIDs array of organization IDs for ONE author
     * @param mapOrganizations a map (organizationID to Organization) of organizations for the whole article
     * @return
     */
    public static int getFirstVietnamOrganization(int[] organizationIDs, Map<Integer, Organization> mapOrganizations) {
        for (int organizationID : organizationIDs) {
            try {
                if (mapOrganizations.get(organizationID).isVN()) {
                    return organizationID;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return organizationIDs[0];
    }
}
