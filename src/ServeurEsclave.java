import java.io.*;
import java.net.*;

public class ServeurEsclave {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ServeurEsclave <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur esclave en attente de connexion sur le port " + port + "...");

            while (true) {
                Socket slaveSocket = serverSocket.accept();
                System.out.println("Connexion établie avec le serveur principal.");

                // Recevoir une ligne de chaque matrice
                ObjectInputStream ois = new ObjectInputStream(slaveSocket.getInputStream());
                int[] ligneMatrice1 = (int[]) ois.readObject();
                int[] ligneMatrice2 = (int[]) ois.readObject();

                // Effectuer l'opération d'addition
                int[] resultat = new int[ligneMatrice1.length];
                for (int i = 0; i < ligneMatrice1.length; i++) {
                    resultat[i] = ligneMatrice1[i] + ligneMatrice2[i];
                }

                // Envoyer le résultat au serveur principal
                ObjectOutputStream oos = new ObjectOutputStream(slaveSocket.getOutputStream());
                oos.writeObject(resultat);

                // Fermer les flux et la socket
                ois.close();
                oos.close();
                slaveSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
