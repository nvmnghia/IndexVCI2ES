package data;

import config.Config;
import util.DataUtl;
import util.StringUtl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Organization {
    private int id, _rgt, _lft;
    private String name, name_en, address, fullname;
    private Integer parent_id = null;

    // Wow wow very tran-si-ent
    private transient boolean isVN = false;

    public boolean isVN() {
        return isVN;
    }

    public Organization(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int get_rgt() {
        return _rgt;
    }

    public void set_rgt(int _rgt) {
        this._rgt = _rgt;
    }

    public int get_lft() {
        return _lft;
    }

    public void set_lft(int _lft) {
        this._lft = _lft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        String temp = name.toLowerCase();
        if (temp.contains("vietnam") || temp.contains("việt nam")) {
            this.isVN = true;
        }
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;

        String temp = name_en.toLowerCase();
        if (temp.contains("vietnam") || temp.contains("việt nam")) {
            this.isVN = true;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    private static Connection connSelectOrganizations = null;
    private static Statement stmSelectOrganizations = null;
    public static Organization[] getOrganizations(String listOrganizationIDs) throws SQLException {
        if (stmSelectOrganizations == null) {
            if (connSelectOrganizations == null) {
                connSelectOrganizations = DataUtl.createNewConnection();
            }

            stmSelectOrganizations = connSelectOrganizations.createStatement();
        }

        int[] organizationIDs = StringUtl.listStrToIntArr(listOrganizationIDs);
        Organization[] organizations = new Organization[organizationIDs.length];

        String query = "SELECT id, name, name_en, address, _lft, _rgt FROM organizes WHERE id IN (" + listOrganizationIDs + ")";
        ResultSet rs = DataUtl.queryDB(stmSelectOrganizations, Config.DB.NAME, query);

        int i = 0;
        while (rs.next()) {
            Organization organization = new Organization(rs.getInt(1));
            organization.setName(rs.getString(2));
            organization.setName_en(rs.getString(3));
            organization.setAddress(rs.getString(4));
            organization.set_lft(rs.getInt(5));
            organization.set_rgt(rs.getInt(6));

            organizations[i++] = organization;
        }

        return organizations;
    }
}
