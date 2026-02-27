package com.ciblorgasport.notifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.ciblorgasport.notifications.models.GroupTableRow;
import com.ciblorgasport.notifications.services.DatabaseService;

class ServerTest {

    @Test
    void bootstrapIncidentGroup_creeSiAbsent() {
        Server server = new Server();
        FakeDatabaseService fakeDatabaseService = new FakeDatabaseService(
            null,
            new GroupTableRow(1L, "Incidents")
        );

        assertDoesNotThrow(() -> server.bootstrapIncidentGroup(fakeDatabaseService));
        assertEquals(1, fakeDatabaseService.getCreateCallCount());
    }

    @Test
    void bootstrapIncidentGroup_neDupliquePasSiExistant() {
        Server server = new Server();
        FakeDatabaseService fakeDatabaseService = new FakeDatabaseService(
            new GroupTableRow(1L, "Incidents"),
            new GroupTableRow(1L, "Incidents")
        );

        assertDoesNotThrow(() -> server.bootstrapIncidentGroup(fakeDatabaseService));
        assertEquals(1, fakeDatabaseService.getCreateCallCount());
    }

    private static final class FakeDatabaseService extends DatabaseService {
        private final GroupTableRow existingGroup;
        private final GroupTableRow createdGroup;
        private int createCallCount = 0;

        private FakeDatabaseService(GroupTableRow existingGroup, GroupTableRow createdGroup) {
            super(false);
            this.existingGroup = existingGroup;
            this.createdGroup = createdGroup;
        }

        @Override
        public GroupTableRow getGroupByName(String name) throws SQLException {
            return existingGroup;
        }

        @Override
        public GroupTableRow createGroupIfNotExists(String groupName) throws SQLException {
            createCallCount++;
            return createdGroup;
        }

        private int getCreateCallCount() {
            return createCallCount;
        }
    }
}
