package hw.oauth.messages.user;

import hw.oauth.messages.UserMessage;

public class DeleteUserMessage implements UserMessage {

    // Builder not very useful, only to have unified API.
    public static Builder builder(String userId) {
        return new Builder(userId);
    }

    private final String userId;

    public DeleteUserMessage(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public static class Builder {

        private final String userId;

        public Builder(String userId) {
            this.userId = userId;
        }

        public DeleteUserMessage build() {
            return new DeleteUserMessage(userId);
        }
    }
}
