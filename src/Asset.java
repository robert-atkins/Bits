import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.*;

/**
 * Created by RobertAtkins on 5/29/16.
 */
class Asset
{
    // Array indexes of Asset object properties when converted to String[]
    static final int ASSET_KEY = 0;
    static final int ASSET_TAG = 1;
    static final int ASSET_NAME = 2;
    static final int ASSET_OWNER_KEY = 3;
    static final int ASSET_LOCATION = 4;
    static final int ASSET_TYPE = 5;
    static final int ASSET_MANUFACTURER = 6;
    static final int ASSET_MODEL = 7;
    static final int ASSET_SERIAL = 8;
    static final int ASSET_PROPERTY_MANAGER_KEY = 9;
    static final int ASSET_PRICE = 10;
    static final int ASSET_PURCHASER_KEY = 11;
    static final int ASSET_PURCHASE_DATE = 12;
    static final int ASSET_WARRANTY_EXPIRY_DATE = 13;
    static final int ASSET_LAST_CHECKED_DATE = 14;

    // Entity objects used for retrieving names
    private Entity ownerE, propertyManagerE, purchaserE;

    // Map of Assets object to Asset.owner Entity object
    private static AssetMap<Asset, Entity> map = new AssetMap<>();

    // Map accessor method
    static AssetMap<Asset, Entity> getMap()
    {
        return map;
    }

    // Date formatter when creating Asset object
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Asset object properties
    private IntegerProperty key = new SimpleIntegerProperty(this, "key");
    final void setKey(int value){key.setValue(value);}
    final int getKey(){return key.getValue();}
    IntegerProperty keyProperty(){return key;}

    private StringProperty assetTag = new SimpleStringProperty(this, "assetTag");
    final void setAssetTag(String value){assetTag.setValue(value);}
    final String getAssetTag(){return assetTag.getValue();}
    StringProperty assetTagProperty(){return assetTag;}

    private StringProperty name = new SimpleStringProperty(this, "name");
    final void setName(String value){name.setValue(value);}
    final String getName(){return name.getValue();}
    StringProperty nameProperty(){return name;}

    private ObjectProperty<Entity> owner = new SimpleObjectProperty<>(this, "owner");
    final void setOwner(Entity value){owner.setValue(value); ownerE = value;}
    final Entity getOwner(){return owner.getValue();}
    ObjectProperty<Entity> ownerProperty(){return owner;}

    private StringProperty ownerFullName = new SimpleStringProperty(this, "ownerFullName");
    final void setOwnerFullName(){ownerFullName.setValue(ownerE.toString());}
    final String getOwnerFullName(){return ownerFullName.getValue();}
    StringProperty ownerFullNameProperty(){return ownerFullName;}

    private StringProperty location = new SimpleStringProperty(this, "location");
    final void setLocation(String value){location.setValue(value);}
    final String getLocation(){return location.getValue();}
    StringProperty locationProperty(){return location;}

    private StringProperty type = new SimpleStringProperty(this, "type");
    final void setType(String value){type.setValue(value);}
    final String getType(){return type.getValue();}
    StringProperty typeProperty(){return type;}

    private StringProperty manufacturer = new SimpleStringProperty(this, "manufacturer");
    final void setManufacturer(String value){manufacturer.setValue(value);}
    final String getManufacturer(){return manufacturer.getValue();}
    StringProperty manufacturerProperty(){return manufacturer;}

    private StringProperty model = new SimpleStringProperty(this, "model");
    final void setModel(String value){model.setValue(value);}
    final String getModel(){return model.getValue();}
    StringProperty modelProperty(){return model;}
    
    private StringProperty serial = new SimpleStringProperty(this, "serial");
    final void setSerial(String value){serial.setValue(value);}
    final String getSerial(){return serial.getValue();}
    StringProperty serialProperty(){return serial;}

    private ObjectProperty<Entity> propertyManager = new SimpleObjectProperty<>(this, "propertyManager");
    final void setPropertyManager(Entity value){propertyManager.setValue(value); propertyManagerE = value;}
    final Entity getPropertyManager(){return propertyManager.getValue();}
    ObjectProperty<Entity> propertyManagerProperty(){return propertyManager;}

    private StringProperty propertyManagerFullName = new SimpleStringProperty(this, "propertyManagerFullName");
    final void setPropertyManagerFullName(){propertyManagerFullName.setValue(propertyManagerE.toString());}
    final String getPropertyManagerFullName(){return propertyManagerFullName.getValue();}
    StringProperty propertyManagerFullNameProperty(){return propertyManagerFullName;}

    private DoubleProperty price = new SimpleDoubleProperty(this, "price");
    final double getPrice(){return price.getValue();}
    final void setPrice(Double value){price.setValue(value);}
    DoubleProperty priceProperty(){return price;}

    private ObjectProperty<Entity> purchaser = new SimpleObjectProperty<>(this, "purchaser");
    final void setPurchaser(Entity value){purchaser.setValue(value); purchaserE = value;}
    final Entity getPurchaser(){return purchaser.getValue();}
    ObjectProperty<Entity> purchaserProperty(){return purchaser;}

    private StringProperty purchaserFullName = new SimpleStringProperty(this, "purchaserFullName");
    final void setPurchaserFullName(){purchaserFullName.setValue(purchaserE.toString());}
    final String getPurchaserFullName(){return purchaserFullName.getValue();}
    StringProperty purchaserFullNameProperty(){return purchaserFullName;}

    private ObjectProperty<LocalDate> purchaseDate = new SimpleObjectProperty<>(this, "purchaseDate");
    final void setPurchaseDate(LocalDate value){purchaseDate.setValue(value);}
    final LocalDate getPurchaseDate(){return purchaseDate.getValue();}
    ObjectProperty<LocalDate> purchaseDateProperty(){return purchaseDate;}

    private ObjectProperty<LocalDate> warrantyExpiryDate = new SimpleObjectProperty<>(this, "warrantyExpiryDate");
    final void setWarrantyExpiryDate(LocalDate value){warrantyExpiryDate.setValue(value);}
    final LocalDate getWarrantyExpiryDate(){return warrantyExpiryDate.getValue();}
    ObjectProperty<LocalDate> warrantyExpiryDateProperty(){return warrantyExpiryDate;}

    private ObjectProperty<LocalDate> lastCheckedDate = new SimpleObjectProperty<>(this, "lastCheckedDate");
    final void setLastCheckedDate(LocalDate value){lastCheckedDate.setValue(value);}
    final LocalDate getLastCheckedDate(){return lastCheckedDate.getValue();}
    ObjectProperty<LocalDate> lastCheckedDateProperty(){return lastCheckedDate;}

    void setEntities()
    {
        ownerE = owner.getValue();
        setOwnerFullName();
        propertyManagerE = propertyManager.getValue();
        setPropertyManagerFullName();
        purchaserE = purchaser.getValue();
        setPurchaserFullName();
    }

    Asset(){}

    String toJdfString()
    {
        return "A::" + getKey() + "&!" +
                getAssetTag() + "&!" +
                getName() + "&!" +
                getOwner().getKey() + "&!" +
                getLocation() + "&!" +
                getType() + "&!" +
                getManufacturer() + "&!" +
                getModel() + "&!" +
                getSerial() + "&!" +
                getPropertyManager().getKey() + "&!" +
                getPrice() + "&!" +
                getPurchaser().getKey() + "&!" +
                getPurchaseDate().toString() + "&!" +
                getWarrantyExpiryDate().toString() + "&!" +
                getLastCheckedDate().toString();
    }
}
