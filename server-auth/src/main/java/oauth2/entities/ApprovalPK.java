/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.entities;

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
        return Objects.equals(userId, other.userId) //
                && Objects.equals(clientId, other.clientId) //
                && Objects.equals(scope, other.scope);
    }
}