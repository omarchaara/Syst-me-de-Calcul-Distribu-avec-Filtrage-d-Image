import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 12345);
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            // Définir deux matrices
            int[][] matrice1 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
            };

            int[][] matrice2 = {
                {9, 8, 7},
                {6, 5, 4},
                {3, 2, 1}
            };

            // Envoyer les matrices au serveur principal
            oos.writeObject(matrice1);
            oos.writeObject(matrice2);
            oos.flush();

            // Recevoir le résultat final du serveur principal
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            int[][] resultatFinal = (int[][]) ois.readObject();

            // Afficher le résultat final
            System.out.println("Résultat final de l'addition :");
            afficherMatrice(resultatFinal);

            // Ne fermez pas les flux ni la socket ici pour permettre au client de rester à l'écoute

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une matrice
    public static void afficherMatrice(int[][] matrice) {
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[i].length; j++) {
                System.out.print(matrice[i][j] + " ");
            }
            System.out.println();
        }
    }
}
