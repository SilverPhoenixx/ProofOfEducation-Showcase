package de.phoenixrpg.proofofeducation.controller.listener.interactions;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import de.phoenixrpg.proofofeducation.controller.proof.DailyProof;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

public class ButtonClickListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        LocalDate date = DateUtils.createDateFromString(event.getMessage().getEmbeds().get(0).getFooter().getText());

        switch (event.getComponentId()) {
            case "printWeek" -> {
                ProofOfEducation.getControllManager().getViewManager().sendFile(event, event.getUser().getIdLong(), date);
            }
            case "addDay" -> {
                event.replyModal(ProofOfEducation.getControllManager().getViewManager().showProofModal(DateUtils.getDateAsString(date), null, null)).queue();
            }
            case "nextWeek" -> {
                LocalDate newDate = date.plusWeeks(1);
                sendWeekEmbed(event, newDate);
            }
            case "backWeek" -> {
                LocalDate newDate = date.minusWeeks(1);
                sendWeekEmbed(event, newDate);
            }
            case "editDay" -> {
                event.replyModal(ProofOfEducation.getControllManager().getViewManager().editDay(event.getUser().getIdLong(), date)).queue();
            }
            case "nextDay" -> {
                LocalDate newDate = date.plusDays(1);
                sendDayEmbed(event, newDate);
            }
            case "backDay" -> {
                LocalDate newDate = date.minusDays(1);
                sendDayEmbed(event, newDate);
            }
        }
    }

    private void sendWeekEmbed(@NotNull ButtonInteractionEvent event, LocalDate newDate) {
        newDate = newDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        HashMap<String, DailyProof> dailyproofs = ProofOfEducation.getControllManager().getDataManager().getWeek(event.getUser().getIdLong(), newDate);
        event.editMessageEmbeds(ProofOfEducation.getControllManager().getViewManager().getWeekEmbed(newDate, dailyproofs)).setActionRow(ProofOfEducation.getControllManager().getViewManager().getWeekButtons(dailyproofs.size())).queue();   }

    private void sendDayEmbed(@NotNull ButtonInteractionEvent event, LocalDate newDate) {
        newDate = DateUtils.checkWeekend(newDate);
        DailyProof dailyproof = ProofOfEducation.getControllManager().getDataManager().getProofByDate(event.getUser().getIdLong(), newDate);
        event.editMessageEmbeds(ProofOfEducation.getControllManager().getViewManager().getDayEmbed(newDate, dailyproof)).queue();
    }
}
