package praktikum.fjt.nellsoneilersjavaslumberjack.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LumberjackProgram {

  private String name;

  public static final String DIRECTORY = "programs";
  public static final String PROGRAM_TEMPLATE = """
// Schreibe dein Programm in folgender Methode.
void main() {
  
}

/* Ausführung:
*	1) Kompiliere dein Programm
*	2) Starte die Simulation
*/
  
// Du kannst eigene Methoden definieren und in deinem Programm nutzen.
public void kehrt() {
  dreheLinks();
  dreheLinks();
}
  """;
  public static final String EXTENSION = ".java";

  public LumberjackProgram(String name) {
    this.name = name;
    createIfNotExists();
  }

  public static boolean programFileExists(String fileName) {
    Path file = Paths.get(DIRECTORY, fileName);
    return Files.exists(file);
  }

  private void createIfNotExists() {
    File dir = new File(DIRECTORY);
    if(!dir.exists()) {
      dir.mkdir();
    }
    Path file = getFile();
    if (!Files.exists(file)) {
      try {
        Files.createFile(file);
      } catch (IOException e) {
        e.printStackTrace();
      }
      saveCodeToFile(PROGRAM_TEMPLATE);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFileName() {
    return name + LumberjackProgram.EXTENSION;
  }

  public String getFullPath() {
    return DIRECTORY + File.separator + getFileName();
  }

  public String loadCodeFromFile() {
    try {
      Path file = getFile();
      List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
      String code = String.join(System.lineSeparator(), lines);
      code = code.substring(getPrefix().length(), code.length() - getPostfix().length());
      return code;
    } catch (IOException exc) {
      return PROGRAM_TEMPLATE;
    }
  }

  private Path getFile() {
    return Paths.get(DIRECTORY, getFileName());
  }

  public void saveCodeToFile(String code) {
    Path file = getFile();
    try {
      Files.writeString(file, getPrefix() + code + getPostfix());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getPostfix() {
    return System.lineSeparator() + "}";
  }

  private String getPrefix() {
    return "public class " + getName() + " extends praktikum.fjt.nellsoneilersjavaslumberjack.model.Lumberjack {" + System.lineSeparator() + "public ";
  }
}
