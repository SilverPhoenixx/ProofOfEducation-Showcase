package de.phoenixrpg.proofofeducation.model;

import de.phoenixrpg.proofofeducation.controller.proof.DailyProof;

import java.time.LocalDate;
import java.util.LinkedHashMap;

public interface DataManager {

    LinkedHashMap<String, DailyProof> getWeek(long discordId, LocalDate date);

     boolean insertProof(long discordId, String location, String task, LocalDate date);
     boolean updateProof(long discordId, LocalDate date, String task, String location);
     boolean deleteProof(long discordId, LocalDate date);
     boolean existProof(long discordId, LocalDate date);
     DailyProof getProofByDate(long discordId, LocalDate date);

     LocalDate getYearById(long discordId);
     String getNameById(long discordId);
     boolean insertUser(long discordId, String name, LocalDate year);
     boolean existUser(long discordId);
     int getUserByDiscordID(long discordId);

     boolean createDataholder();
}
