package hw.oauth.messages.user;

import java.util.List;

import com.google.common.collect.ImmutableList;

import hw.oauth.messages.UpdateAction;
import hw.oauth.messages.UserMessage;
import hw.oauth.messages.user.changes.AuthorityUpdate;
import hw.oauth.messages.user.changes.RoleUpdate;

public class UpdateUserMessage implements UserMessage {

    private final String userId;
    private final List<Update> updates;

    public static Builder builder(String userId) {
        return new Builder(userId);
    }

    private UpdateUserMessage(Builder builder) {
        userId = builder.userId;
        updates = builder.updates.build();
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public List<Update> getUpdates() {
        return updates;
    }

    public void visitChanges(Visitor visitor) {
        updates.stream().forEach(change -> change.visit(visitor));
    }

    public static class Builder {

        private final String userId;
        private final ImmutableList.Builder<Update> updates = ImmutableList.builder();

        public Builder(String userId) {
            this.userId = userId;
        }

        public Builder addAuthority(String authority) {
            updates.add(new AuthorityUpdate(UpdateAction.ADD, authority));
            return this;
        }

        public Builder removeAuthority(String authority) {
            updates.add(new AuthorityUpdate(UpdateAction.REMOVE, authority));
            return this;
        }

        public Builder addRole(String role) {
            updates.add(new RoleUpdate(UpdateAction.ADD, role));
            return this;
        }

        public Builder removeRole(String role) {
            updates.add(new RoleUpdate(UpdateAction.REMOVE, role));
            return this;
        }

        public UpdateUserMessage build() {
            return new UpdateUserMessage(this);
        }
    }

    public interface Visitor {

        void visitAuthority(UpdateAction action, String authority);

        void visitRole(UpdateAction action, String role);
    }

    public interface Update {

        void visit(Visitor visitor);
    }
}