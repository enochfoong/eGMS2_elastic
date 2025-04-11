package hk.com.asl.egms2.model;

public class ReportWithAttachment {
    private String grant_no;
    private byte[] data;

    public ReportWithAttachment() {}

    public ReportWithAttachment(String grantNo, byte[] data) {
        this.grant_no = grantNo;
        this.data = data;
    }

    public String getGrant_no() {
        return grant_no;
    }

    public void setGrant_no(String grantNo) {
        this.grant_no = grantNo;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
