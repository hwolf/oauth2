package hw.oauth.messages.user.changes;

import hw.oauth.messages.UpdateAction;
import hw.oauth.messages.user.UpdateUserMessage;
import hw.oauth.messages.user.UpdateUserMessage.Visitor;

public class RoleUpdate implements UpdateUserMessage.Update {

    private final UpdateAction action;
    private final String role;

    public RoleUpdate(UpdateAction action, String role) {
        this.action = action;
        this.role = role;
    }

    public UpdateAction getAction() {
        return action;
    }

    public String getRole() {
        return role;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visitRole(action, role);
    }
}
