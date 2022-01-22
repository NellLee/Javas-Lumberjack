package praktikum.fjt.nellsoneilersjavaslumberjack.model;

public class ExampleSetting {

  private final String editorContent;
  private final String islandContent;

  public ExampleSetting(String editorContent, String islandContent) {
    this.editorContent = editorContent;
    this.islandContent = islandContent;
  }

  public String getEditorContent() {
    return editorContent;
  }

  public String getIslandContent() {
    return islandContent;
  }
}
