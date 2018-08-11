package data;

import com.google.gson.annotations.SerializedName;
import com.sun.xml.internal.bind.v2.model.core.ID;

public class Article {
    private int id, year_num, journal_id, is_isi, is_scopus, is_vci, is_international;
    private String title, slug, volume, number, year, reference, language, keyword, doi, document_type, authors_full;
    private Integer cites_count;
    private String[] citations, citations_new, citations_per_year, subjects, subjects_id;
    private Author[] authors, articles_authors_data;
    private int[] articles_authors, organizes_id, representatives_id;
    private Organization[] organizes_data;
    private Journal journal_data;
    private Representative[] representatives_data;

    @SerializedName("abstract")
    private String abstracts;

    public Article() {
    }

    public Article(int id) {
        this.id = id;
    }

    public void setJournal_id(int journal_id) {
        this.journal_id = journal_id;
    }

    public void setIs_isi(int is_isi) {
        this.is_isi = is_isi;
    }

    public void setIs_scopus(int is_scopus) {
        this.is_scopus = is_scopus;
    }

    public void setIs_vci(int is_vci) {
        this.is_vci = is_vci;
    }

    public void setIs_international(int is_international) {
        this.is_international = is_international;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
        this.year_num = Integer.valueOf(year);
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public void setCites_count(Integer cites_count) {
        this.cites_count = cites_count;
    }

    public void setCitations(String[] citations) {
        this.citations = citations;
    }

    public void setCitations_new(String[] citations_new) {
        this.citations_new = citations_new;
    }

    public void setCitations_per_year(String[] citations_per_year) {
        this.citations_per_year = citations_per_year;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }

    public void setSubjects_id(String[] subjects_id) {
        this.subjects_id = subjects_id;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;

        StringBuilder authors_full_builder = new StringBuilder();
        for (Author author : authors) {
            authors_full_builder.append(author.getName()).append(", ");
        }
        this.authors_full = authors_full_builder.substring(0, authors_full_builder.length() >= 2 ? authors_full_builder.length() - 2 : 0);    // Trim the last ", "

        int[] IDs = new int[authors.length];
        for (int i = 0; i < IDs.length; ++i) {
            IDs[i] = authors[i].getId();
        }
        this.setArticles_authors(IDs);
    }

    public void setArticles_authors_data(Author[] articles_authors_data) {
        this.articles_authors_data = articles_authors_data;
    }

    public void setArticles_authors(int[] articles_authors) {
        this.articles_authors = articles_authors;
    }

    public void setOrganizes_id(int[] organizes_id) {
        this.organizes_id = organizes_id;
    }

    public void setOrganizes_data(Organization[] organizes_data) {
        this.organizes_data = organizes_data;

        int[] IDs = new int[organizes_data.length];
        for (int i = 0; i < organizes_data.length; ++i) {
            IDs[i] = organizes_data[i].getId();
        }

        this.setOrganizes_id(IDs);
    }

    public void setJournal_data(Journal journal_data) {
        this.journal_data = journal_data;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public void setRepresentatives_id(int[] representatives_id) {
        this.representatives_id = representatives_id;
    }

    public void setRepresentatives_data(Representative[] representatives_data) {
        this.representatives_data = representatives_data;

        int[] IDs = new int[representatives_data.length];
        for (int i = 0; i < IDs.length; ++i) {
            IDs[i] = representatives_data[i].getId();
        }
        this.setRepresentatives_id(IDs);
    }
}
