package hw.oauth2.approvals;

import static org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.APPROVED;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

public class ApprovalsServiceImpl implements ApprovalStore {

    private static final String SQL_GET_APPROVALS = "select " //
            + "    expires_at," //
            + "    status," //
            + "    last_modified_at," //
            + "    user_id," //
            + "    client_id," //
            + "    scope " //
            + "from " //
            + "    t_oauth_approvals " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id  = ?";

    private static final String SQL_REFRESH_APPROVALS = "update " //
            + "    t_oauth_approvals " //
            + "set " //
            + "    expires_at = ?," //
            + "    status = ?," //
            + "    last_modified_at = ? " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id  = ? and " //
            + "    scope = ?";

    private static final String SQL_ADD_APPROVAL = "insert into " //
            + "    t_oauth_approvals (expires_at, status, last_modified_at, user_id, client_id, scope) " //
            + "values " //
            + "    (?,?,?,?,?,?)";

    private static final String SQL_DELETE_APPROVAL = "delete from " //
            + "    t_oauth_approvals " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id  = ? and " //
            + "    scope = ?";

    private final JdbcTemplate jdbc;

    public ApprovalsServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Approval> getApprovals(String userName, String clientId) {
        return jdbc.query(SQL_GET_APPROVALS, new Object[] { userName, clientId }, (rs, rowNum) -> {
            String user = rs.getString(4);
            String client = rs.getString(5);
            String scope = rs.getString(6);
            Date expiresAt = rs.getTimestamp(1);
            String status = rs.getString(2);
            Date lastUpdatedAt = rs.getTimestamp(3);
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
        int refreshed = jdbc.update(sql, ps -> {
            ps.setTimestamp(1, new Timestamp(approval.getExpiresAt().getTime()));
            ps.setString(2, (approval.getStatus() == null ? APPROVED : approval.getStatus()).toString());
            ps.setTimestamp(3, new Timestamp(approval.getLastUpdatedAt().getTime()));
            ps.setString(4, approval.getUserId());
            ps.setString(5, approval.getClientId());
            ps.setString(6, approval.getScope());
        });
        return refreshed == 1;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        boolean success = true;
        for (final Approval approval : approvals) {
            int refreshed = jdbc.update(SQL_DELETE_APPROVAL, ps -> {
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
