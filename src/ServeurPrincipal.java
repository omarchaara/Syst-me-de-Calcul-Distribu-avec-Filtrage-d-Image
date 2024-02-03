import java.io.*;
import java.net.*;

public class ServeurPrincipal {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Serveur principal en attente de connexion...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connexion établie avec le client.");

            // Recevoir les deux matrices du client
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            int[][] matrice1 = (int[][]) ois.readObject();
            int[][] matrice2 = (int[][]) ois.readObject();

            // Diviser chaque matrice en lignes et envoyer au serveur esclave
            int nombreLignes = matrice1.length;
            int nombreEsclaves = 3;
            int lignesParEsclave = nombreLignes / nombreEsclaves;

            // Variables pour stocker les résultats partiels de chaque esclave
            int[] resultatPartiel1 = new int[matrice1[0].length];
            int[] resultatPartiel2 = new int[matrice1[0].length];
            int[] resultatPartiel3 = new int[matrice1[0].length];

            for (int i = 0; i < nombreEsclaves; i++) {
                int debutLigne = i * lignesParEsclave;
                int finLigne = (i + 1) * lignesParEsclave;

                try {
                    // Ajouter une temporisation de 1 seconde entre les connexions
                    Thread.sleep(1000);

                    Socket slaveSocket = new Socket("localhost", 54320 + i); // Ports 54321, 54322, 54323
                    System.out.println("Connexion établie avec le serveur esclave sur le port " + (54320 + i));

                    ObjectOutputStream oos = new ObjectOutputStream(slaveSocket.getOutputStream());
                    System.out.println("Envoi des données au serveur esclave...");

                    // Envoyer une ligne de chaque matrice
                    oos.writeObject(matrice1[debutLigne]);
                    oos.writeObject(matrice2[debutLigne]);
                    oos.flush();

                    // Attendre la réponse du serveur esclave
                    ObjectInputStream responseStream = new ObjectInputStream(slaveSocket.getInputStream());
                    int[] resultatPartiel = (int[]) responseStream.readObject();
                    System.out.println("Réponse reçue du serveur esclave sur le port " + (54320 + i));

                    // Stocker le résultat partiel dans la variable appropriée
                    switch (i) {
                        case 0:
                            resultatPartiel1 = resultatPartiel;
                            break;
                        case 1:
                            resultatPartiel2 = resultatPartiel;
                            break;
                        case 2:
                            resultatPartiel3 = resultatPartiel;
                            break;
                        // Vous pouvez étendre cela en ajoutant plus de cas pour un nombre différent d'esclaves
                    }

                    // Fermer les flux et la socket
                    oos.close();
                    responseStream.close();
                    slaveSocket.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    System.err.println("La connexion au serveur esclave sur le port " + (54320 + i) + " a échoué.");
                }
            }

            System.out.println("Rassembler les résultats des serveurs esclaves");

            // Rassembler les résultats des serveurs esclaves dans le résultat final
            int[][] resultatFinal = new int[nombreLignes][matrice1[0].length];
            for (int i = 0; i < nombreLignes; i++) {
                int debutLigne = i * lignesParEsclave;

                // Copier le résultat partiel dans le résultat final
                switch (debutLigne) {
                    case 0:
                        System.arraycopy(resultatPartiel1, 0, resultatFinal[i], 0, resultatPartiel1.length);
                        break;
                    case 1:
                        System.arraycopy(resultatPartiel2, 0, resultatFinal[i], 0, resultatPartiel2.length);
                        break;
                    case 2:
                        System.arraycopy(resultatPartiel3, 0, resultatFinal[i], 0, resultatPartiel3.length);
                        break;
                    // Vous pouvez étendre cela en ajoutant plus de cas pour un nombre différent de lignes
                }
            }

            // Envoyer le résultat final au client
            ObjectOutputStream clientOOS = new ObjectOutputStream(clientSocket.getOutputStream());
            clientOOS.writeObject(resultatFinal);
            clientOOS.flush();
            System.out.println("Résultat final envoyé au client.");

            // Fermer les flux et la socket
            ois.close();
            clientOOS.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
