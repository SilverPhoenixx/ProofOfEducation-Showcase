package de.phoenixrpg.proofofeducation.controller.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AProofCommand {

    private static HashMap<String, AProofCommand> commands = new HashMap<>();

    public AProofCommand() {
        commands.put(getName(), this);
    }
    public abstract String getName();

    public abstract String getDescription();
    public CommandData createCommand() {
        return Commands.slash(getName(), getDescription());
    }
    public abstract void execute(SlashCommandInteractionEvent event);


    public static List<CommandData> createCommands() {
        ArrayList<CommandData> guildCommands = new ArrayList<>();
        commands.values().forEach(phoenixCommand -> {
            guildCommands.add(phoenixCommand.createCommand());
        });

        return guildCommands;
    }

    public static void loadCommands() {
        new AddProofCommand();
        new AddUserCommand();
        new CreateProofCommand();
        new EditProofCommand();
        new ShowWeekCommand();
        new ShowDayCommand();
    }

    public static HashMap<String, AProofCommand> getCommands() {
        return commands;
    }
}
