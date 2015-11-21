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
package oauth2.authentication.approvals;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

import oauth2.entities.ApprovalPK;
import oauth2.entities.ApprovalRepository;

public class ApprovalServiceImpl implements ApprovalStore {

    private final ApprovalRepository approvalRepository;

    public ApprovalServiceImpl(oauth2.entities.ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    @Override
    public List<Approval> getApprovals(String userId, String clientId) {
        return approvalRepository.findByUserIdAndClientId(userId, clientId) //
                .stream() //
                .map(ApprovalServiceImpl::fromEntity) //
                .collect(Collectors.toList());
    }

    static Approval fromEntity(oauth2.entities.Approval entity) {
        return new Approval(entity.getUserId(), entity.getClientId(), entity.getScope(), toDate(entity.getExpiresAt()),
                entity.getStatus(), toDate(entity.getLastUpdateAt()));
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        approvals.stream() //
                .map(ApprovalServiceImpl::toEntity) //
                .forEach(entity -> approvalRepository.save(entity));
        return true;
    }

    static oauth2.entities.Approval toEntity(Approval approval) {
        oauth2.entities.Approval entity = new oauth2.entities.Approval();
        entity.setUserId(approval.getUserId());
        entity.setClientId(approval.getClientId());
        entity.setScope(approval.getScope());
        entity.setStatus(approval.getStatus());
        entity.setExpiresAt(toInstant(approval.getExpiresAt()));
        entity.setLastUpdateAt(toInstant(approval.getLastUpdatedAt()));
        return entity;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        approvals.stream() //
                .map(ApprovalServiceImpl::toPrimaryKey) //
                .forEach(pk -> approvalRepository.delete(pk));
        return true;
    }

    static ApprovalPK toPrimaryKey(Approval approval) {
        return new ApprovalPK(approval.getUserId(), approval.getClientId(), approval.getScope());
    }

    private static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    private static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }
}
