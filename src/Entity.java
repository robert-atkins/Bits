import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by RobertAtkins on 5/29/16.
 */
class Entity
{
    // Array indexes of Entity object properties when converted to String[]
    static final int ENTITY_KEY = 0;
    static final int ENTITY_CUSTOM_ID = 1;
    static final int ENTITY_FIRST_NAME = 2;
    static final int ENTITY_LAST_NAME = 3;
    static final int ENTITY_EMAIL = 4;
    static final int ENTITY_LOCATION = 5;
    static final int ENTITY_PHONE = 6;
    static final int ENTITY_DIVISION = 7;
    
    // No <Owner, Property Manager, Purchaser> Entity objects
    static final Entity NO_OWNER = new Entity(new String[]{
            "0", "NONE","NO","OWNER","NONE","NONE","0","NONE" });
    static final Entity NO_PROPERTY_MANAGER = new Entity(new String[]{
            "1", "NONE","NO","PROPERTY MANAGER","NONE","NONE","0","NONE" });
    static final Entity NO_PURCHASER = new Entity(new String[]{
            "2", "NONE","NO","PURCHASER","NONE","NONE","0","NONE" });
    
    // Set NO_ Entity object properties
    static void setEmptyEntities()
    {
        map.put(NO_OWNER.key.getValue(), NO_OWNER);
        map.put(NO_PROPERTY_MANAGER.key.getValue(), NO_PROPERTY_MANAGER);
        map.put(NO_PURCHASER.key.getValue(), NO_PURCHASER);
        Bits.TOTAL_ENTITIES_WRITTEN.set(3);
    }

    // Map of Entity.key to Entity object
    private static EntityMap<Integer, Entity> map = new EntityMap<>();

    // Map accessor method
    static EntityMap<Integer, Entity> getMap()
    {
        return map;
    }

    // Entity object properties
    private IntegerProperty key = new SimpleIntegerProperty();
    final int getKey(){return key.getValue();}
    final void setKey(int value){key.setValue(value);}
    IntegerProperty keyProperty(){return key;}

    private StringProperty customId = new SimpleStringProperty();
    final String getCustomId(){return customId.getValue();}
    final void setCustomId(String value){customId.setValue(value);}
    StringProperty customIdProperty(){return customId;}

    private StringProperty firstName = new SimpleStringProperty();
    final String getFirstName(){return firstName.getValue();}
    final void setFirstName(String value){firstName.setValue(value);}
    StringProperty firstNameProperty(){return firstName;}

    private StringProperty lastName = new SimpleStringProperty();
    final String getLastName(){return lastName.getValue();}
    final void setLastName(String value){lastName.setValue(value);}
    StringProperty lastNameProperty(){return lastName;}

    private StringProperty fullName = new SimpleStringProperty();
    final String getFullName(){return getFirstName() + " " + getLastName();}
    final void setFullName(){fullName.setValue(getFirstName() + " " + getLastName());}
    StringProperty fullNameProperty(){return fullName;}

    private StringProperty email = new SimpleStringProperty();
    final String getEmail(){return email.getValue();}
    final void setEmail(String value){email.setValue(value);}
    StringProperty emailProperty(){return email;}

    private StringProperty location = new SimpleStringProperty();
    final String getLocation(){return location.getValue();}
    final void setLocation(String value){location.setValue(value);}
    StringProperty locationProperty(){return location;}

    private LongProperty phone = new SimpleLongProperty();
    final Long getPhone(){return phone.getValue();}
    final void setPhone(Long value){phone.setValue(value);}
    LongProperty phoneProperty(){return phone;}

    private StringProperty division = new SimpleStringProperty();
    final String getDivision(){return division.getValue();}
    final void setDivision(String value){division.setValue(value);}
    StringProperty divisionProperty(){return division;}

    Entity(){}

    private Entity(String[] entityProperties)
    {
        setKey(Integer.parseInt(entityProperties[ENTITY_KEY]));
        setCustomId(entityProperties[ENTITY_CUSTOM_ID]);
        setFirstName(entityProperties[ENTITY_FIRST_NAME]);
        setLastName(entityProperties[ENTITY_LAST_NAME]);
        setEmail(entityProperties[ENTITY_EMAIL]);
        setLocation(entityProperties[ENTITY_LOCATION]);
        setPhone(Long.parseLong(entityProperties[ENTITY_PHONE]));
        setDivision(entityProperties[ENTITY_DIVISION]);
        setFullName();
    }

    String toJdfString()
    {
        return "E::" + getKey() + "&!" +
                getCustomId() + "&!" +
                getFirstName() + "&!" +
                getLastName() + "&!" +
                getEmail() + "&!" +
                getLocation() + "&!" +
                getPhone() + "&!" +
                getDivision() + "&!";
    }

    @Override
    public String toString()
    {
        return getFullName();
    }
}
