package de.phoenixrpg.proofofeducation.controller.listener.interactions;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;


public class ModalInteractListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().contains("proof")) {
            try {
                String dateString = event.getValue("date").getAsString();
                String location = event.getValue("location").getAsString();
                String task = event.getValue("task").getAsString();
                LocalDate date = DateUtils.createDateFromString(dateString);

                if(date == null) {
                    event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("WRONG_DATE")).setEphemeral(true).queue();
                    return;
                }

                if(ProofOfEducation.getControllManager().getDataManager().insertProof(event.getUser().getIdLong(), location, task, date)) {
                    event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("PROOF_ADDED").replace("%DATE", dateString)).setEphemeral(true).queue();
                } else {
                    event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("PROOF_NOT_ADDED").replace("%DATE", dateString)).setEphemeral(true).queue();
                }
            } catch (NumberFormatException ex) {
                event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("PROOF_ADDED_ERROR")).setEphemeral(true).queue();
            }
        }
    }
}
