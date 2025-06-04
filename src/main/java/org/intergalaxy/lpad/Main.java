package org.intergalaxy.lpad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.*;
import java.util.Scanner;

import static org.intergalaxy.lpad.HEXUtils.byteToHex;
import static org.intergalaxy.lpad.HEXUtils.hexToByteArray;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String SELECT_COMMAND = "00A4040000";

    public static void main(String[] args) throws Exception {
        var terminals = TerminalFactory.getDefault().terminals().list();
        if (terminals.isEmpty()) {
            logger.error("No smart card readers found.");
            return;
        }

        logger.info("Select smart card reader from list:");
        for (int i = 0; i < terminals.size(); i++) {
            var terminal = terminals.get(i);
            logger.info("{} {}, card is present: {}", (i + 1), terminal.getName(), terminal.isCardPresent());
        }

        var scanner = new Scanner(System.in);
        var terminalNumber = scanner.nextInt();
        var selectedTerminal = terminals.get(terminalNumber - 1);
        if (selectedTerminal == null) {
            logger.error("Cannot connect to smart card reader {}", terminalNumber);
            return;
        }

        var card = selectedTerminal.connect("*");
        logger.info("Card connected: {}", card.getATR());

        var channel = card.getBasicChannel();
        sendAPDUAndDisplayResponse(SELECT_COMMAND, channel);

        while (true) {
            logger.info("Enter next command:");
            var command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) 
                System.exit(0);

            sendAPDUAndDisplayResponse(command, channel);
        }
    }

    private static void sendAPDUAndDisplayResponse(String command, CardChannel channel) throws CardException {
        var selectAPDU = new CommandAPDU(hexToByteArray(command));
        logger.info("APDU sent: {}", byteToHex(selectAPDU.getBytes()));
        var response = channel.transmit(selectAPDU);
        logger.info("Response: {}", response);
    }


}