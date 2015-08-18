package oauth2.entities

import org.junit.Test

import com.google.common.testing.EqualsTester

import oauth2.entities.ApprovalPK;

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
