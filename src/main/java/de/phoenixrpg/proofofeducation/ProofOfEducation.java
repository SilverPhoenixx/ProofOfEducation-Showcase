package de.phoenixrpg.proofofeducation;

import de.phoenixrpg.proofofeducation.controller.ControllManager;

import javax.security.auth.login.LoginException;

public class ProofOfEducation {

    private static ControllManager controllManager;
    public static void main(String[] args) throws LoginException {
        controllManager = new ControllManager();
    }

    public static ControllManager getControllManager() {
        return controllManager;
    }
}
