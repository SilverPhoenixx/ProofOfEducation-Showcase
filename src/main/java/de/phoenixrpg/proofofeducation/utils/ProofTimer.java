package de.phoenixrpg.proofofeducation.utils;

import de.phoenixrpg.proofofeducation.ProofOfEducation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class ProofTimer extends Thread {

    @Override
    public void run() {
        try {

            while (true) {
                Thread.sleep(1000 * 60);
                LocalDateTime date = LocalDateTime.now();
                if(date.getHour() == 18 && date.getMinute() == 0) {
                    ProofOfEducation.getControllManager().getViewManager().sendDailyNotificaction();
                }

                if(date.getDayOfWeek() == DayOfWeek.FRIDAY && date.getHour() == 20 && date.getMinute() == 0) {
                    ProofOfEducation.getControllManager().getViewManager().sendFridayNotification();
                }
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
