package hw.oauth2.authentication.approvals;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

import hw.oauth2.entities.ApprovalPK;
import hw.oauth2.entities.ApprovalRepository;

public class ApprovalServiceImpl implements ApprovalStore {

    private final ApprovalRepository approvalRepository;

    public ApprovalServiceImpl(hw.oauth2.entities.ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    @Override
    public List<Approval> getApprovals(String userId, String clientId) {
        return approvalRepository.findByUserIdAndClientId(userId.toLowerCase(), clientId) //
                .stream() //
                .map(this::mapApproval) //
                .collect(Collectors.toList());
    }

    private Approval mapApproval(hw.oauth2.entities.Approval entity) {
        return new Approval(entity.getUserId(), entity.getClientId(), entity.getScope(),
                Date.from(entity.getExpiresAt()), entity.getStatus(), Date.from(entity.getLastUpdateAt()));
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        approvals.stream().forEach(this::addApproval);
        return true;
    }

    private void addApproval(Approval approval) {
        hw.oauth2.entities.Approval entity = approvalRepository
                .findOne(new ApprovalPK(approval.getUserId(), approval.getClientId(), approval.getScope()));
        if (entity == null) {
            entity = new hw.oauth2.entities.Approval();
            entity.setUserId(approval.getUserId());
            entity.setClientId(approval.getClientId());
            entity.setScope(approval.getScope());
        }
        entity.setStatus(approval.getStatus());
        entity.setExpiresAt(approval.getExpiresAt().toInstant());
        entity.setLastUpdateAt(approval.getLastUpdatedAt().toInstant());
        approvalRepository.save(entity);
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        approvals.stream().forEach(this::revokeApproval);
        return true;
    }

    private void revokeApproval(Approval approval) {
        approvalRepository.delete(new ApprovalPK(approval.getUserId(), approval.getClientId(), approval.getScope()));
    }
}
