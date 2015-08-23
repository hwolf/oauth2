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
package oauth2.entities

import org.junit.Test

import com.google.common.testing.EqualsTester

class ApprovalPKTests {

    @Test
    void testEqualsAndHashCode() {
        new EqualsTester()
                .addEqualityGroup(new ApprovalPK("user1", "client1", "scope1"), new ApprovalPK("user1", "client1", "scope1"))
                .addEqualityGroup(new ApprovalPK("user2", "client1", "scope1"), new ApprovalPK("user2", "client1", "scope1"))
                .addEqualityGroup(new ApprovalPK("user1", "client2", "scope1"), new ApprovalPK("user1", "client2", "scope1"))
                .addEqualityGroup(new ApprovalPK("user1", "client1", "scope2"), new ApprovalPK("user1", "client1", "scope2"))
                .testEquals()
    }
}
