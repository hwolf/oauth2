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
package oauth2.authentication.approvals

import java.time.Instant

import oauth2.entities.ApprovalRepository

import org.springframework.security.oauth2.provider.approval.Approval
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus

import spock.lang.Specification

class ApprovalServiceImplSpec extends Specification {

    def "getApprovals: When search for an approval then return approval"() {
        given:
        ApprovalRepository repository = Stub() {
            findByUserIdAndClientId(_, _) >> {
                [new oauth2.entities.Approval()]
            }
        }

        when:
        List<Approval> approvals = new ApprovalServiceImpl(repository).getApprovals("A user id", "A client id")

        then:
        approvals.size == 1
    }

    def "addApprovals: When add approval then the entity should saved"() {

        given:
        ApprovalRepository repository = Mock() { findOne(_) >> null }

        when:
        new ApprovalServiceImpl(repository).addApprovals([new Approval(null, null, null, null, null)])

        then:
        1 * repository.save(_)
    }

    def "fromEntity/toEntity: All properties should mapped"() {

        given:
        def expected = new oauth2.entities.Approval(userId: "A user id", clientId: "A client id", scope: "A scope", lastUpdateAt: Instant.ofEpochMilli(1L),        status: ApprovalStatus.APPROVED, expiresAt: Instant.ofEpochMilli(2L))

        when:
        def actual = ApprovalServiceImpl.toEntity(ApprovalServiceImpl.fromEntity(expected))

        then:
        expected.userId == actual.userId
        expected.clientId == actual.clientId
        expected.scope == actual.scope
        expected.status == actual.status
        expected.expiresAt == actual.expiresAt
        expected.lastUpdateAt == actual.lastUpdateAt
    }

    def "revokeApprovals: When revoke an approval then the entity should deleted"() {

        given:
        ApprovalRepository repository = Mock()

        when:
        new ApprovalServiceImpl(repository).revokeApprovals([new Approval(null, null, null, null, null)])

        then:
        1 * repository.delete(_)
    }
}
