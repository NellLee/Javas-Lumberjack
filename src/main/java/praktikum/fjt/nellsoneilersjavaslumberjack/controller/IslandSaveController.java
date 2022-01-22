package praktikum.fjt.nellsoneilersjavaslumberjack.controller;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.PrinterJob;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.Island;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Direction;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.dataTypes.Position;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.PhysicalObject;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Stump;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Tree;
import praktikum.fjt.nellsoneilersjavaslumberjack.model.physicalObjects.Wood;
import praktikum.fjt.nellsoneilersjavaslumberjack.view.AlertFactory;

public class IslandSaveController {

  private final FXMLController fxmlCtrl;
  private final Island island;

  public IslandSaveController(FXMLController fxmlCtrl, Island island) {
    this.fxmlCtrl = fxmlCtrl;
    this.island = island;
  }


  public void initialize() {

    initializeSerializationBtnEventHandling();
    initializePrintBtnEventHandling();
    initializeSaveImageBtnEventHandling();
    initializeXMLBtnEventHandling();
  }

  private void initializeSaveImageBtnEventHandling() {
    fxmlCtrl.saveIslandPNGMenuItem.setOnAction(actionEvent -> saveIslandRegionAsImage("png"));
    fxmlCtrl.saveIslandGIFMenuItem.setOnAction(actionEvent -> saveIslandRegionAsImage("gif"));
  }

  private void saveIslandRegionAsImage(String fileExtension) {
    fileExtension = fileExtension.toLowerCase();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Bild speichern unter...");
    fileChooser.getExtensionFilters().add(new ExtensionFilter(fileExtension.toUpperCase() + " Bild Dateien", "*." + fileExtension));
    File file = fileChooser.showSaveDialog(fxmlCtrl.stage);
    if(file == null) {
      return;
    }

    WritableImage islandImage = new WritableImage((int) fxmlCtrl.islandRegion.getWidth(), (int) fxmlCtrl.islandRegion.getHeight());
    fxmlCtrl.islandRegion.snapshot(null, islandImage);
    RenderedImage renderedImage = SwingFXUtils.fromFXImage(islandImage, null);
    try {
      ImageIO.write(renderedImage, fileExtension, file);
      fxmlCtrl.setStatusLabelMessage("Bild '" + file.getName() + "' erfolgreich gespeichert!");
    } catch (IOException e) {
      AlertFactory.createError("Speichern des Bildes fehlgeschlagen: " + e.getMessage());
    }
  }

  private void initializePrintBtnEventHandling() {
    fxmlCtrl.printIslandMenuItem.setOnAction(actionEvent -> {
      PrinterJob job = PrinterJob.createPrinterJob();
      if (job != null && job.showPrintDialog(fxmlCtrl.stage)){
        boolean success = job.printPage(fxmlCtrl.islandRegion);
        if (success) {
          job.endJob();
          fxmlCtrl.setStatusLabelMessage("Insel erfolgreich gedruckt!");
        } else {
          AlertFactory.createError("Drucken der Insel fehlgeschlagen!");
        }
      }
    });
  }

