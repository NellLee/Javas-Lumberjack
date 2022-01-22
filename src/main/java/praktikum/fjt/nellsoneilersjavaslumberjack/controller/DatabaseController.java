package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.derby.jdbc.EmbeddedDriver;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.ExampleSetting;

public class DatabaseController {

  private static final DatabaseController INSTANCE = new DatabaseController();

  private static final String CONNECTION_URL = "jdbc:derby:javasLumberjackDatabase;create=true";

  private static final int ARG_MAX_LENGTH = 255;
  private static final String PAIR_DELIMITER = "_";

  private boolean status;
  private Connection connection = null;


  public static DatabaseController getInstance() {
    return INSTANCE;
  }

  public void checkStatus() {
    System.out.println("Status der Datenbankverbindung wird überprüft:");
    if(!INSTANCE.existsDatabase()) {
      INSTANCE.status = INSTANCE.createDatabase();
      if(INSTANCE.status) {
        System.out.println("Datenbank wurde erstellt!");
      } else {
        System.out.println("Datenbank erstellen fehlgeschlagen!");
      }
    } else {
      INSTANCE.status = true;
      System.out.println("Die existierende Datenbank wird genutzt.");
    }
  }

  public Connection getConnection() throws SQLException {
    //maven dependency loads derby
    DriverManager.registerDriver(new EmbeddedDriver());
    if(connection == null) {
      System.out.println("Erste Verbindung des Datenbanktreibers wird aufgebaut...");
      connection = DriverManager.getConnection(CONNECTION_URL);
      System.out.println("...Fertig!");
    }else if(!connection.isValid(0)) {
      connection = DriverManager.getConnection(CONNECTION_URL);
    }
    return connection;
  }

