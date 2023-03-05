package de.phoenixrpg.proofofeducation.controller;

import de.phoenixrpg.proofofeducation.controller.commands.AProofCommand;
import de.phoenixrpg.proofofeducation.controller.listener.guild.GuildReadyListener;
import de.phoenixrpg.proofofeducation.controller.listener.interactions.ButtonClickListener;
import de.phoenixrpg.proofofeducation.controller.listener.interactions.ModalInteractListener;
import de.phoenixrpg.proofofeducation.controller.listener.interactions.SlashCommandListener;
import de.phoenixrpg.proofofeducation.model.DataManager;
import de.phoenixrpg.proofofeducation.model.database.MariaDBData;
import de.phoenixrpg.proofofeducation.utils.ProofTimer;
import de.phoenixrpg.proofofeducation.view.ViewManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class ControllManager {
    private JDA jda;

    private DataManager dataManager;
    private ViewManager viewManager;

    public ControllManager() throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("MTAxOTIxOTg3Njc1NTI3NTg2Ng.GSjZwS.5lstgTZNbA5JctniisiG2H7AXgD6pNi1T9Cs9s")
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.playing("mit Word"));

        jda = builder.build();

        viewManager = ViewManager.getViewManager();

        dataManager = MariaDBData.getDatabase("Host", 3306, "Database", "RPG", "r2F6]pD*w5c3J*wg3mt4");
        dataManager.createDataholder();


        loadListener();

        AProofCommand.loadCommands();

        ProofTimer proofTimer = new ProofTimer();
        proofTimer.start();

    }

    public void loadListener() {
        getJDA().addEventListener(new SlashCommandListener());
        getJDA().addEventListener(new ModalInteractListener());
        getJDA().addEventListener(new ButtonClickListener());


        getJDA().addEventListener(new GuildReadyListener());
    }


    public ViewManager getViewManager() {
        return viewManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public JDA getJDA() {
        return jda;
    }
}
