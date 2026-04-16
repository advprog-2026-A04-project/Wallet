package id.ac.ui.cs.a04.json.wallet.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpaControllerTest {

    @Test
    void redirectShouldForwardToWalletAppShell() {
        SpaController controller = new SpaController();

        String view = controller.redirect("wallet");

        assertEquals("forward:/app/index.html", view);
    }
}
