package org.flinkfood._helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

public class ReadFile {
    public static Optional<String> read(String fileWithPath) {
        StringBuilder content = new StringBuilder();
        try {
            File myObj = new File(fileWithPath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content.append(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            // TODO: replace with more robust error handling
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(content.toString());
    }
}
