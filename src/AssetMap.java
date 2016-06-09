import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RobertAtkins on 4/18/16.
 */ 
class AssetMap<A extends Asset,E extends Entity> extends Map<A, E>
{
    ObservableList<Asset> getAllAssets()
    {
        List<Asset> list = new ArrayList<>();
        list.addAll(keySet());
        ObservableList data = FXCollections.observableList(list);
        return data;
    }

    ObservableList<String> getAllLocations()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getLocation());
        return results;
    }

    ObservableList<String> getAllTypes()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getType());
        return results;
    }
    ObservableList<String> getAllManufacturers()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getManufacturer());
        return results;
    }
    ObservableList<String> getAllModels()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getModel());
        return results;
    }
    ObservableList<String> getAllPurchaseDates()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getPurchaseDate().toString());
        return results;
    }
    ObservableList<String> getAllWarrantyExpiries()
    {
        ObservableList<String> results = FXCollections.observableArrayList();
        for(Asset a : keySet())
            results.add(a.getWarrantyExpiryDate().toString());
        return results;
    }
}
