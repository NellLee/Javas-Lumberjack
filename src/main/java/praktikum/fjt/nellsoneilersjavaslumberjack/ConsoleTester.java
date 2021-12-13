package praktikum.fjt.nellsoneilersjavaslumberjack;

import java.util.Scanner;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Lumberjack;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Direction;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Wood;

public class ConsoleTester {


  public static boolean colorDisabled = false;
  public static String ANSI_RESET = "\u001B[0m";
  public static String ANSI_RED = "\u001B[31m";
  public static String ANSI_GREEN = "\u001B[32m";
  public static String ANSI_BLUE = "\u001B[34m";
  public static String ANSI_WHITE = "\u001B[37m";
  public static final String SEPARATOR = "----------------------------------------";
  private static int treeCount;
  private static Island island;
  private static Lumberjack actor;
  private static int commandCount = 0;


  public static void main(String[] args) {
    init();

    Scanner inputScanner = new Scanner(System. in);

    printWelcomeScreen(inputScanner);

    if(colorDisabled) {
      ANSI_RESET = ANSI_RED = ANSI_GREEN = ANSI_BLUE = ANSI_WHITE = "";
    }

    while(true) {
      printCurrentState();

      System.out.println("[Bitte gib einen Befehl ein...]");
      String input = getNextCommand(inputScanner);
      commandCount++;

      executeCommand(input);
    }
  }

  private static void executeCommand(String input) {
    try {
      switch (input.trim()) {
        case "v", "vorwaerts", "vorwaerts()" -> actor.vorwaerts();
        case "dl", "dreheLinks", "dreheLinks()" -> actor.dreheLinks();
        case "dr", "dreheRechts", "dreheRechts()" -> actor.dreheRechts();
        case "nh", "nimmHolz", "nimmHolz()" -> actor.nimmHolz();
        case "lh", "legeHolz", "legeHolz()" -> actor.legeHolz();
        case "fb", "faelleBaum", "faelleBaum()" -> {
          actor.faelleBaum();
          treeCount--;
        }
        case "b", "vorneBegehbar", "vorneBegehbar()" -> System.out.println(
            ANSI_GREEN + (actor.vorneBegehbar() ? "Vorne begehbar" : "Vorne NICHT begehbar") + ANSI_RESET);
        case "as", "axtSchwingbar", "axtSchwingbar()" -> System.out.println(
            ANSI_GREEN + (actor.axtSchwingbar() ? "Axt schwingbar" : "Axt NICHT schwingbar") + ANSI_RESET);
        case "vb", "vorneBaum", "vorneBaum()" -> System.out.println(
            ANSI_GREEN + (actor.vorneBaum() ? "Vorne Baum" : "Vorne KEIN Baum") + ANSI_RESET);
        case "vh", "vorneHolz", "vorneHolz()" -> System.out.println(
            ANSI_GREEN + (actor.vorneHolz() ? "Vorne Holz" : "Vorne KEIN Holz") + ANSI_RESET);
        case "th", "traegtHolz", "traegtHolz()" -> System.out.println(
            ANSI_GREEN + (actor.traegtHolz() ? "Trägt Holz" : "Trägt KEIN Holz") + ANSI_RESET);
        case "q", "quit", "e", "exit" -> { System.exit(0);
        }
        case "" -> {}
        default -> throw new IllegalStateException("Invalid command: " + input);
      }
    } catch (RuntimeException e) {
      System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
    }
  }

  private static void printCurrentState() {
    System.out.println();
    System.out.println("Highscore des Entwicklers: " + ANSI_GREEN + 37 + ANSI_RESET);
    System.out.println("Genutzte Befehle: " + ANSI_RED + commandCount + ANSI_RESET);
    System.out.println("Verbleibende Bäume: " + ANSI_RED + treeCount + ANSI_RESET);
    System.out.println();
    System.out.println(island);
    if(treeCount == 0) {
      System.out.println(ANSI_RED + "DU HAST GEWONNEN!");
      System.exit(0);
    }
    System.out.println();
  }

  private static String getNextCommand(Scanner inputScanner) {
    System.out.println(SEPARATOR);
    String input = inputScanner.nextLine();
    System.out.println(SEPARATOR);
    return input;
  }

