package de.phoenixrpg.proofofeducation.controller.commands;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import de.phoenixrpg.proofofeducation.controller.proof.DailyProof;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

public class ShowWeekCommand extends AProofCommand {
    @Override
    public String getName() {
        return "showweek";
    }

    @Override
    public String getDescription() {
        return "Show your given week";
    }

    @Override
    public CommandData createCommand() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "date", "date of the week");
        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if(event.getOption("date") == null) {
            event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("MISSING_DATE")).setEphemeral(true).queue();
            return;
        }
        LocalDate date = DateUtils.createDateFromString(event.getOption("date").getAsString());
        if(date == null) {
            event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("WRONG_DATE")).setEphemeral(true).queue();
            return;
        }

        date = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        HashMap<String, DailyProof> dailyproofs = ProofOfEducation.getControllManager().getDataManager().getWeek(event.getUser().getIdLong(), date);
        event.replyEmbeds(ProofOfEducation.getControllManager().getViewManager().getWeekEmbed(date, dailyproofs))
                .setActionRow(ProofOfEducation.getControllManager().getViewManager().getWeekButtons(dailyproofs.size()))
                .setEphemeral(true).queue();
    }
}
