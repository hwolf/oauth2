package oauth2.controllers;

public class ChangePasswordForm {

    private final String userId;

    private String oldPassword;
    private String newPassword;
    private String newPassword2;

    public ChangePasswordForm(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    public void clearPasswords() {
        oldPassword = null;
        newPassword = null;
        newPassword2 = null;
    }
}