  private static void printWelcomeScreen(Scanner inputScanner) {
    System.out.println(SEPARATOR);
    System.out.println("|   WILLKOMMEN ZU JAVA'S LUMBERJACK!   |");
    System.out.println(SEPARATOR);
    System.out.println();
    System.out.println("Versuche alle Bäume zu fällen und benutze dabei so wenig Befehle wie möglich.");
    System.out.println();
    System.out.println("HINWEIS:");
    System.out.println("Dieser Konsolentester benutzt ANSI Codes um farbliche Texte darzustellen.");
    System.out.println("Dies funktioniert möglicherweise nicht in der Konsole der Eclipse IDE.");
    System.out.println(ANSI_RED + "Dieser Satz sollte farbig sein." + ANSI_RESET);
    System.out.println("Ist er dies nicht:");
    System.out.println("erwäge die Installation des Plugins \"ANSI Escape in Console\" für Eclipse");
    System.out.println();
    System.out.println("oder [Gib 'keineFarben' ein, um alle Farben zu deaktivieren...]");
    System.out.println();
    System.out.println("[Gib 'start' ein, um zu starten...]");
    System.out.println();
    String startInput = getNextCommand(inputScanner).trim();
    while (!startInput.equals("start")) {
      if(startInput.equals("keineFarben")) {
        colorDisabled = true;
        System.out.println();
        System.out.println("Farben deaktiviert.");
      }
      System.out.println();
      if(!colorDisabled) {
        System.out.println("[Gib 'keineFarben' ein, um alle Farben zu deaktivieren...]");
      }
      System.out.println("[Gib 'start' ein, um zu starten...]");
      System.out.println();
      startInput = getNextCommand(inputScanner).trim();
    }
  }

  private static void init() {
    island = new Island(9, 6, new Position(1, 4), Direction.RIGHT);
    actor = island.getActor();
    createExampleOcean(island);
    createExampleObjects(island);
  }

  private static void createExampleObjects(Island island) {
    // Create some objects
    island.setPhysObjectAt(new Position(6, 1), new Tree());
    island.setPhysObjectAt(new Position(7, 2), new Tree());
    island.setPhysObjectAt(new Position(3, 2), new Tree());
    island.setPhysObjectAt(new Position(3, 3), new Tree());
    island.setPhysObjectAt(new Position(4, 3), new Tree());
    island.setPhysObjectAt(new Position(1, 3), new Tree());
    treeCount = 6;
    island.setPhysObjectAt(new Position(2, 1), new Wood());
    island.setPhysObjectAt(new Position(7, 1), new Stump(new Wood()));
    island.setPhysObjectAt(new Position(3, 4), new Stump());
  }

  private static void createExampleOcean(Island island) {
    // Define the ocean (I know that this is ugly and long, but outside this test one would probably never define the ocean through code)
    island.setOceanAt(new Position(0, 0));
    island.setOceanAt(new Position(0, 1));
    island.setOceanAt(new Position(0, 2));
    island.setOceanAt(new Position(0, 3));
    island.setOceanAt(new Position(0, 4));
    island.setOceanAt(new Position(0, 5));
    island.setOceanAt(new Position(1, 5));
    island.setOceanAt(new Position(2, 5));
    island.setOceanAt(new Position(3, 5));
    island.setOceanAt(new Position(4, 5));
    island.setOceanAt(new Position(5, 5));
    island.setOceanAt(new Position(6, 5));
    island.setOceanAt(new Position(7, 5));
    island.setOceanAt(new Position(8, 5));
    island.setOceanAt(new Position(6, 4));
    island.setOceanAt(new Position(7, 4));
    island.setOceanAt(new Position(8, 4));
    island.setOceanAt(new Position(8, 3));
    island.setOceanAt(new Position(8, 2));
    island.setOceanAt(new Position(8, 1));
    island.setOceanAt(new Position(8, 0));
    island.setOceanAt(new Position(7, 0));
    island.setOceanAt(new Position(6, 0));
    island.setOceanAt(new Position(5, 0));
    island.setOceanAt(new Position(4, 0));
    island.setOceanAt(new Position(3, 0));
    island.setOceanAt(new Position(2, 0));
    island.setOceanAt(new Position(1, 0));
  }

}
