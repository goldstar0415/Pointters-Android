package com.pointters.model.response;

/**
 * Created by prashantkumar on 25/8/17.
 */

public class ResponseBySendOtpApi {
    private String tempPassword;
    private String resetPasswordExpires;

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getResetPasswordExpires() {
        return resetPasswordExpires;
    }

    public void setResetPasswordExpires(String resetPasswordExpires) {
        this.resetPasswordExpires = resetPasswordExpires;
    }
}
