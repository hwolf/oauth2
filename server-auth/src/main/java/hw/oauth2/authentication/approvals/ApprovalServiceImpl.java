package hw.oauth2.authentication.approvals;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

public class ApprovalServiceImpl implements ApprovalStore {

    private static final String SQL_GET_APPROVALS = "select " //
            + "    status," //
            + "    expires_at," //
            + "    last_modified_at," //
            + "    user_id," //
            + "    client_id," //
            + "    scope " //
            + "from " //
            + "    t_approvals " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id = ?";

    private static final String SQL_REFRESH_APPROVALS = "update " //
            + "    t_approvals " //
            + "set " //
            + "    expires_at = ?," //
            + "    status = ?," //
            + "    last_modified_at = ? " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id = ? and " //
            + "    scope = ?";

    private static final String SQL_ADD_APPROVAL = "insert into "//
            + "    t_approvals " //
            + "(" //
            + "    expires_at, " //
            + "    status, " //
            + "    last_modified_at, " //
            + "    user_id, " //
            + "    client_id, " //
            + "    scope" //
            + ") values (" //
            + "    ?,?,?,?,?,?" //
            + ")";

    private static final String SQL_DELETE_APPROVAL = "delete from " //
            + "    t_approvals " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id = ? and " //
            + "    scope = ?";

    private final JdbcTemplate jdbcTemplate;

    public ApprovalServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Approval> getApprovals(String userName, String clientId) {
        return jdbcTemplate.query(SQL_GET_APPROVALS, new Object[] { userName, clientId }, (rs, rowNum) -> {
            String status = rs.getString(1);
            Date expiresAt = rs.getTimestamp(2);
            Date lastUpdatedAt = rs.getTimestamp(3);
            String user = rs.getString(4);
            String client = rs.getString(5);
            String scope = rs.getString(6);
            return new Approval(user, client, scope, expiresAt, ApprovalStatus.valueOf(status), lastUpdatedAt);
        });
    }

    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        boolean success = true;
        for (Approval approval : approvals) {
            if (!updateApproval(SQL_REFRESH_APPROVALS, approval)) {
                if (!updateApproval(SQL_ADD_APPROVAL, approval)) {
                    success = false;
                }
            }
        }
        return success;
    }

    private boolean updateApproval(final String sql, final Approval approval) {
        int refreshed = jdbcTemplate.update(sql, ps -> {
            ps.setTimestamp(1, new Timestamp(approval.getExpiresAt().getTime()));
            ps.setString(2, mapStatus(approval).toString());
            ps.setTimestamp(3, new Timestamp(approval.getLastUpdatedAt().getTime()));
            ps.setString(4, approval.getUserId());
            ps.setString(5, approval.getClientId());
            ps.setString(6, approval.getScope());
        });
        return refreshed == 1;
    }

    private ApprovalStatus mapStatus(final Approval approval) {
        ApprovalStatus status = approval.getStatus();
        if (status == null) {
            status = ApprovalStatus.APPROVED;
        }
        return status;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        boolean success = true;
        for (final Approval approval : approvals) {
            int refreshed = jdbcTemplate.update(SQL_DELETE_APPROVAL, ps -> {
                ps.setString(1, approval.getUserId());
                ps.setString(2, approval.getClientId());
                ps.setString(3, approval.getScope());
            });
            if (refreshed != 1) {
                success = false;
            }
        }
        return success;
    }
}
