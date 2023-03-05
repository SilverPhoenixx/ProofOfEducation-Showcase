package de.phoenixrpg.proofofeducation.controller.commands;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.LocalDate;

public class AddUserCommand extends AProofCommand {
    @Override
    public String getName() {
        return "adduser";
    }

    @Override
    public String getDescription() {
        return "Add a user with given name and year";
    }

    @Override
    public CommandData createCommand() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "name", "name of person")
                .addOption(OptionType.STRING, "year", "start from apprenticeship (dd.MM.yyyy)");
        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if(event.getOption("name") == null || event.getOption("year") == null) {
            event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("MISSING_NAME_DATE")).setEphemeral(true).queue();
            return;
        }

        LocalDate date = DateUtils.createDateFromString(event.getOption("year").getAsString());

        if(date == null) {
            event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("WRONG_DATE")).setEphemeral(true).queue();
            return;
        }

        if(!ProofOfEducation.getControllManager().getDataManager().insertUser(event.getUser().getIdLong(),
                    event.getOption("name").getAsString(), date)) {
            event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("USER_DIDNT_CREATE")).setEphemeral(true).queue();
            return;
        }

        event.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("USER_CREATE")).setEphemeral(true).queue();

    }
}
