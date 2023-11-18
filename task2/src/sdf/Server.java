package sdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//The server takes 2 parameters: 1. shopping cart directory
//2. port the server listens to

public class Server {

    public static void main(String[] args) throws IOException {
        String directoryPath = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            System.out.printf("Starting shopping cart server on port %d\n", port);
            ServerSocket server = new ServerSocket(port);
            System.out.println("Waiting for client connection");

            File theDir = new File("shoppingcart");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }

            System.out.println("Welcome to your shopping cart");
            boolean loggedin = false;
            String userName = "";

            ArrayList<String> cart = new ArrayList<String>();

            while (true) {
                Socket client = server.accept();
                // client connection arrive
                System.out.println("Connection received...");

                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

                // InputStreamReader isr = new InputStreamReader(client.getInputStream());
                // BufferedReader br = new BufferedReader(isr);
                // OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
                // BufferedWriter bw = new BufferedWriter(osw);

                boolean stop = false;
                String reply = "";

                while (!stop) {
                    String input = ois.readUTF();
                    System.out.printf("CLIENT REQUEST: %s\n", input);

                    String[] terms = input.split(" ");
                    String command = terms[0];

                    switch (command) {
                        case "add":
                            boolean found = false;
                            for (int i = 1; i < terms.length; i++) {
                                for (String item : cart) {
                                    if ((item.toLowerCase()).equals(terms[i].toLowerCase())) {
                                        found = true;
                                        reply = "%s is already in your shopping cart %n".formatted(terms[i]);
                                    }
                                }
                                if (!found) {
                                    cart.add(terms[i]);
                                    reply = "%s has been added to the cart.\n".formatted(terms[i]);
                                }
                            }
                            break;

                        case "delete":
                            Integer deleteItem = Integer.parseInt(terms[1]);
                            if ((0 < deleteItem) && (deleteItem <= cart.size())) {
                                String removed = cart.get(deleteItem - 1);
                                cart.remove(deleteItem - 1);
                                reply = "%s was removed from the shopping cart".formatted(removed);
                            } else {
                                reply = "incorrect item index";
                            }
                            break;

                        case "list":
                            if (cart.size() != 0) {
                                for (int i = 0; i < cart.size(); i++) {
                                    reply = "%d. %s %n".formatted(i + 1, cart.get(i));
                                }
                            } else {
                                reply = "Your cart is empty";
                            }
                            break;

                        case "load":
                            reply = "here";
                            loggedin = true;
                            userName = terms[1];
                            String fileName = userName + ".cart";
                            Path filePath = Paths.get(directoryPath, fileName);
                            boolean fileExists = Files.exists(filePath);

                            if (fileExists) {
                                try {
                                    String fileDirect = directoryPath + "/" + fileName;
                                    FileReader fr = new FileReader(fileDirect);
                                    BufferedReader br = new BufferedReader(fr);
                                    String line;
                                    while (null != (line = br.readLine())) {
                                        reply = line;
                                    }
                                    br.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!fileExists) {
                                try {
                                    Files.createFile(filePath);
                                    reply = "Created File: %s\n".formatted(fileName);
                                } catch (IOException e) {
                                    reply = "Something went wrong";
                                }
                            }
                            break;

                        case "save":
                            if (loggedin == false) {
                                System.out.println("please log in first");
                            } else {
                                try {
                                    Path fPath = Paths.get(directoryPath, userName + ".cart");
                                    File fileToWrite = new File("%s".formatted(fPath));
                                    FileWriter fw = new FileWriter(fileToWrite); // fileWriter documentation if leave
                                                                                 // append
                                                                                 // alone, append default is false
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    for (String cartItem : cart) {
                                        bw.write(cartItem);
                                        bw.newLine();
                                    }
                                    bw.close();
                                    reply = "Shopping cart items have been written to the file";

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        case "users":
                            try (DirectoryStream<Path> directoryStream = Files
                                    .newDirectoryStream(Path.of(directoryPath))) {
                                for (Path path : directoryStream) {
                                    String fn = path.getFileName().toFile().getName();
                                    if (fn.equals(".DS_Store"))
                                        continue;
                                    System.out.println(fn);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            break;

                    }
                    oos.writeUTF(reply);
                    oos.flush();
                }

            }
        } catch (IOException ex) {
            System.err.println("Server error, exiting");
            ex.printStackTrace();
        }
    }
}
