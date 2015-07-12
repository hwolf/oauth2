package hw.oauth2.entities;

import java.util.Collection;

import org.springframework.data.repository.Repository;

public interface ApprovalRepository extends Repository<Approval, ApprovalPK> {

    Approval findOne(ApprovalPK key);

    Collection<Approval> findByUserIdAndClientId(String userId, String clientId);

    void save(Approval approval);

    void delete(ApprovalPK approvalPK);
}
