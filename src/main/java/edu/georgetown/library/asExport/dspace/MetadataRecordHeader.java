package edu.georgetown.library.asExport.dspace;

public enum MetadataRecordHeader {
    ID("id"),
    COLL("collection"),
    IDOTHER("dc.identifier.other[en]"),
    TITLE("dc.title[en]"),
    RELURI("dc.relation.uri[en]"),
    TYPE("dc.type[en]"),
    CREATOR("dc.creator[en]"),
    AUTHOR("dc.contributor.author[en]"),
    DATE("dc.date.created[en]"),
    RIGHTS("dc.rights[en]"),
    DESC("dc.description[en]"),
    SUBJ("dc.subject[en]");
    
    private String header;
    MetadataRecordHeader(String h) {
        header = h;
    }
    public String getHeader(){
        return header;
    }
}