  public boolean saveExampleSetting(String name, String editorContent, String islandContent, String[] tags) {
    if(!status) {
      return false;
    }

    Connection curConnection = null;
    Statement curStatement = null;
    PreparedStatement curPrepStatement = null;
    ResultSet curResult;

    try {
      curConnection = getConnection();
      curConnection.setAutoCommit(false);

      curPrepStatement = curConnection.prepareStatement("INSERT INTO EXAMPLES (name, editorContent, islandContent) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
      curPrepStatement.setString(1, trimToMaxLength(name));
      curPrepStatement.setString(2, editorContent);
      curPrepStatement.setString(3, islandContent);
      curPrepStatement.executeUpdate();
      ResultSet generatedKeys = curPrepStatement.getGeneratedKeys();
      int curId;
      if (generatedKeys.next()) {
        curId = generatedKeys.getInt(1);
      } else {
        throw new SQLException("No ID obtained.");
      }

      curPrepStatement = curConnection.prepareStatement("INSERT INTO EXAMPLETAGS VALUES (?, ?)");
      curPrepStatement.setInt(2, curId);
      for(String tag : tags) {
        curPrepStatement.setString(1, trimToMaxLength(tag));
        curPrepStatement.executeUpdate();
      }

      curConnection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        if(curConnection != null) {
          curConnection.rollback();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
      return false;
    } finally {
      try {
        if (curConnection != null) {
          curConnection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      closeAll(curConnection, curStatement, curPrepStatement);
    }
    return true;
  }


  public ArrayList<String> getExampleNameIdPairByTags(String[] tags) {
    ArrayList<String> result = getAllExampleNameIdPairs();
    for (String tag : tags) {
      result.retainAll(getExampleNameIdPairByTag(tag));
    }
    return result;
  }

  public ArrayList<String> getExampleNameIdPairByTag(String tag) {
    if(!status) {
      return null;
    }

    Connection curConnection = null;
    PreparedStatement curPrepStatement = null;
    ResultSet curResult;

    try {
      curConnection = getConnection();

      curPrepStatement = curConnection.prepareStatement("SELECT EXAMPLES.id, EXAMPLES.name FROM EXAMPLES, EXAMPLETAGS "
          + "WHERE EXAMPLETAGS.tag = ? AND EXAMPLETAGS.id = EXAMPLES.id ORDER BY EXAMPLES.id");
      curPrepStatement.setString(1, trimToMaxLength(tag));
      curResult = curPrepStatement.executeQuery();

      ArrayList<String> nameIdPairs = new ArrayList<>();
      while(curResult.next()) {
        nameIdPairs.add(curResult.getString(2) + PAIR_DELIMITER + curResult.getInt(1));
      }
      return nameIdPairs;
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        if(curConnection != null) {
          curConnection.rollback();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    } finally {
      try {
        if (curConnection != null) {
          curConnection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      closeAll(curConnection, curPrepStatement);
    }
    return null;
  }

  public ArrayList<String> getAllExampleNameIdPairs() {
    if(!status) {
      return null;
    }

    Connection curConnection = null;
    Statement curStatement = null;
    ResultSet curResult;

    try {
      curConnection = getConnection();
      curStatement = curConnection.createStatement();
      curResult = curStatement.executeQuery("SELECT EXAMPLES.id, EXAMPLES.name FROM EXAMPLES ORDER BY EXAMPLES.id");

      ArrayList<String> nameIdPairs = new ArrayList<>();
      while(curResult.next()) {
        nameIdPairs.add(curResult.getString(2) + PAIR_DELIMITER + curResult.getInt(1));
      }
      return nameIdPairs;
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        if(curConnection != null) {
          curConnection.rollback();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    } finally {
      try {
        if (curConnection != null) {
          curConnection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      closeAll(curConnection, curStatement);
    }
    return null;
  }

  public ExampleSetting getExampleByNameIdPair(String nameIdPair) {
    return getExampleById(Integer.parseInt(nameIdPair.substring(nameIdPair.indexOf(PAIR_DELIMITER)+1)));
  }

  public ExampleSetting getExampleById(int id) {
    if(!status) {
      return null;
    }

    Connection curConnection = null;
    PreparedStatement curPrepStatement = null;
    ResultSet curResult;

    try {
      curConnection = getConnection();

      curPrepStatement = curConnection.prepareStatement("SELECT editorContent, islandContent FROM EXAMPLES "
          + "WHERE id = ?");
      curPrepStatement.setInt(1, id);
      curResult = curPrepStatement.executeQuery();

      if(curResult.next()) {
        return new ExampleSetting(curResult.getString(1), curResult.getString(2));
      } else {
        throw new SQLException("No entry with id [" + id + "] found.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        if(curConnection != null) {
          curConnection.rollback();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    } finally {
      try {
        if (curConnection != null) {
          curConnection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      closeAll(curConnection, curPrepStatement);
    }
    return null;
  }

  private void closeAll(AutoCloseable... closeables) {
    for (AutoCloseable closeable : closeables) {
      close(closeable);
    }
  }
  private void close(AutoCloseable closeable) {
    if(closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String trimToMaxLength(String str) {
    return str.length() > ARG_MAX_LENGTH ? str.substring(0, ARG_MAX_LENGTH) : str;
  }

  private boolean createDatabase() {

    Connection curConnection = null;
    Statement curStatement = null;
    try {
      curConnection = DriverManager.getConnection(CONNECTION_URL);
      curConnection.setAutoCommit(false);

      curStatement = curConnection.createStatement();

      curStatement.execute("CREATE TABLE EXAMPLES ("
          + "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
          + "name VARCHAR(" + ARG_MAX_LENGTH + ") NOT NULL, "
          + "editorContent LONG VARCHAR NOT NULL, "
          + "islandContent LONG VARCHAR NOT NULL)"
      );
      curStatement.execute("CREATE TABLE EXAMPLETAGS ("
          + "tag VARCHAR(" + ARG_MAX_LENGTH + ") NOT NULL, "
          + "id INT NOT NULL)"
      );

      curConnection.commit();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        if(curConnection != null) {
          curConnection.rollback();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    } finally {
      try {
        if (curConnection != null) {
          curConnection.setAutoCommit(true);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      closeAll(curConnection, curStatement);
    }
    return false;
  }


  public void shutDownDatabase() {
    close(connection);
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch (SQLException e) {
      if(e.getSQLState().equals("XJ015")) {
        System.out.println("Datenbank wurde heruntergefahren.");
      } else {
        e.printStackTrace();
      }
    }
  }

  private boolean existsDatabase() {
    try {
      Connection curConnection = getConnection();
      DatabaseMetaData metaData = curConnection.getMetaData();
      if(!metaData.getTables(curConnection.getCatalog(), "APP", "EXAMPLES", new String[] {"TABLE"}).next()
        || !metaData.getTables(curConnection.getCatalog(), "APP", "EXAMPLETAGS", new String[] {"TABLE"}).next()) {
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return true;
  }
}
