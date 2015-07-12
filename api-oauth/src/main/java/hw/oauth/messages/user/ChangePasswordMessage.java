package hw.oauth.messages.user;

import hw.oauth.messages.UserMessage;

public class ChangePasswordMessage implements UserMessage {

    public static Builder builder(String userId) {
        return new Builder(userId);
    }

    private final String userId;
    private final String oldPassword;
    private final String newPassword;

    public ChangePasswordMessage(String userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public static class Builder {

        private final String userId;
        private String oldPassword;
        private String newPassword;

        public Builder(String userId) {
            this.userId = userId;
        }

        public Builder oldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
            return this;
        }

        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ChangePasswordMessage build() {
            return new ChangePasswordMessage(userId, oldPassword, newPassword);
        }
    }
}
