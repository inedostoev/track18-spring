package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.net.InetSocketAddress;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File inputFile = new File(finPath);
        final File outputFile;

        if(foutPath != null) {
            outputFile = new File(foutPath);
        }
        else {
            outputFile = File.createTempFile("based_file_", ".txt");
            outputFile.deleteOnExit();
        }

        int bufSize = 1023;

        byte[] inBuf = new byte[bufSize];
        byte[] outBuf = new byte[bufSize * 4/3];

        try(final InputStream is = new FileInputStream(inputFile);
            final OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));) {

            int readBytes = -1;

            while((readBytes = is.read(inBuf, 0, bufSize)) != -1) {
                int addZero = 0;
                if(readBytes % 3 == 1 || readBytes % 3 == 2) {
                    addZero = 3 - readBytes % 3;
                }
                for(int j = 0; j < addZero; j++) {
                    inBuf[readBytes++] = 0x00;
                }
                for(int i = 0; i < readBytes / 3; i++) {
                    /*
                     * 0xFC = 11111100
                     * 0x03 = 00000011
                     * 0xF0 = 11110000
                     * 0x0F = 00001111
                     * 0xC0 = 11000000
                     * 0x3F = 00111111
                     */
                    int nFirstChar = (inBuf[i * 3] & 0xFC) >> 2;
                    int nSecondChar = ((inBuf[i * 3] & 0x03) << 4) | ((inBuf[i * 3 + 1] & 0xF0) >>> 4);
                    int nThirdChar = ((inBuf[i * 3 + 1] & 0x0F) << 2) | ((inBuf[i * 3 + 2] & 0xC0) >>> 6);
                    int nForthChar = inBuf[i * 3 + 2] & 0x3F;

                    outBuf[i * 4] = (byte) toBase64[nFirstChar];
                    outBuf[i * 4 + 1] = (byte) toBase64[nSecondChar];
                    outBuf[i * 4 + 2] = (byte) toBase64[nThirdChar];
                    outBuf[i * 4 + 3] = (byte) toBase64[nForthChar];
                }

                for(int i = 0; i < addZero; i++) {
                    outBuf[readBytes * 4/3 - 1 - i] = (byte)'=';
                }

                os.write(outBuf, 0, readBytes * 4/3);
            }

        }
        catch(IOException ex) {
            System.out.print(ex.getMessage());
        }

        return outputFile;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));
    }

}
