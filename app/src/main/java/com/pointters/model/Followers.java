package com.pointters.model;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class Followers {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String companyName;
    private boolean isMutualFollow = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isMutualFollow() {
        return isMutualFollow;
    }

    public void setMutualFollow(boolean mutualFollow) {
        isMutualFollow = mutualFollow;
    }
}
