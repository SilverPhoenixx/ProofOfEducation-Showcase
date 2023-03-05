package de.phoenixrpg.proofofeducation.controller.listener.guild;

import de.phoenixrpg.proofofeducation.controller.commands.AProofCommand;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildReadyListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(AProofCommand.createCommands()).queue();
    }
}
