package com.asu.tx;

import java.util.ArrayList;
import java.util.List;

public class TransactionCoordinator {

    private List<TransactionParticipant> participants = new ArrayList<>();

    public void register(TransactionParticipant p) {
        participants.add(p);
    }

    public void commit() {

        try {
            for (TransactionParticipant p : participants) {
                p.prepare();
            }

            for (TransactionParticipant p : participants) {
                p.commit();
            }

        } catch (Exception e) {
            for (TransactionParticipant p : participants) {
                p.rollback();
            }
        }
    }
}