  private void initializeSerializationBtnEventHandling() {
    fxmlCtrl.serializeIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel speichern unter...");
      fileChooser.setInitialDirectory(new File("islands"));
      fileChooser.getExtensionFilters().add(new ExtensionFilter("JavasLumberjack Insel Dateien", "*.island"));
      File file = fileChooser.showSaveDialog(fxmlCtrl.stage);
      if(file != null) {
        try (
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
        ) {
          synchronized (island) {
            objectOut.writeObject(island);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    fxmlCtrl.deserializeIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel öffnen...");
      fileChooser.setInitialDirectory(new File("islands"));
      fileChooser.getExtensionFilters().add(new ExtensionFilter("JavasLumberjack Insel Dateien", "*.island"));
      File file = fileChooser.showOpenDialog(fxmlCtrl.stage);
      if(file != null) {
        try (
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
          synchronized (island) {
            island.replaceBy((Island) objectIn.readObject());
          }
          fxmlCtrl.refreshIslandSizeSpinners();
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void initializeXMLBtnEventHandling() {
    fxmlCtrl.saveXMLIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel speichern unter...");
      fileChooser.setInitialDirectory(new File("islands"));
      fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Dateien", "*.xml"));
      File file = fileChooser.showSaveDialog(fxmlCtrl.stage);
      if(file != null) {
        try {
          if (!saveIslandXMLToStream(island, new FileOutputStream(file))) {
            AlertFactory.createError("Could not save XML.");
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    });

    fxmlCtrl.loadXMLIslandMenuItem.setOnAction(actionEvent -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Insel öffnen...");
      fileChooser.setInitialDirectory(new File("islands"));
      fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Dateien", "*.xml"));
      File file = fileChooser.showOpenDialog(fxmlCtrl.stage);
      if(file != null) {
        try {
          if (!loadXMLStreamToIsland(island, new FileInputStream(file))) {
            AlertFactory.createError("Could not load XML.");
          } else {
            fxmlCtrl.refreshIslandSizeSpinners();
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private boolean loadXMLStreamToIsland(Island island, InputStream stream) {
    Island newIsland = new Island(1, 1);

    XMLEventReader xmlReader = null;
    try {
      xmlReader = XMLInputFactory.newInstance().createXMLEventReader(stream);

      while(xmlReader.hasNext()) {
        XMLEvent curEvent = xmlReader.nextEvent();
        switch (curEvent.getEventType()) {
          case XMLStreamConstants.START_DOCUMENT:
            break;
          case XMLStreamConstants.START_ELEMENT:
            StartElement element = curEvent.asStartElement();
            String elementName = element.getName().getLocalPart();
            switch (elementName) {
              case "island":
                try {
                  int width = Integer.parseInt(
                      element.getAttributeByName(new QName("width")).getValue());
                  int height = Integer.parseInt(
                      element.getAttributeByName(new QName("height")).getValue());
                  newIsland.setSize(width, height);
                } catch (Exception exc) {
                  return false;
                }
                break;
              case "tree":
                try {
                  int x = Integer.parseInt(element.getAttributeByName(new QName("x")).getValue());
                  int y = Integer.parseInt(element.getAttributeByName(new QName("y")).getValue());
                  newIsland.setPhysObjectAt(new Position(x, y), new Tree());
                } catch (Exception exc) {
                  return false;
                }
                break;
              case "stump":
                try {
                  int x = Integer.parseInt(element.getAttributeByName(new QName("x")).getValue());
                  int y = Integer.parseInt(element.getAttributeByName(new QName("y")).getValue());
                  boolean hasWoodOnTop = Boolean.parseBoolean(
                      element.getAttributeByName(new QName("woodOnTop")).getValue());
                  Stump stump = new Stump();
                  if (hasWoodOnTop) {
                    stump.setWoodOnTop(new Wood());
                  }
                  newIsland.setPhysObjectAt(new Position(x, y), stump);
                } catch (Exception exc) {
                  return false;
                }
                break;
              case "wood":
                try {
                  int x = Integer.parseInt(element.getAttributeByName(new QName("x")).getValue());
                  int y = Integer.parseInt(element.getAttributeByName(new QName("y")).getValue());
                  newIsland.setPhysObjectAt(new Position(x, y), new Wood());
                } catch (Exception exc) {
                  return false;
                }
                break;
              case "ocean":
                try {
                  int x = Integer.parseInt(element.getAttributeByName(new QName("x")).getValue());
                  int y = Integer.parseInt(element.getAttributeByName(new QName("y")).getValue());
                  newIsland.setOceanAt(new Position(x, y));
                } catch (Exception exc) {
                  return false;
                }
                break;
              case "actor":
                try {
                  int x = Integer.parseInt(element.getAttributeByName(new QName("x")).getValue());
                  int y = Integer.parseInt(element.getAttributeByName(new QName("y")).getValue());
                  Direction dir = Direction.valueOf(
                      element.getAttributeByName(new QName("dir")).getValue());
                  newIsland.setActorPosition(new Position(x, y));
                  newIsland.setActorDirection(dir);
                } catch (Exception exc) {
                  return false;
                }
                break;
              default:
                break;
            }
            break;
          case XMLStreamConstants.END_DOCUMENT:
            xmlReader.close();
            break;
          default:
            break;
        }
      }

      island.replaceBy(newIsland);
      return true;

    } catch (XMLStreamException e) {
      e.printStackTrace();
    } finally {
      if(xmlReader != null) {
        try {
          xmlReader.close();
        } catch (XMLStreamException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  public boolean saveIslandXMLToStream(Island island, OutputStream stream) {
    XMLStreamWriter xmlWriter = null;
    try {
      xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);

      xmlWriter.writeStartDocument("utf-8", "1.0");
      xmlWriter.writeCharacters("\n\n");
      xmlWriter.writeDTD(getDtd());
        xmlWriter.writeCharacters("\n\n");

        synchronized (island) {
          int width = island.getWidth();
          int height = island.getHeight();
          xmlWriter.writeStartElement("island");
          xmlWriter.writeAttribute("width", String.valueOf(width));
          xmlWriter.writeAttribute("height", String.valueOf(height));
          xmlWriter.writeCharacters("\n");

          for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
              Position curPos = new Position(x, y);
              Optional<PhysicalObject> physObjOpt = island.getPhysObjectAt(curPos);
              if (physObjOpt.isPresent()) {
                PhysicalObject physObj = physObjOpt.get();

                xmlWriter.writeStartElement(physObj.getClass().getSimpleName().toLowerCase());
                xmlWriter.writeAttribute("x", String.valueOf(x));
                xmlWriter.writeAttribute("y", String.valueOf(y));
                if (physObj instanceof Stump stump) {
                  xmlWriter.writeAttribute("woodOnTop", String.valueOf(stump.hasWoodOnTop()));
                }
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters("\n");
              } else if (island.hasOceanAt(curPos)) {
                xmlWriter.writeStartElement("ocean");
                xmlWriter.writeAttribute("x", String.valueOf(x));
                xmlWriter.writeAttribute("y", String.valueOf(y));
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters("\n");
              }
            }
          }

          Position actorPos = island.getActorPosition();
          xmlWriter.writeStartElement("actor");
          xmlWriter.writeAttribute("x", String.valueOf(actorPos.getX()));
          xmlWriter.writeAttribute("y", String.valueOf(actorPos.getY()));
          xmlWriter.writeAttribute("dir", String.valueOf(island.getActorDirection()));
          xmlWriter.writeEndElement();
          xmlWriter.writeCharacters("\n");

          xmlWriter.writeEndElement();
          xmlWriter.writeCharacters("\n");

          xmlWriter.writeEndDocument();
        }
      return true;
    } catch (XMLStreamException e) {
      e.printStackTrace();
    } finally {
      if(xmlWriter != null) {
        try {
          xmlWriter.close();
        } catch (XMLStreamException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }


  public String getIslandXMLString() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    saveIslandXMLToStream(island, outputStream);
    return outputStream.toString(StandardCharsets.UTF_8);
  }

  public boolean setIslandFromXMLString(String xmlString) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
    return loadXMLStreamToIsland(island, inputStream);
  }

  private String getDtd() {
    return """
<!DOCTYPE island [
<!ELEMENT island ((tree | stump | wood | ocean)*, actor)>
<!ATTLIST island
  width CDATA #REQUIRED
  height CDATA #REQUIRED
>
<!ELEMENT tree EMPTY>
<!ATTLIST tree
  x CDATA #REQUIRED
  y CDATA #REQUIRED
>
<!ELEMENT stump EMPTY>
<!ATTLIST stump
  x CDATA #REQUIRED
  y CDATA #REQUIRED
  woodOnTop CDATA #REQUIRED
>
<!ELEMENT wood EMPTY>
<!ATTLIST wood
  x CDATA #REQUIRED
  y CDATA #REQUIRED
>
<!ELEMENT ocean EMPTY>
<!ATTLIST ocean
  x CDATA #REQUIRED
  y CDATA #REQUIRED
>
<!ELEMENT actor EMPTY>
<!ATTLIST actor
  x CDATA #REQUIRED
  y CDATA #REQUIRED
  dir CDATA #REQUIRED
>
]>
""";
  }
}
