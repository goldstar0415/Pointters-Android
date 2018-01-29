package com.pointters.model;

import java.util.List;

/**
 * Created by mac on 1/26/18.
 */

public class UserProfileModel {

    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String description;
    private String companyName;
    private String education;
    private String insurance;
    private String license;
    private String awards;
    private List<Media> profileBackgroundMedia;
    private String profilePic;
    private MetricsModel userMetrics;
    private Boolean verified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<Media> getProfileBackgroundMedia() {
        return profileBackgroundMedia;
    }

    public void setProfileBackgroundMedia(List<Media> profileBackgroundMedia) {
        this.profileBackgroundMedia = profileBackgroundMedia;
    }

    public MetricsModel getUserMetrics() {
        return userMetrics;
    }

    public void setUserMetrics(MetricsModel userMetrics) {
        this.userMetrics = userMetrics;
    }
}
