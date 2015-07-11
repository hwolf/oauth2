package hw.oauth.messages.user.changes;

import hw.oauth.messages.UpdateAction;
import hw.oauth.messages.user.UpdateUserMessage;
import hw.oauth.messages.user.UpdateUserMessage.Visitor;

public class AuthorityUpdate implements UpdateUserMessage.Update {

    private final UpdateAction action;
    private final String authority;

    public AuthorityUpdate(UpdateAction action, String authority) {
        this.action = action;
        this.authority = authority;
    }

    public UpdateAction getAction() {
        return action;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visitAuthority(action, authority);
    }
}
