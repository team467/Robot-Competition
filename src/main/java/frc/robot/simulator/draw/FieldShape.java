package frc.robot.simulator.draw;

import javafx.scene.image.*;
import java.util.ArrayList;
import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

public class FieldShape {

  public static final double PIXELS_PER_MAP_INCH = 1.0;

  public static final double FIELD_OFFSET_X =  1.5 * 12.0;

  public static final double FIELD_OFFSET_Y = 10.0 * 12.0;

  public static final double ODD_Y_OFFSET = 37.0;
    
  
  Image fieldImage = null;
public FieldShape(){
    super();
    //loads images and applies them
    File resourceLocation = new File("src/main/deploy/Field.png");
    try{
    fieldImage = new Image(resourceLocation.toURI().toURL().toString());
    }catch(Exception e){

    }
}
  private GraphicsContext context;
  public ArrayList<Shape> fieldPieces = new ArrayList<Shape>();
  public void context(GraphicsContext context) {
    this.context = context;
  }

  /**
   * Draws the field
   * Field sizes in width x height feet
   *  Field: 29' x 74' (27' x 54" internal)
   *  Alliance Station: 22x10
   *  Auto Line: 27x2 (10 feet from alliance walls)
   *  Exchange zone: 4x3 (2 inch tape)
   *    Exchange hole is 1' 9" wide
   *  Null Territory: 7ft 11.25in x 6 (2 inch tape)
   *  Platform Zone: 11' 1.5" x 9' 11.75" [2' tape alliance color]
   *  Portal: 4' x 12' 11"
   *    10' on short width
   *  Wall 1' 6" wide?
   *  Power Cube Zone: 3' 9" x 3' 6"
   *  Starting Line: 27' x 2" White tape, 2 ' 6" behind alliance wall
   *  Player Station: 5' 9" x 1'
   *  Scale
   *    15' from end to end
   *    Plate is 3' x 4'
   *    Platform top is 8' 8" x 3' 5.25"
   *    Ramp is 1' 1"
   *  Switch
   *    14' from Alliance Station to middle of switch
   *    Plates are 3 ' x 4'
   *    Switch is 4' 8" x 12'
   *  Portal
   *    1' 2" square opening
   *  Power cube is 1' 1" x 1' 1" x 11"
   */
  public void drawBaseField() {
      //draws field thing
      context.drawImage(fieldImage, 
      100.0*PIXELS_PER_MAP_INCH, 
      12.0*PIXELS_PER_MAP_INCH, 
      54.0*12.0*PIXELS_PER_MAP_INCH, 
      27.0*12.0*PIXELS_PER_MAP_INCH);
  }

  
  public void buildFieldPieces(ObservableList<Node> stuffOnField){

  }

}
