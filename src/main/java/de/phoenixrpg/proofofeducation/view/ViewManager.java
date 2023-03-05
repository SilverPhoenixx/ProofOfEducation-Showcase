package de.phoenixrpg.proofofeducation.view;

import de.phoenixrpg.proofofeducation.ProofOfEducation;
import de.phoenixrpg.proofofeducation.utils.DateUtils;
import de.phoenixrpg.proofofeducation.controller.proof.DailyProof;
import de.phoenixrpg.proofofeducation.controller.proof.ProofAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewManager {

    private static ViewManager viewManager;
    private ViewManager() {
    }

    private HashMap<String, String> messages = new HashMap<>() {{
        put("PROOF_ADDED", "Der Nachweis, am: %DATE wurde erfolgreich hinzugefügt.");
        put("PROOF_NOT_ADDED", "Der Nachweis, am: %DATE konnte nicht hinzugefügt werden.");
        put("PROOF_ADDED_ERROR", "Bei dem Hinzufügen des Nachweises ist ein Fehler aufgetreten");

        put("WRONG_DATE", "Das Datum wurde falsch geschrieben oder nicht angegeben [Format: dd.MM.yyyy]");
        put("MISSING_DATE", "Das Datum muss angegeben werden");
        put("MISSING_NAME_DATE", "Der Name und das Datum müssen angegeben werden.");
        put("MISSING_DAYS", "Nicht alle Tage, der Woche, wurden ausgefüllt.");

        put("USER_DIDNT_CREATE", "Der Benutzer konnte nicht angelegt werden.");
        put("USER_CREATE", "Der Benutzer wurde angelegt");

        put("ERROR_FILE_CREATION", "Ein Fehler während der Datei Erzeugung ist aufgetreten.");
    }};

    public Modal editDay(long discordId, LocalDate date) {
        DailyProof dailyProof = ProofOfEducation.getControllManager().getDataManager().getProofByDate(discordId, date);

        if(dailyProof == null) return showProofModal(DateUtils.getDateAsString(date), null, null);

        return showProofModal(DateUtils.getDateAsString(dailyProof.getProofDate()), dailyProof.getLocation(), dailyProof.getTask());
    }

    public List<ItemComponent> getWeekButtons(int size) {
        List<ItemComponent> itemComponents = new ArrayList<>();
        itemComponents.add(Button.primary("backWeek", "Last"));
        itemComponents.add(Button.primary("addDay", "Add"));
        itemComponents.add(Button.primary("nextWeek", "Next"));
        if (size == 5) itemComponents.add(Button.secondary("printWeek", "Print"));
        return itemComponents;
    }

    public List<ItemComponent> getDayButtons() {
        List<ItemComponent> itemComponents = new ArrayList<>();
        itemComponents.add(Button.primary("backDay", "Last"));
        itemComponents.add(Button.primary("editDay", "Edit"));
        itemComponents.add(Button.primary("nextDay", "Next"));
        return itemComponents;
    }

    public Modal showProofModal(String dateValue, String locationValue, String taskValue) {
        TextInput.Builder weekDay = TextInput.create("date", "Datum", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder("Beispiel: 01.01.2022")
                .setMinLength(8)
                .setMaxLength(16);
        if(dateValue != null) weekDay.setValue(dateValue);

        TextInput.Builder location = TextInput.create("location", "Standort", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder("Beispiel: Berufsschule")
                .setMinLength(0)
                .setMaxLength(32);
        if(locationValue != null) location.setValue(locationValue);

        TextInput.Builder task = TextInput.create("task", "Tätigkeit", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder("Beispiel: Java OOP und Enumerations [\"  \" = neue Zeile]")
                .setMinLength(0)
                .setMaxLength(256);
        if(taskValue != null) task.setValue(taskValue);


        return Modal.create("proof", "Täglicher Nachweis")
                .setTitle("Täglicher Nachweis")
                .addActionRow(weekDay.build())
                .addActionRow(location.build())
                .addActionRow(task.build())
                .build();
    }

    public void sendFile(IReplyCallback interact, long discordId, LocalDate localDate) {
        ProofAdapter proofAdapter = new ProofAdapter(discordId, localDate);
        if (proofAdapter.getDailyProofs().size() < 5 || proofAdapter.getDailyProofs() == null) {
            interact.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("MISSING_DAYS")).setEphemeral(true).queue();
            return;
        }
        File file = proofAdapter.build();
        if (file == null) {
            interact.reply(ProofOfEducation.getControllManager().getViewManager().getMessages("ERROR_FILE_CREATION")).queue();
            return;
        }
        interact.replyFiles(FileUpload.fromData(file)).setEphemeral(true).queue(interactionHook -> file.delete());
    }

    public void sendDailyNotificaction() {
        ProofOfEducation.getControllManager().getJDA().getTextChannelById("1019220634498248779").sendMessage("Heute schon den Nachweis geschrieben? [/addproof]").queue();
    }

    public void sendFridayNotification() {
        ProofOfEducation.getControllManager().getJDA().getTextChannelById("1019220634498248779").sendMessage("Möchtest du den wöchentlichen Nachweis erstellen? [/createproof dd.MM.yyyy]").queue();

    }

    public MessageEmbed getWeekEmbed(LocalDate date, HashMap<String, DailyProof> dailyProofList) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Nachweise der Woche").setColor(Color.CYAN).setDescription("--------------------------------------------------------------------");

        LocalDate cloneDate = date.minusDays(1);
        for (int proofPosition = 0; proofPosition < 5; proofPosition++) {
            cloneDate = cloneDate.plusDays(1);
            if (!dailyProofList.containsKey(cloneDate.toString())) {
                embedBuilder.addField("Unvollständig - " + DateUtils.getDateAsString(cloneDate), "/", false);
                continue;
            }
            DailyProof proof = dailyProofList.get(cloneDate.toString());
            addInformationToEmbed(proof, embedBuilder);
        }
        embedBuilder.setFooter(DateUtils.getDateAsString(date));
        return embedBuilder.build();
    }

    public MessageEmbed getDayEmbed(LocalDate date, DailyProof proof) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Nachweis des Tages").setColor(Color.CYAN).setDescription("--------------------------------------------------------------------");
        embedBuilder.setColor(Color.CYAN);

        if (proof == null) {
            embedBuilder.addField("Unvollständig - " + DateUtils.getDateAsString(date), "/", false);
        } else {
            addInformationToEmbed(proof, embedBuilder);
        }
        embedBuilder.setFooter(DateUtils.getDateAsString(date));
        return embedBuilder.build();
    }

    private void addInformationToEmbed(DailyProof proof, EmbedBuilder embedBuilder) {
        String task = "";
        String[] tasks = proof.getTask().split("  ");
        for (int taskPosition = 0; taskPosition < tasks.length; taskPosition++) {
            task += "- " + tasks[taskPosition] + "\n";
        }
        embedBuilder.addField(DateUtils.getDateAsString(proof.getProofDate()), "Standort: " + proof.getLocation() + "\n" + "Tätigkeit:\n" + task, false);
    }

    public String getMessages(String key) {
        return messages.get(key);
    }

    public static ViewManager getViewManager() {
        if (viewManager == null)
            viewManager = new ViewManager();
        return viewManager;
    }

}
