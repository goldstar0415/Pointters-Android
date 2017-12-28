package com.pointters.model.request;

import com.pointters.model.Media;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class UserPutRequest {
    private String companyName;
    private String description;
    private String firstName;
    private String lastName;
    private LocationRequestModel location;
    private String phone;
    private String profilePic;
    private String education;
    private String insurance;
    private String license;
    private String awards;
    private ArrayList<Media> profileBackgroundMedia;
    private Boolean completedRegistration;
    private String completedRegistrationDate;

    public UserPutRequest(String companyName, String description, String firstName, String lastName, LocationRequestModel location, String phone, String profilePic, String education, String insurance, String license, String awards, ArrayList<Media> profileBackgroundMedia, Boolean completedRegistration, String completedRegistrationDate) {
        if(!companyName.isEmpty())
        this.companyName = companyName;

        if (!description.isEmpty())
        this.description = description;


        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;

        if (!phone.isEmpty())
            this.phone = phone;


        this.profilePic = profilePic;

        if (!education.isEmpty())
        this.education = education;

        if (!insurance.isEmpty())
        this.insurance = insurance;

        if (!license.isEmpty())
        this.license = license;

        if (!awards.isEmpty())
        this.awards = awards;


        this.profileBackgroundMedia = profileBackgroundMedia;

        this.completedRegistration = completedRegistration;
        this.completedRegistrationDate = completedRegistrationDate;
    }

    /*public UserPutRequest(String companyName, String description, String firstName, String lastName, LocationRequestModel location, String phone, String profilePic, String education, String insurance, String license, String awards, Boolean completedRegistration, String completedRegistrationDate) {
        if(!companyName.isEmpty())
        this.companyName = companyName;

       if (!description.isEmpty())
        this.description = description;

        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;

        if (!phone.isEmpty())
            this.phone = phone;

        this.profilePic = profilePic;

        if (!education.isEmpty())
        this.education = education;

        if (!insurance.isEmpty())
        this.insurance = insurance;

        if (!license.isEmpty())
        this.license = license;

        if (!awards.isEmpty())
        this.awards = awards;

        this.completedRegistration = completedRegistration;
        this.completedRegistrationDate = completedRegistrationDate;
    }*/

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocationRequestModel getLocation() {
        return location;
    }

    public void setLocation(LocationRequestModel location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public ArrayList<Media> getProfileBackgroundMedia() {
        return profileBackgroundMedia;
    }

    public void setProfileBackgroundMedia(ArrayList<Media> profileBackgroundMedia) {
        this.profileBackgroundMedia = profileBackgroundMedia;
    }

    public Boolean getCompletedRegistration() {
        return completedRegistration;
    }

    public void setCompletedRegistration(Boolean completedRegistration) {
        this.completedRegistration = completedRegistration;
    }

    public String getCompletedRegistrationDate() {
        return completedRegistrationDate;
    }

    public void setCompletedRegistrationDate(String completedRegistrationDate) {
        this.completedRegistrationDate = completedRegistrationDate;
    }
}
