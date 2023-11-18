package sdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) throws Exception{

        File theDir = new File("shoppingcart");
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        String directoryPath = args[0];// java -cp classes sdf.App /Users/CelineNg/Desktop/SoftwareDevelopmentFundamentals/SDFworkshops/sc/task1/shoppingcart

        System.out.println("Welcome to your shopping cart");
        boolean loggedin = false;
        String userName = "";
        ArrayList<String> cart = new ArrayList<String>();

        Console cons = System.console();

        boolean stop = false;

        while (!stop) {
            String input = cons.readLine("> ");

            if ("exit".equals(input)) {
                stop = true; }

            String[] terms = input.split(" ");
            String command = terms[0];

            if ("add".equals(command)) {
                boolean found = false;
                for (int i = 1; i < terms.length; i++) {
                    for (String item : cart) {
                        if ((item.toLowerCase()).equals(terms[i].toLowerCase())) {
                            found = true;
                            System.out.printf("%s is already in your shopping cart %n", terms[i]);
                            break;
                        }
                        }
                        if (!found) {
                            cart.add(terms[i]);
                            System.out.printf("%s has been added to the cart.\n", terms[i]);
                        }
                }
            } else if ("delete".equals(command)) {
                Integer deleteItem = Integer.parseInt(terms[1]);
                if ((0 < deleteItem) && (deleteItem <= cart.size())) {
                    String removed = cart.get(deleteItem - 1);
                    cart.remove(deleteItem - 1);
                    System.out.printf("%s was removed from the shopping cart", removed);
                } else {
                    System.out.println("incorrect item index");
                }
            } else if ("list".equals(command)) {
                if (cart.size() != 0) {
                    for (int i = 0; i < cart.size(); i++) {
                        System.out.printf("%d. %s %n", i + 1, cart.get(i));
                    }
                } else {
                    System.out.println("Your cart is empty");
                }

            } else if ("load".equals(command)) {
                loggedin = true;
                userName = terms[1];
                String fileName = userName + ".cart";
                Path filePath = Paths.get(directoryPath, fileName);
                boolean fileExists = Files.exists(filePath); 
                System.out.printf("UserFile '%s' %s%n", userName, fileExists ? "exists." : "does not exist.");

                if (fileExists) {
                    try {
                        String fileDirect = directoryPath + "/"+ fileName;
                        FileReader fr = new FileReader(fileDirect);
                        BufferedReader br = new BufferedReader(fr);
                        String line;
                        while (null != (line = br.readLine())) {
                            System.out.println(line);
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!fileExists) {
                    try {
                        Files.createFile(filePath);
                        System.out.println("Created File: " + fileName);
                    } catch (IOException e) {
                        System.out.println("Something went wrong");
                    }
                }
            } else if ("save".equals(command)) {
                if (loggedin == false) {
                    System.out.println("please log in first");
                } else { // currently over writes, how do i make it so it will append instead
                    try {
                        Path filePath = Paths.get(directoryPath, userName + ".cart");
                        File fileToWrite = new File("%s".formatted(filePath));
                        FileWriter fw = new FileWriter(fileToWrite); // fileWriter documentation if leave append
                                                                           // alone, append default is false
                        BufferedWriter bw = new BufferedWriter(fw);
                        for (String cartItem : cart) {
                            bw.write(cartItem);
                            bw.newLine();
                        }
                        bw.close();
                        System.out.println("Shopping cart items have been written to the file");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if ("users".equals(command)) {
                // Specify the path to the folder you want to list files from
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(directoryPath))) {
                    // Use a try-with-resources block to create a DirectoryStream with Files.newDirectoryStream(directoryPath).
                    // stream will iterate over the files and directories in the specified folder
                    // Iterate through DirectoryStream and check each item if it's a regular file using Files.isRegularFile(filePath).
                    // If it's a file, print its name using filePath.getFileName().
                    for (Path filePath : directoryStream) {
                        String fn = filePath.getFileName().toFile().getName();
                        if (fn.equals(".DS_Store"))
                            continue;
                        System.out.println(fn);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}