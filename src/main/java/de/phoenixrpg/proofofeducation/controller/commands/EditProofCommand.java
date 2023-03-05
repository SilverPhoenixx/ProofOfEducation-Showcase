package de.phoenixrpg.proofofeducation.controller.commands;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.LocalDate;

public class EditProofCommand extends AProofCommand {
    @Override
    public String getName() {
        return "editproof";
    }

    @Override
    public String getDescription() {
        return "Edit an proof by given date";
    }

    @Override
    public CommandData createCommand() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "date", "date of week");
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


        event.replyModal(ProofOfEducation.getControllManager().getViewManager().editDay(event.getUser().getIdLong(), date)).queue();
    }
}
