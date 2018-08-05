package com.oliveoyl.utils;

public class CryptoFishyCertificateInfo {

    private String regulatorBody;
    private String year;
    private String type;
    private String location;
    private String md5;
    private long timestamp;
    private String generationDate;

    public String getRegulatorBody() {
        return regulatorBody;
    }
    public void setRegulatorBody(String regulatorBody) { this.regulatorBody = regulatorBody; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getGenerationDate() { return generationDate; }
    public void setGenerationDate(String generationDate) { this.generationDate = generationDate; }
}
