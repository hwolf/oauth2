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

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;

import lombok.Getter;
import lombok.Setter;
import oauth2.entities.converters.InstantConverter;

@Entity
@Table(name = "t_approvals")
@IdClass(ApprovalPK.class)
@Getter
@Setter
public class Approval {

    @Id
    private String userId;

    @Id
    private String clientId;

    @Id
    private String scope;

    @Column(nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant lastUpdateAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant expiresAt;
}
