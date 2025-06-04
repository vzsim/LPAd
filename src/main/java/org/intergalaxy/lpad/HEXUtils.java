package org.intergalaxy.lpad;

public class HEXUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private HEXUtils() {

    }

    public static String byteToHex(byte[] array) {
        char[] hexChars = new char[array.length * 2];
        for (int j = 0; j < array.length; j++) {
            int v = array[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static byte[] hexToByteArray(String s) {
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
