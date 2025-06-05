package org.intergalaxy.lpad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.*;
import java.util.Scanner;

import static org.intergalaxy.lpad.HEXUtils.byteToHex;
import static org.intergalaxy.lpad.HEXUtils.hexToByteArray;

public class LPAd {
    private static final Logger logger = LoggerFactory.getLogger(LPAd.class);
    public static final String SELECT_COMMAND = "00A4040000";

    private CardChannel channel;
    private Scanner scanner;

    public void start() throws CardException {
        var selectedCard = selectSmartCard();

        var card = selectedCard.connect("*");
        logger.info("Card connected: {}", card.getATR());

        channel = card.getBasicChannel();
        sendAPDUAndDisplayResponse(SELECT_COMMAND);

        readCommands();
    }

    private CardTerminal selectSmartCard() throws CardException {
        var terminals = TerminalFactory.getDefault().terminals().list();
        if (terminals.isEmpty()) {
            logger.error("No smart card readers found.");
            throw new IllegalStateException();
        }

        logger.info("Select smart card reader from list:");
        for (int i = 0; i < terminals.size(); i++) {
            var terminal = terminals.get(i);
            logger.info("{} {}, card is present: {}", (i + 1), terminal.getName(), terminal.isCardPresent());
        }

        scanner = new Scanner(System.in);
        var terminalNumber = Integer.parseInt(scanner.nextLine());
        var selectedTerminal = terminals.get(terminalNumber - 1);
        if (selectedTerminal == null) {
            logger.error("Cannot connect to smart card reader {}", terminalNumber);
            throw new IllegalStateException();
        }

        return selectedTerminal;
    }

    private void readCommands() {
        while (true) {
            logger.info("Enter next command:");
            var command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) {
                logger.info("Closing app by request");
                System.exit(0);
            }

            try {
                sendAPDUAndDisplayResponse(command);
            } catch (IllegalArgumentException | CardException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void sendAPDUAndDisplayResponse(String command) throws CardException {
        var selectAPDU = new CommandAPDU(hexToByteArray(command));
        logger.info("APDU sent: {}", byteToHex(selectAPDU.getBytes()));
        var response = channel.transmit(selectAPDU);
        logger.info("Response: {}", response);
    }

}
