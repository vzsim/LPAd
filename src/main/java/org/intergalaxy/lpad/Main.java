package org.intergalaxy.lpad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {
        try {
            var lpad = new LPAd();
            lpad.start();
        } catch (CardException e) {
            logger.error(e.getMessage(), e);
        }
    }


}