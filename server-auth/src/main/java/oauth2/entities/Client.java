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

import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_clients")
@Getter
@Setter
public class Client {

    @Id
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    @ElementCollection
    @CollectionTable(name = "t_client_entries", joinColumns = @JoinColumn(name = "client_id") )
    @Setter(AccessLevel.NONE)
    private Collection<Entry> entries;

    private Integer accessTokenValidity;
    private Integer refreshTokenValidity;

    public Collection<Entry> getEntries() {
        if (entries == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(entries);
    }

    public void addEntry(String name, String value) {
        if (entries == null) {
            entries = Sets.newHashSet();
        }
        entries.add(Entry.create(name, value));
    }

    public void removeEntry(String name, String value) {
        if (entries == null) {
            return;
        }
        entries.remove(Entry.create(name, value));
    }
}
