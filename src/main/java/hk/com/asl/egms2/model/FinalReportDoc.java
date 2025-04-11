package hk.com.asl.egms2.model;

import java.io.Serializable;
import java.util.Date;

public class FinalReportDoc implements Serializable {
    private Attachment attachment;
    private String grant_no;

    // No-argument constructor
    public FinalReportDoc() {}

    public FinalReportDoc(Attachment attachment, String grantNo) {
        this.attachment = attachment;
        this.grant_no = grantNo;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public String getGrant_no() {
        return grant_no;
    }

    public void setGrant_no(String grantNo) {
        this.grant_no = grantNo;
    }
}

class Attachment {
    private Date date;
    private String content_type;
    private String author;
    private String format;
    private Date modified;
    private String language;
    private Date metadata_date;
    private String title;
    private String creator_tool;
    private String content;
    private int content_length;

    

    // No-argument constructor
    public Attachment() {}

    public Attachment(Date date, String contentType, String format, Date modified, String language, Date metadataDate, String content, int contentLength) {
        this.date = date;
        this.content_type = contentType;
        this.format = format;
        this.modified = modified;
        this.language = language;
        this.metadata_date = metadataDate;
        this.content = content;
        this.content_length = contentLength;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getMetadata_date() {
        return metadata_date;
    }

    public void setMetadata_date(Date metadata_date) {
        this.metadata_date = metadata_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator_tool() {
        return creator_tool;
    }

    public void setCreator_tool(String creator_tool) {
        this.creator_tool = creator_tool;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContent_length() {
        return content_length;
    }

    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    

}
