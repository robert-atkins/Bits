import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

/**
 * Created by RobertAtkins on 5/29/16.
 */
public class Bits extends Application
{
    // Property object used to save objects to file
    static final Properties BITS_PROPERTIES = new Properties();

    // Total number of Assets written to JDF file
    static final IntegerProperty TOTAL_ASSETS_WRITTEN = new SimpleIntegerProperty(0);

    // Total number of Entities written to JDF file
    static final IntegerProperty TOTAL_ENTITIES_WRITTEN = new SimpleIntegerProperty(0);

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Entity.setEmptyEntities();
        Encryption.loadFile();
        InventoryPane inventoryPane = new InventoryPane();
        Scene scene = new Scene(inventoryPane);
        primaryStage.setTitle("Bits");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
