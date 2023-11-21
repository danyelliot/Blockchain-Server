package com.servidortareas;

import java.io.File;

public class VariablesConexion {
  public final static String PATH = getPathToFile("listapalabras1.txt");
  public final static int PORT = 12345;
  public final static String IP = "localhost";
  public final static int MAX_CONNECTIONS = 2;
  public final static int N_ZEROS = 1;

  private static String getPathToFile(String fileName) {
    String rootDir = System.getProperty("user.dir");
    String filePath = rootDir + File.separator + "servidor_tareas" + File.separator + "src" + File.separator + "main"
        + File.separator + "java" + File.separator + "com" + File.separator + "servidortareas" + File.separator
        + fileName;
    return filePath;
  }
}
