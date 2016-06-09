import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

/**
 * Created by RobertAtkins on 5/30/16.
 */
class EntityMap<K extends Integer, V extends Entity> extends Map<K,V>
{

    ObservableList<Entity> getAllEntities()
    {
        ArrayList<Entity> list = new ArrayList<>();
        list.addAll(values());
        list.remove(Entity.NO_OWNER);
        list.remove(Entity.NO_PROPERTY_MANAGER);
        list.remove(Entity.NO_PURCHASER);
        return FXCollections.observableList(list);
    }
}
