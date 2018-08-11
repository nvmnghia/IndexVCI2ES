package data;

import config.Config;
import util.DataUtl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Representative {
    private int id;
    private String name;

    public Representative(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    private static Connection connSelectRepresentative = null;
    private static Statement stmSelectRepresentative = null;
    public static Representative[] getRepresentatives(String listOrganizationIDs) throws SQLException {
        if (stmSelectRepresentative == null) {
            if (connSelectRepresentative == null) {
                connSelectRepresentative = DataUtl.createNewConnection();
            }

            stmSelectRepresentative = connSelectRepresentative.createStatement();
        }

        List<Representative> representatives = new ArrayList<>();

        String query = "SELECT rep.id, rep.name FROM representatives rep " +
                "JOIN organize_representative org_rep ON org_rep.representative_id = rep.id " +
                "JOIN organizes org ON org.id = org_rep.organize_id " +
                "WHERE org.id IN (" + listOrganizationIDs + ") " +
                "GROUP BY rep.id";
        ResultSet rs = DataUtl.queryDB(stmSelectRepresentative, Config.DB.NAME, query);

        int i = 0;
        while (rs.next()) {
            representatives.add(new Representative(rs.getInt(1), rs.getString(2)));
        }

        return representatives.toArray(new Representative[0]);
    }
}
