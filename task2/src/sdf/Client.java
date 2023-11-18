package sdf;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

//<user>@<host>:<port> 
//For example user@localhost:3000 means connect to the shopping
//cart server a localhost listening on port 3000. Load fred’s shopping cart.

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {

        String[] userSplit = args[0].split("@");
        String user = userSplit[0];
        String[] hostSplit = userSplit[1].split(":");
        String host = hostSplit[0];
        int port = Integer.parseInt(hostSplit[1]);

        Socket socket = new Socket(host, port); // Creates a stream socket and connects it to the specified port number
                                                // on the named host.
        System.out.println("connected to server");

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        oos.writeUTF("load %s".formatted(user));
        String response = ois.readUTF();
        System.out.printf("%s\n", response);
        // System.out.println("here");

        Console cons = System.console();

        while (true) {
            String input = cons.readLine("> ");
            String[] terms = input.split(" ");

            oos.writeUTF(input);
            oos.flush();

            if (terms[0].equals("exit")) {
                break;
            }

            response = ois.readUTF();
            System.out.printf("%s\n", response);

        }
    }

}
