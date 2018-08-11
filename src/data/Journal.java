package data;

import util.DataUtl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Journal {
    private Integer id, is_scopus, is_isi, is_vci, is_international;
    private String name;
    private String name_en;
    private String type;
    private String description;

    public void setIs_international(Integer is_international) {
        this.is_international = is_international;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public void setProprietor(String proprietor) {
        this.proprietor = proprietor;
    }

    private String slug, website, address, issn, proprietor;

    public Journal() {
    }

    public Journal(Integer id) {
        this.id = id;
    }

    public void setIs_scopus(Integer is_scopus) {
        this.is_scopus = is_scopus;

        if (this.is_scopus != 0) {
            this.is_international = 1;
        }
    }

    public void setIs_isi(Integer is_isi) {
        this.is_isi = is_isi;

        if (this.is_isi != 0) {
            this.is_international = 1;
        }
    }

    public void setIs_vci(Integer is_vci) {
        this.is_vci = is_vci;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static Connection connSelectJournal = null;
    private static PreparedStatement pstmSelectJournal = null;
    public static Journal getJournals(int journalID) throws SQLException {
        if (pstmSelectJournal == null) {
            if (connSelectJournal == null) {
                connSelectJournal = DataUtl.createNewConnection();
            }

            pstmSelectJournal = connSelectJournal.prepareStatement("SELECT id, name, name_en, type, description, is_scopus, is_isi, is_vci FROM vci_scholar.journals WHERE id = ?");
        }

        Journal journal = new Journal(journalID);

        pstmSelectJournal.setInt(1, journalID);
        ResultSet rs = pstmSelectJournal.executeQuery();
        rs.next();

        journal.setName(rs.getString(2));
        journal.setName_en(rs.getString(3));
        journal.setType(rs.getString(4));
        journal.setDescription(rs.getString(5));
        journal.setIs_scopus(rs.getInt(6));
        journal.setIs_isi(rs.getInt(7));
        journal.setIs_vci(rs.getInt(8));

        return journal;
    }
}
