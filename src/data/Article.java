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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear_num() {
        return year_num;
    }

    public void setYear_num(int year_num) {
        this.year_num = year_num;
        this.year = String.valueOf(year_num);
    }

    public int getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(int journal_id) {
        this.journal_id = journal_id;
    }

    public int getIs_isi() {
        return is_isi;
    }

    public void setIs_isi(int is_isi) {
        this.is_isi = is_isi;
    }

    public int getIs_scopus() {
        return is_scopus;
    }

    public void setIs_scopus(int is_scopus) {
        this.is_scopus = is_scopus;
    }

    public int getIs_vci() {
        return is_vci;
    }

    public void setIs_vci(int is_vci) {
        this.is_vci = is_vci;
    }

    public int getIs_international() {
        return is_international;
    }

    public void setIs_international(int is_international) {
        this.is_international = is_international;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumber() {
        return number;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getAuthors_full() {
        return authors_full;
    }

    public void setAuthors_full(String authors_full) {
        this.authors_full = authors_full;
    }

    public Integer getCites_count() {
        return cites_count;
    }

    public void setCites_count(Integer cites_count) {
        this.cites_count = cites_count;
    }

    public String[] getCitations() {
        return citations;
    }

    public void setCitations(String[] citations) {
        this.citations = citations;
    }

    public String[] getCitations_new() {
        return citations_new;
    }

    public void setCitations_new(String[] citations_new) {
        this.citations_new = citations_new;
    }

    public String[] getCitations_per_year() {
        return citations_per_year;
    }

    public void setCitations_per_year(String[] citations_per_year) {
        this.citations_per_year = citations_per_year;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }

    public String[] getSubjects_id() {
        return subjects_id;
    }

    public void setSubjects_id(String[] subjects_id) {
        this.subjects_id = subjects_id;
    }

    public Author[] getAuthors() {
        return authors;
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

    public Author[] getArticles_authors_data() {
        return articles_authors_data;
    }

    public void setArticles_authors_data(Author[] articles_authors_data) {
        this.articles_authors_data = articles_authors_data;
    }

    public int[] getArticles_authors() {
        return articles_authors;
    }

    public void setArticles_authors(int[] articles_authors) {
        this.articles_authors = articles_authors;
    }

    public int[] getOrganizes_id() {
        return organizes_id;
    }

    public void setOrganizes_id(int[] organizes_id) {
        this.organizes_id = organizes_id;
    }

    public Organization[] getOrganizes_data() {
        return organizes_data;
    }

    public void setOrganizes_data(Organization[] organizes_data) {
        this.organizes_data = organizes_data;

        int[] IDs = new int[organizes_data.length];
        for (int i = 0; i < organizes_data.length; ++i) {
            IDs[i] = organizes_data[i].getId();
        }

        this.setOrganizes_id(IDs);
    }

    public Journal getJournal_data() {
        return journal_data;
    }

    public void setJournal_data(Journal journal_data) {
        this.journal_data = journal_data;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public int[] getRepresentatives_id() {
        return representatives_id;
    }

    public void setRepresentatives_id(int[] representatives_id) {
        this.representatives_id = representatives_id;
    }

    public Representative[] getRepresentatives_data() {
        return representatives_data;
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
