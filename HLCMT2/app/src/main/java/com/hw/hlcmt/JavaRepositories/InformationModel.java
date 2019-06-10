package com.hw.hlcmt.JavaRepositories;

public class InformationModel {
    private String dateUpdated;
    private String Information;

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getInformation() {
        return Information;
    }

    public void setInformation(String information) {
        Information = information;
    }

    public InformationModel() {
    }

    public InformationModel(String dateUpdated, String information) {
        this.dateUpdated = dateUpdated;
        Information = information;
    }
}
