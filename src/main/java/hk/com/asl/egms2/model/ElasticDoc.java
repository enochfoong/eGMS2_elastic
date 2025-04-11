package hk.com.asl.egms2.model;

public class ElasticDoc<SourceType> {
    private String id;
    private SourceType source;

    // No-argument constructor
    public ElasticDoc() {}
    public ElasticDoc(String id, SourceType source) {
        this.id = id;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SourceType getSource() {
        return source;
    }

    public void setSource(SourceType source) {
        this.source = source;
    }
}
