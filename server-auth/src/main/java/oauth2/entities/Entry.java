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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.common.collect.ImmutableSet;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Entry {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    public static Entry create(String name, String value) {
        Entry entry = new Entry();
        entry.setName(name);
        entry.setData(value);
        return entry;
    }

    public static Set<String> filterEntriesByName(String name, Collection<Entry> entries) {
        if (entries == null) {
            return ImmutableSet.of();
        }
        return entries.stream() //
                .filter(entry -> Objects.equals(name, entry.getName())) //
                .map(entry -> entry.getData()) //
                .collect(Collectors.toSet());
    }
}
