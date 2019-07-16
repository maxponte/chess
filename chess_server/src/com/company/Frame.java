package com.company;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Frame {
    boolean fin;
    byte opcode;
    long length;
    String payload;
    public Frame(byte[] raw) {
        byte finOpCode = raw[0];
        fin = (byte)((finOpCode >> 7) & 1) > 0;
        opcode = (byte)(finOpCode & 0x0F);
        byte snd = (byte)(raw[1] & 0x7F);
        int start = 0;
        if(snd < 126) {
            length = (long)snd;
            start = 2;
        } else if(snd == 126) {
            length = ((short)raw[2] << 8) & (short)raw[3];
            start = 4;
        } else if(snd == 127) {
            length = ((long)raw[2] << 56);
            length &= ((long)raw[3] << 56-8);
            length &= ((long)raw[4] << 56-16);
            length &= ((long)raw[5] << 32);
            length &= ((long)raw[6] << 24);
            length &= ((long)raw[7] << 16);
            length &= ((long)raw[8] << 8);
            length &= (long)raw[9];
            start = 10;
        }
        byte[] mask = new byte[]{raw[start], raw[start+1], raw[start+2], raw[start+3]};
        for(int i = start+4; i < start+4+length; i++) {
            raw[i] = (byte)(raw[i] ^ mask[(i-start-4)%4]);
        }
        payload = new String(raw, start+4, (int)length, StandardCharsets.UTF_8);
    }
    public static boolean isPing(byte[] raw) {
        return (raw[0] & 0x0F) == 9;
    }
    public static void toPong(byte[] ping) {
        ping[0] = (byte)((ping[0] & 0xF0) | 0x0A);
    }
    public static byte[] convert(String payload) {
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        long length = payloadBytes.length;
        short smallLength = 0;
        short mediumLength = 0;
        long largeLength = 0;
        if(length < 126) {
            smallLength = (short)length;
        } else if (length < 1 << 16) {
            smallLength = 126;
            mediumLength = (short)length;
        } else {
            smallLength = 127;
            largeLength = length;
        }
        short fin = (short)0x8000;
        short opcode = (short)0x0100;
        short header = (short)(fin | opcode | smallLength);
        int size = 16 + (int)length;
        ByteBuffer b = ByteBuffer.allocate(size);
        b.putShort(header);
        if(mediumLength > 0) b.putShort(mediumLength);
        if(largeLength > 0) b.putLong(largeLength);
        b.put(payloadBytes);
        return b.array();
    }
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    /*
    Opcode should be 0x1 for
        "text"
        Mask is always0
        Fin Is one when this is the last of a series of messages
    Frame format:
​​
      0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+

    Example: "example"
        remember: one byte = 2 hex characters

        first int
            first digit should be 0x8 = 0b1000
            second digit should be the opcode, 0x1
            then payload length, in this case - 0x07
            then 0x0000
            first int - 0x81070000
            we got - 0x81070000
            good.
        14 bytes of header data, then payload. is that right?
        4 bytes size
        8 bytes empty
        2 bytes empty
        = 14 bytes before payload
     */

}
