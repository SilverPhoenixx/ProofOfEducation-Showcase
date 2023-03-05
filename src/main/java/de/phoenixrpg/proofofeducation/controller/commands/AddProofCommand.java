package de.phoenixrpg.proofofeducation.controller.commands;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.controller.ControllManager;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.LocalDate;
import java.util.Date;

public class AddProofCommand extends AProofCommand {
    @Override
    public String getName() {
        return "addproof";
    }

    @Override
    public String getDescription() {
        return "Add a proof to your history";
    }

    @Override
    public CommandData createCommand() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription());
        return commandData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyModal(ProofOfEducation.getControllManager().getViewManager().showProofModal(DateUtils.getDateAsString(LocalDate.now()), null, null)).queue();
    }
}
