package com.servidortareas;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Servidor {
  String path = VariablesConexion.PATH;
  String ip = VariablesConexion.IP;
  int puerto = VariablesConexion.PORT;
  ManejadorCliente sendlis[] = new ManejadorCliente[VariablesConexion.MAX_CONNECTIONS];

  BlockingQueue<String> responseQueue = new LinkedBlockingQueue<String>();

  int number_zeros = VariablesConexion.N_ZEROS;
  
  List<String> words = new ArrayList<String>();

  List<Double> timesInSec = new ArrayList<Double>();
  Thread ThreadHandler[] = new Thread[VariablesConexion.MAX_CONNECTIONS];

  public void iniciarServidor() {
    try {
      ServerSocket ss = new ServerSocket(puerto);
      System.out.println("Iniciando el servidor en el Puerto: " + puerto + "...");

      Thread responseThread = new Thread(new ResponseHandler(responseQueue));
      responseThread.start();

      int i = 0;
      while (true) {
        // Esperamos a que un cliente se conecte
        Socket sc = ss.accept(); // Acepta la conexion entrante
        System.out.println("Cliente conectado desde la IP: " + sc.getInetAddress() + ":" + sc.getPort());

        // Creamos un objeto PrintWriter para enviar datos al cliente
        PrintWriter out = new PrintWriter(new OutputStreamWriter(sc.getOutputStream(), "ISO-8859-1"), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));

        System.out.println("bandera 1");
        sendlis[i] = new ManejadorCliente(sc, in, out, responseQueue);
        System.out.println("bandera 2");
        Thread thread = new Thread(sendlis[i]);
        System.out.println("bandera 3");
        // ThreadHandler[i] = thread;
        thread.start();
        System.out.println("bandera 4");
        i++;

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  class ManejadorCliente implements Runnable {
    private Socket sc;
    private BufferedReader in;
    private PrintWriter out;
    private BlockingQueue<String> responseQueue;

    public ManejadorCliente(Socket sc, BufferedReader in, PrintWriter out, BlockingQueue<String> responseQueue) {
      this.sc = sc;
      this.in = in;
      this.out = out;
      this.responseQueue = responseQueue;
    }

    public void run() {
      try {
        System.out.println("bandera 5");
        words = leerArchivo();
        System.out.println(words);
        // Calculamos el tiempo de inicio del hallazgo de los ceros en el hash
        long startTime = System.nanoTime();
        System.out.println("bandera 6");

        // Enviar solo un elemento de words
        out.println(words.get(0) + " " + number_zeros);

        // // Esperamos a que el cliente envie un mensaje
        String mensajeCliente = in.readLine();
         System.out.println("bandera 8");
        System.out.println("Mensaje del cliente: " + mensajeCliente);
        System.out.println("bandera 7");
        responseQueue.put(mensajeCliente);// Add to queue

        // Calculamos el tiempo de finalizacion del hallazgo de los ceros en el hash
        long estimatedTime = System.nanoTime() - startTime;
        double estimatedTimeInSec = (double) estimatedTime / 1_000_000_000;
        timesInSec.add(estimatedTimeInSec);
        System.out.println(words.get(0) + " " + number_zeros);

        System.out.println("Mensaje del cliente: " + mensajeCliente);
        System.out.println("Tiempo de demora en encontrar el primer key: " + estimatedTimeInSec + "s");
        // Cerramos la conexion con el cliente
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  class ResponseHandler implements Runnable {
    private BlockingQueue<String> responseQueue;

    public ResponseHandler(BlockingQueue<String> responseQueue) {
      this.responseQueue = responseQueue;
    }

    public void run() {
      try {
        int contador_paraConfirmar = 0;
        while (true) {

          if (responseQueue.size() == VariablesConexion.MAX_CONNECTIONS) {

            String response = responseQueue.take();
            System.out.println("tamano despues de take" + responseQueue.size());

            System.out.println("Respuesta del cliente atravez de la cola: " + response);

            for (ManejadorCliente sendli : sendlis) {
              sendli.out.println(response);
            }

            for (ManejadorCliente sendli : sendlis) {
              String respuesta_para_confirmar = sendli.in.readLine();

              if (respuesta_para_confirmar.equals("OK")) {
                contador_paraConfirmar++;
              }

            }
            if (contador_paraConfirmar == VariablesConexion.MAX_CONNECTIONS) {
              System.out.println("Se ha verificado por los mineros el key" + " " + response);
              break;
            }

          }

        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public List<String> leerArchivo() {
    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    List<String> palabras = new ArrayList<String>();
    try {
      // Apertura del fichero y creacion de BufferedReader para poder
      // hacer una lectura comoda (disponer del metodo readLine()).
      archivo = new File(path);
      fr = new FileReader("D:\\Windows\\OneDrive - UNIVERSIDAD NACIONAL DE INGENIERIA\\1.10 Computer\\Desktop\\DistributedMiningNetwork-main\\DistributedMiningNetwork-main\\servidor_tareas\\src\\main\\java\\com\\servidortareas\\listapalabras1.txt");
      
      br = new BufferedReader(fr);
      System.out.println("2");
      
      // Lectura del fichero
      String linea = br.readLine();
      while (linea != null) {
        // System.out.println(linea);
        palabras.add(linea);
        linea = br.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // En el finally cerramos el fichero, para asegurarnos
      // que se cierra tanto si todo va bien como si salta
      // una excepcion.
      try {
        if (br != null) {
          br.close();
        }
        if (fr != null) {
          fr.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    return palabras;
  }

}
