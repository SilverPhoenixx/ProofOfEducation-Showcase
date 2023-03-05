package de.phoenixrpg.proofofeducation.controller.listener.interactions;
import de.phoenixrpg.proofofeducation.controller.commands.AProofCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!AProofCommand.getCommands().containsKey(event.getName())) return;
        AProofCommand command = AProofCommand.getCommands().get(event.getName());
        command.execute(event);
    }
}
