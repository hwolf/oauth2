package hw.oauth2.entities;

import java.io.Serializable;
import java.util.Objects;

public final class ApprovalPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String clientId;
    private String scope;

    ApprovalPK() {
    }

    public ApprovalPK(String userId, String clientId, String scope) {
        this.userId = userId;
        this.clientId = clientId;
        this.scope = scope;
    }

    public String getUserId() {
        return userId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, clientId, scope);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ApprovalPK other = (ApprovalPK) obj;
        return Objects.equals(userId, other.userId) && Objects.equals(clientId, other.clientId)
                && Objects.equals(scope, other.scope);
    }
}