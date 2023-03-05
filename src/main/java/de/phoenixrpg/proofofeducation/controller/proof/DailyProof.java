package de.phoenixrpg.proofofeducation.controller.proof;

import java.time.LocalDate;
import java.util.Date;

public class DailyProof {

    private String task;
    private String location;
    private LocalDate proofDate;

    public DailyProof(String task, String location, LocalDate proofDate) {
        this.task = task;
        this.location = location;
        this.proofDate = proofDate;
    }


    public String getLocation() {
        return location;
    }

    public String getTask() {
        return task;
    }

    public LocalDate getProofDate() {
        return proofDate;
    }
}
