package de.unikarlsruhe.nan.pos;

import de.unikarlsruhe.nan.pos.util.CRC;

import java.io.*;

/**
 * @author Anton Schirg
 */
public class NoteValidator {
    private static NoteValidator instance = null;

    private OutputStream scanner_writer = null;
    private InputStream reader = null;

    public static NoteValidator getInstance() throws IOException {
        if (instance == null) {
            instance = new NoteValidator();
        }
        return instance;
    }

    private NoteListener listener = null;

    public NoteValidator() throws IOException {
        try {
            scanner_writer = new FileOutputStream("/dev/ttyACM0");
            reader = new FileInputStream("/dev/ttyACM0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sendPacket(new char[]{0x01});
        //readPacket();
        sendPacket(new char[]{0x11}); // Sync
        sendPacket(new char[]{0x05}); // Set-up request
        sendPacket(new char[]{0x02, 0xff, 0xff}); // Set inhibits (enable all notes)
    }



    public void disableListener() {
        listener = null;
    }

    public void setListener(NoteListener listener) throws IOException {
        if(this.listener != null) {
            throw new Error("Listener already registerd!");
        }
        this.listener = listener;
        sendPacket(new char[]{0x0A});
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (listener != null) {
                        char[] pollData = sendPacket(new char[]{0x07});
                        if(pollData.length > 1) {
                            /*for (int i = 0; i < pollData.length; i++) {
                                System.err.print(String.format("%02x", (int) pollData[i]));
                            }
                            System.err.println();
                            */
                            switch (pollData[1]) {
                                case 0xef:
                                    if (pollData[2] > 0) {
                                        System.err.println("Read channel " + (int)pollData[2]);
                                        if(!listener.onNoteRead((int)pollData[2])) {
                                            System.err.println("Got reject from listener");
                                            rejectNote();
                                        }
                                    } else {
                                        System.err.println("Reading...");
                                    }
                                    break;
                                case 0xcc:
                                    System.err.println("Stacking");
                                    break;
                                case 0xee:
                                    System.err.println("Credit!: " + (int)pollData[2]);
                                    break;
                                case 0xeb:
                                    System.err.println("Stacked. ");
                                    break;
                                case 0xed:
                                    System.err.println("Rejecting... ");
                                    break;
                                case 0xec:
                                    System.err.println("Rejected!");
                                    listener.onNoteRejected();
                                    break;
                                default:
                                    System.err.println("Unhandled repsonse: " + String.format("%02x", (int) pollData[1]));

                            }
                        }
                    }
                    sendPacket(new char[]{0x09});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }
    private void rejectNote() throws IOException {
        sendPacket(new char[]{0x08});
    }
    public interface NoteListener {
        /**
         * @return valid scan - if true listener will be unregistered
         */
        public boolean onNoteRead(int channel);
        public void onNoteCredited(int channel);
        public void onNoteRejected();
    }

    public static void main(String[] args) {
        try {
            NoteValidator cardReader = new NoteValidator();
            cardReader.setListener(new NoteListener() {
                @Override
                public boolean onNoteRead(int channel) {
                    return true;
                }

                @Override
                public void onNoteCredited(int channel) {
                }

                @Override
                public void onNoteRejected() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean flip = true;

    private char[] readPacket() throws IOException {
        char s = (char) reader.read();
        if (s != 0x7f) {
            throw new Error("Unexpected input: " + String.format("%02x", (short) s));
        }
        reader.read();
        int len = reader.read();
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = (char) reader.read();
        }
        reader.read();
        reader.read();
        if (data[0] != 0xf0) {
            throw new Error("Non-ok response: " + String.format("%02x", data[0]));
        }
        return data;
    }

    private char[] sendPacket(char[] data) throws IOException {
        char[] buf = new char[3 + data.length + 2];
        buf[0] = 0x7f;
        buf[1] = (char) (flip ? 0x80 : 0x00);
        flip = !flip;
        buf[2] = (char) data.length;
        for (int i = 0; i < data.length; i++) {
            buf[3 + i] = data[i];
        }
        CRC crc = new CRC();
        for (int i = 1; i < 3 + data.length; i++) {
            crc.update(buf[i]);
        }
        char[] crcData = crc.get();
        for (int i = 0; i < crcData.length; i++) {
            buf[3 + data.length + i] = crcData[i];
        }

        int numStuff = 0;
        for (int i = 1; i < 3 + data.length + 2; i++) {
            if (buf[i] == 0x7f)
                numStuff++;
        }

        char[] stuffedBuf = new char[buf.length + numStuff];
        stuffedBuf[0] = buf[0];
        int stuffed_i = 1;
        for (int i = 1; i < 3 + data.length + 2; i++) {
            stuffedBuf[stuffed_i] = buf[i];
            if (stuffedBuf[stuffed_i] == 0x7f) {
                stuffed_i++;
                stuffedBuf[stuffed_i] = 0x7f;
            }
            stuffed_i++;
        }

        for (int i = 0; i < stuffedBuf.length; i++) {
            scanner_writer.write(stuffedBuf[i]);
        }
        scanner_writer.flush();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readPacket();
    }
}
