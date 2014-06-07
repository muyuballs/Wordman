package info.breezes.wordman.utils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by jianxingqiao on 14-6-6.
 */
public class StreamUtils {

    public static String[] readStrings(InputStream inputStream, String charsetName) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
            String line = reader.readLine();
            while (line != null) {
                strings.add(line);
                line = reader.readLine();
            }
        } catch (IOException exp) {
            throw new RuntimeException(exp);
        }
        return strings.toArray(new String[0]);
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) {
        byte[] buf = new byte[89120];
        try {
            int c = inputStream.read(buf);
            while (c != -1) {
                outputStream.write(buf, 0, c);
                c = inputStream.read(buf);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
