import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.util.*;
import static javafx.collections.FXCollections.*;

/**
 * Created by RobertAtkins on 5/29/16.
 */
class InventoryPane extends VBox
{

    // Dialog codes
    private static final int NEW = 0; // All fields enabled
    private static final int VIEW = 1; // No fields enabled, commit<T>Button hidden & disabled
    private static final int EDIT = 2; // All fields enabled, commit<T>Button action updates <T> record

    /** Display the about information dialog
     */
    private void aboutMenuItemAction(ActionEvent e)
    {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Bits");
        about.setHeaderText("Bits - Basic Inventory Tracking System");
        String s = "Bits was created to be a lightweight and easy-to-use asset management program for institutions to track property.";
        about.setContentText(s);
        about.show();
    }

    private void exportAssetsAction(ActionEvent ae, ObservableList<Asset> assets)
    {
        String userHome = System.getProperty("user.home");
        FileChooser export = new FileChooser();

        export.setInitialDirectory(new File(userHome + "/Desktop"));
        export.setTitle("Export Assets");
        export.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File csv = export.showSaveDialog(getScene().getWindow());
        if(csv != null)
        {
            try
            {
                FileWriter csvWriter = new FileWriter(csv, false);
                csvWriter.write("KEY");
                csvWriter.write(',');
                csvWriter.write("TAG");
                csvWriter.write(',');
                csvWriter.write("OWNER");
                csvWriter.write(',');
                csvWriter.write("LOCATION");
                csvWriter.write(',');
                csvWriter.write("TYPE");
                csvWriter.write(',');
                csvWriter.write("MANUFACTURER");
                csvWriter.write(',');
                csvWriter.write("SERIAL");
                csvWriter.write(',');
                csvWriter.write("PROPERTY MANAGER");
                csvWriter.write(',');
                csvWriter.write("PRICE");
                csvWriter.write(',');
                csvWriter.write("PURCHASER");
                csvWriter.write(',');
                csvWriter.write("PURCHASE DATE");
                csvWriter.write(',');
                csvWriter.write("WARRANTY EXPIRY DATE");
                csvWriter.write(',');
                csvWriter.write("LAST CHECKED DATE");
                csvWriter.write('\n');

                for(Asset a : assets)
                {
                    csvWriter.write(Integer.toString(a.getKey()));
                    csvWriter.write(',');
                    csvWriter.write(a.getAssetTag());
                    csvWriter.write(',');
                    csvWriter.write(a.getOwnerFullName());
                    csvWriter.write(',');
                    csvWriter.write(a.getLocation());
                    csvWriter.write(',');
                    csvWriter.write(a.getType());
                    csvWriter.write(',');
                    csvWriter.write(a.getManufacturer());
                    csvWriter.write(',');
                    csvWriter.write(a.getSerial());
                    csvWriter.write(',');
                    csvWriter.write(a.getPropertyManagerFullName());
                    csvWriter.write(',');
                    csvWriter.write(Double.toString(a.getPrice()));
                    csvWriter.write(',');
                    csvWriter.write(a.getPurchaserFullName());
                    csvWriter.write(',');
                    csvWriter.write(a.getPurchaseDate().toString());
                    csvWriter.write(',');
                    csvWriter.write(a.getWarrantyExpiryDate().toString());
                    csvWriter.write(',');
                    csvWriter.write(a.getLastCheckedDate().toString());
                    csvWriter.write('\n');
                }

                csvWriter.flush();
                csvWriter.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void exportAllEntitiesAction(ActionEvent ae)
    {
        FileChooser export = new FileChooser();
        export.setTitle("Export Entities");
        export.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File csv = export.showSaveDialog(getScene().getWindow());
        if(csv != null)
        {
            try
            {
                FileWriter csvWriter = new FileWriter(csv, false);
                csvWriter.write("KEY");
                csvWriter.write(',');
                csvWriter.write("CUSTOM ID");
                csvWriter.write(',');
                csvWriter.write("FIRST NAME");
                csvWriter.write(',');
                csvWriter.write("LAST NAME");
                csvWriter.write(',');
                csvWriter.write("EMAIL");
                csvWriter.write(',');
                csvWriter.write("LOCATION");
                csvWriter.write(',');
                csvWriter.write("PHONE");
                csvWriter.write(',');
                csvWriter.write("DIVISION");
                csvWriter.write('\n');

                for(Entity e : Entity.getMap().getAllEntities())
                {
                    csvWriter.write(Integer.toString(e.getKey()));
                    csvWriter.write(',');
                    csvWriter.write(e.getCustomId());
                    csvWriter.write(',');
                    csvWriter.write(e.getFirstName());
                    csvWriter.write(',');
                    csvWriter.write(e.getLastName());
                    csvWriter.write(',');
                    csvWriter.write(e.getEmail());
                    csvWriter.write(',');
                    csvWriter.write(e.getLocation());
                    csvWriter.write(',');
                    csvWriter.write(Long.toString(e.getPhone()));
                    csvWriter.write(',');
                    csvWriter.write(e.getDivision());
                    csvWriter.write('\n');
                }

                csvWriter.flush();
                csvWriter.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Take an asset object and populate the asset editor dialog with the
     * asset's properties. The dialogType parameter is used to determine the
     * boolean parameters passed to the private assetEditorDialog method.
     *
     * @param a Asset used to populate fields.
     * @param type Type of assetEditorDialog to display (0 - NEW, 1 - EDIT, 2 - VIEW)
     */
    private void assetDialogButtonAction(ActionEvent ae, Asset a, int type)
    {
        boolean isNew = false;
        boolean editable = false;

        switch(type){
            case NEW:
                isNew = true;
                editable = true;
                break;
            case EDIT:
                isNew = false;
                editable = true;
                break;
            case VIEW:
                isNew = false;
                editable = false;
                break;
        }

        /* This Section creates the labels and text fields in the in the dialog*/
        Label assetNameLabel = new Label("Asset Name:");
        TextField assetNameField = new TextField();

        Label assetTagLabel = new Label("Asset Tag:");
        TextField assetTagField = new TextField();

        Label ownerLabel = new Label("Owner:");
        TextField ownerField = new TextField();
        Button ownerButton = new Button("Select");

        Label locationLabel = new Label("Location:");
        TextField locationField = new TextField();

        Label typeLabel = new Label("Type:");
        TextField typeField = new TextField();

        Label manufacturerLabel = new Label("Manufacturer:");
        TextField manufacturerField = new TextField();

        Label modelLabel = new Label("Model:");
        TextField modelField = new TextField();

        Label serialLabel = new Label("Serial:");
        TextField serialField = new TextField();

        Label propertyManagerLabel = new Label("Property Manager:");
        TextField propertyManagerField = new TextField();
        Button propertyManagerButton = new Button("Select");

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();

        Label purchaserLabel = new Label("Purchaser:");
        TextField purchaserField = new TextField();
        Button purchaserButton = new Button("Select");

        Label purchaseDateLabel = new Label("Purchase Date:");
        DatePicker purchaseDatePicker = new DatePicker();

        Label warrantyExpiryLabel = new Label("Warranty Expiry Date:");
        DatePicker warrantyExpiryDatePicker = new DatePicker();

        Label lastCheckedDateLabel = new Label("Last Checked Date:" );
        DatePicker lastCheckedDatePicker = new DatePicker();

        HBox columnContainer = new HBox(); // Contains column1 and column2
        HBox ownerNodesBox = new HBox(); // The button, label, textfield for owner
        HBox propertyManagerNodesBox = new HBox(); // The button, label, textfield for property manager
        HBox purchaserNodesBox = new HBox(); // The button, label, textfield for purchaser

        VBox column1 = new VBox();
        VBox column2 = new VBox();

        Dialog assetDialog = new Dialog();

        if(editable)
        {
            assetNameField.setDisable(!editable);
            ownerField.setDisable(false);
            ownerField.setEditable(false);
            ownerButton.setDisable(!editable);
            assetTagField.setDisable(!editable);
            locationField.setDisable(!editable);
            typeField.setDisable(!editable);
            manufacturerField.setDisable(!editable);
            modelField.setDisable(!editable);
            serialField.setDisable(!editable);
            propertyManagerField.setDisable(false);
            propertyManagerField.setEditable(false);
            propertyManagerButton.setDisable(!editable);
            priceField.setDisable(!editable);
            purchaserField.setDisable(false);
            purchaserField.setEditable(false);
            purchaserButton.setDisable(!editable);
            purchaseDatePicker.setDisable(false);
            warrantyExpiryDatePicker.setEditable(false);
            purchaseDatePicker.setDisable(!editable);
            warrantyExpiryDatePicker.setEditable(false);
            warrantyExpiryDatePicker.setDisable(!editable);
            lastCheckedDatePicker.setEditable(false);
            lastCheckedDatePicker.setDisable(!editable);
        }

        if(a.ownerProperty().getValue() != null)
        {
            assetNameField.setText(a.getName());
            ownerField.setText(a.getOwner().getFullName());
            assetTagField.setText(a.getAssetTag());
            locationField.setText(a.getLocation());
            typeField.setText(a.getType());
            manufacturerField.setText(a.getManufacturer());
            modelField.setText(a.getModel());
            serialField.setText(a.getSerial());
            propertyManagerField.setText(a.getPropertyManager().getFullName());
            priceField.setText(Double.toString(a.getPrice()));
            purchaserField.setText(a.getPurchaser().getFullName());
            purchaseDatePicker.setValue(a.getPurchaseDate());
            warrantyExpiryDatePicker.setValue(a.getWarrantyExpiryDate());
            lastCheckedDatePicker.setValue(a.getLastCheckedDate());
        }

        ownerButton.setOnAction(action -> {
            Entity e = entitySelectorDialogAction();
            if(e != null)
            {
                a.setOwner(e);
                ownerField.setText(e.getFullName());
            }
        });
        propertyManagerButton.setOnAction(action -> {
            Entity e = entitySelectorDialogAction();
            if(e != null)
            {
                a.setPropertyManager(e);
                propertyManagerField.setText(e.getFullName());
            }
        });
        purchaserButton.setOnAction(action -> {
            Entity e = entitySelectorDialogAction();
            if(e != null)
            {
                a.setPurchaser(e);
                purchaserField.setText(e.getFullName());
            }
        });

        ownerNodesBox.setSpacing(5);
        propertyManagerNodesBox.setSpacing(5);
        purchaserNodesBox.setSpacing(5);

        String commitText = "Add Asset";
        String cancelText = "Cancel";
        if(!isNew)
        {
            commitText = "Update";
            if(!editable)
            {
                cancelText = "Close";
            }
        }

        ButtonType commit = new ButtonType(commitText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);
        assetDialog.getDialogPane().getButtonTypes().addAll(cancel, commit);

        if(editable)
        {
            ownerNodesBox.getChildren().addAll(ownerField, ownerButton);
            propertyManagerNodesBox.getChildren().addAll(propertyManagerField, propertyManagerButton);
            purchaserNodesBox.getChildren().addAll(purchaserField, purchaserButton);
        }
        else
        {
            ownerNodesBox.getChildren().add(ownerField);
            propertyManagerNodesBox.getChildren().add(propertyManagerField);
            purchaserNodesBox.getChildren().add(purchaserField);
            assetDialog.getDialogPane().getButtonTypes().remove(commit);
        }

        column1.setSpacing(5); // Column 1
        column1.getChildren().addAll(
                assetNameLabel,
                assetNameField,
                assetTagLabel,
                assetTagField,
                ownerLabel,
                ownerNodesBox,
                locationLabel,
                locationField,
                typeLabel,
                typeField,
                manufacturerLabel,
                manufacturerField,
                modelLabel,
                modelField
        );

        column2.setSpacing(5); // Column 2
        column2.getChildren().addAll(
                serialLabel,
                serialField,
                propertyManagerLabel,
                propertyManagerNodesBox,
                priceLabel,
                priceField,
                purchaserLabel,
                purchaserNodesBox,
                purchaseDateLabel,
                purchaseDatePicker,
                warrantyExpiryLabel,
                warrantyExpiryDatePicker,
                lastCheckedDateLabel,
                lastCheckedDatePicker
        );

        columnContainer.setSpacing(5);
        columnContainer.getChildren().addAll(column1, column2);

        boolean finalIsNew = isNew;
        assetDialog.setResultConverter(dialogButton -> {
            if(dialogButton == commit)
            {
                if(finalIsNew)
                {
                    final Asset newAsset = new Asset();
                    Bits.TOTAL_ASSETS_WRITTEN.setValue(Bits.TOTAL_ASSETS_WRITTEN.getValue() + 1);
                    newAsset.setKey(Bits.TOTAL_ASSETS_WRITTEN.getValue());
                    newAsset.setName(assetNameField.getText());
                    newAsset.setAssetTag(assetTagField.getText());
                    newAsset.setOwner(a.getOwner());
                    newAsset.setLocation(locationField.getText());
                    newAsset.setType(typeField.getText());
                    newAsset.setManufacturer(manufacturerField.getText());
                    newAsset.setModel(modelField.getText());
                    newAsset.setSerial(serialField.getText());
                    newAsset.setPropertyManager(a.getPropertyManager());
                    newAsset.setPrice(Double.parseDouble(priceField.getText()));
                    newAsset.setPurchaseDate(purchaseDatePicker.getValue());
                    newAsset.setPurchaser(a.getPurchaser());
                    newAsset.setWarrantyExpiryDate(warrantyExpiryDatePicker.getValue());
                    newAsset.setLastCheckedDate(lastCheckedDatePicker.getValue());

                    newAsset.setEntities();

                    Asset.getMap().put(newAsset, newAsset.getOwner());
                } else {
                    a.setName(assetNameField.getText());
                    a.setAssetTag(assetTagField.getText());
                    a.setLocation(locationField.getText());
                    a.setType(typeField.getText());
                    a.setManufacturer(manufacturerField.getText());
                    a.setModel(modelField.getText());
                    a.setSerial(serialField.getText());
                    a.setPrice(Double.parseDouble(priceField.getText()));
                    a.setPurchaseDate(purchaseDatePicker.getValue());
                    a.setWarrantyExpiryDate(warrantyExpiryDatePicker.getValue());
                    a.setLastCheckedDate(lastCheckedDatePicker.getValue());
                    a.setEntities();

                    Asset.getMap().put(a, a.getOwner());
                }

                Tree.setTrees();
                Encryption.saveJDF();
                return null;
            } else if (dialogButton == cancel){
                assetDialog.close();
            }
            return null;
        });

        assetDialog.getDialogPane().setContent(columnContainer);
        assetDialog.setOnCloseRequest(event -> assetDialog.close());

        assetDialog.showAndWait();
    }

    /*
    private void importAssetsButtonAction(ActionEvent ae)
    {
        String userHome = System.getProperty("user.home");
        FileChooser export = new FileChooser();

        export.setInitialDirectory(new File(userHome + "/Desktop"));
        export.setTitle("Export Assets");
        export.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File csv = export.showSaveDialog(getScene().getWindow());
        String line = "";
        Stack<String[]> assets = new Stack<>();
        if(csv != null)
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(csv));
                while ((line = br.readLine()) != null)
                {
                    // use comma as separator
                    String[] properties = line.split(",");
                    assets.push(properties);
                    checkProperties
                }
            } catch(IOException e)
            {
                e.printStackTrace();
            }
            for(String[] a : assets)
            {

            }
        }
    }

    private void importEntitiesButtonAction(ActionEvent ae)
    {
        String userHome = System.getProperty("user.home");
        FileChooser export = new FileChooser();

        export.setInitialDirectory(new File(userHome + "/Desktop"));
        export.setTitle("Export Assets");
        export.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));

        File csv = export.showSaveDialog(getScene().getWindow());
        String line = "";
        if(csv != null)
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(csv));
                while ((line = br.readLine()) != null) {

                    // use comma as separator
                    String[] country = line.split(",");

                    System.out.println("Country [code= " + country[4]
                            + " , name=" + country[5] + "]");

                }
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }*/

    private void entityDialogButtonAction(ActionEvent ae, Entity e, int type)
    {
        boolean isNew = false;
        boolean editable = false;

        switch(type)
        {
            case NEW:
                isNew = true;
                editable = true;
                break;
            case EDIT:
                isNew = false;
                editable = true;
                break;
            case VIEW:
                isNew = false;
                editable = false;
                break;
        }

        Label customIdLabel = new Label("Entity ID:");
        TextField customIdField = new TextField();

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label locationLabel = new Label("Location:");
        TextField locationField = new TextField();

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();

        Label divisionLabel = new Label("Division:");
        TextField divisionField = new TextField();

        customIdField.setEditable(editable);
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        emailField.setEditable(editable);
        locationField.setEditable(editable);
        phoneField.setEditable(editable);
        divisionField.setEditable(editable);

        if(!isNew)
        {
            customIdField.setText(e.getCustomId());
            firstNameField.setText(e.getFirstName());
            lastNameField.setText(e.getLastName());
            emailField.setText(e.getEmail());
            locationField.setText(e.getLocation());
            phoneField.setText(e.getPhone().toString());
            divisionField.setText(e.getDivision());
        }

        VBox container = new VBox();
        container.setSpacing(5);
        container.getChildren().addAll(
                customIdLabel,
                customIdField,
                firstNameLabel,
                firstNameField,
                lastNameLabel,
                lastNameField,
                emailLabel,
                emailField,
                locationLabel,
                locationField,
                phoneLabel,
                phoneField,
                divisionLabel,
                divisionField
        );

        Dialog entityEditorDialog = new Dialog();

        String commitText = "Add Entity";
        String cancelText = "Cancel";
        if(!isNew)
        {
            commitText = "Update";
            if(!editable)
            {
                cancelText = "Close";
            }
        }

        ButtonType commit = new ButtonType(commitText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);

        entityEditorDialog.getDialogPane().getButtonTypes().addAll(commit, cancel);

        if(!editable)
        {
            entityEditorDialog.getDialogPane().getButtonTypes().remove(commit);
        }

        boolean finalIsNew = isNew;
        entityEditorDialog.setResultConverter(dialogButton -> {
            if(dialogButton == commit)
            {
                if(finalIsNew)
                {
                    Bits.TOTAL_ENTITIES_WRITTEN.setValue(Bits.TOTAL_ENTITIES_WRITTEN.getValue() + 1);
                    e.setKey(Bits.TOTAL_ENTITIES_WRITTEN.getValue());
                }
                e.setCustomId(customIdField.getText());
                e.setFirstName(firstNameField.getText());
                e.setLastName(lastNameField.getText());
                e.setEmail(emailField.getText());
                e.setLocation(locationField.getText());
                e.setPhone(Long.parseLong(phoneField.getText()));
                e.setDivision(divisionField.getText());

                Entity.getMap().put(e.getKey(), e);
                Tree.setTrees();
                Encryption.saveJDF();
                return null;
            } else if (dialogButton == cancel){
                entityEditorDialog.close();
            }
            return null;
        });

        entityEditorDialog.getDialogPane().setContent(container);
        entityEditorDialog.setOnCloseRequest(event -> entityEditorDialog.close());

        entityEditorDialog.showAndWait();
    }

    private Entity entitySelectorDialogAction()
    {
        Dialog<Entity> selector = new Dialog<>();
        selector.setTitle("Entity Selector");

        ButtonType confirm = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Button newEntity = new Button("New");
        Button editEntity = new Button("Edit");
        Button viewEntity = new Button("View");
        Button deleteEntity = new Button("Delete");
        ObservableList<Entity> entities = Entity.getMap().getAllEntities();

        ListView<Entity> entityListView = new ListView<>(entities);
        entityListView.setItems(entities);

        selector.getDialogPane().getButtonTypes().addAll(confirm, cancel);

        Node confirmButton = selector.getDialogPane().lookupButton(confirm);
        confirmButton.setDisable(true);

        // Disable the edit, view, delete, and confirm buttons until an entity is selected
        editEntity.disableProperty().bind(Bindings.isEmpty(entityListView.getSelectionModel().getSelectedItems()));
        viewEntity.disableProperty().bind(Bindings.isEmpty(entityListView.getSelectionModel().getSelectedItems()));
        deleteEntity.disableProperty().bind(Bindings.isEmpty(entityListView.getSelectionModel().getSelectedItems()));
        confirmButton.disableProperty().bind(Bindings.isEmpty(entityListView.getSelectionModel().getSelectedItems()));

        // Set button actions
        newEntity.setOnAction(a -> {
            entityDialogButtonAction(a, new Entity(), NEW);
            entityListView.setItems(Entity.getMap().getAllEntities());
        });
        editEntity.setOnAction(a -> {
            entityDialogButtonAction(a, entityListView.getSelectionModel().getSelectedItem(), EDIT);
            entityListView.setItems(Entity.getMap().getAllEntities());
        });
        viewEntity.setOnAction(a -> {
            entityDialogButtonAction(a, entityListView.getSelectionModel().getSelectedItem(), VIEW);
            entityListView.setItems(Entity.getMap().getAllEntities());
        });
        deleteEntity.setOnAction(a ->
        {
            Entity e = entityListView.getSelectionModel().getSelectedItem();
            entityDeleteWarning(e);
            entityListView.setItems(Entity.getMap().getAllEntities());
        });

        // Return the selected entity or null if the dialog is cancelled
        selector.setResultConverter(dialogButton -> {
            if(dialogButton == cancel)
            {
                return null;
            } else {
                return entityListView.getSelectionModel().getSelectedItem();
            }
        });

        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.getChildren().addAll(newEntity, editEntity, viewEntity, deleteEntity);

        VBox buttonsAndBox = new VBox();
        buttonsAndBox.setSpacing(5);
        buttonsAndBox.getChildren().addAll(entityListView, buttons);

        selector.getDialogPane().setContent(buttonsAndBox);
        selector.setOnCloseRequest(event -> selector.close());

        Entity returnEntity = null;
        Optional<Entity> result = selector.showAndWait();

        if(result.isPresent())
        {
            returnEntity = result.get();
        }

        return returnEntity;
    }

    /** Private
     * @param entity
     */
    private static void entityDeleteWarning(Entity entity)
    {
        Dialog entityDeleteWarning = new Dialog();
        entityDeleteWarning.setTitle("Confirm Action");
        entityDeleteWarning.setHeaderText("Warning! There are still assets associated with this entity. Please select an option: ");
        entityDeleteWarning.setContentText("Canceling this dialog will not affect your data. Selecting \"Delete\" will remove the entity " +
                " and set any attributes with this entity to an empty entity");

        ButtonType delete = new ButtonType("Delete", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        entityDeleteWarning.getDialogPane().getButtonTypes().addAll(delete, cancel);

        entityDeleteWarning.setResultConverter(dialogButton -> dialogButton.equals(delete));
        entityDeleteWarning.setOnCloseRequest(event -> entityDeleteWarning.close());

        Optional<Boolean> result = entityDeleteWarning.showAndWait();
        if(result.isPresent())
        {
            if(result.get())
            {
                for(Asset asset : Asset.getMap().keySet())
                {
                    if((asset.getOwner().equals(entity)) || (asset.getPropertyManager().equals(entity)) || (asset.getPurchaser().equals(entity)))
                    {

                        if(asset.getOwner().equals(entity))
                        {
                            asset.setOwner(Entity.NO_OWNER);
                        }
                        if(asset.getPropertyManager().equals(entity))
                        {
                            asset.setPropertyManager(Entity.NO_PROPERTY_MANAGER);
                        }
                        if(asset.getPurchaser().equals(entity))
                        {
                            asset.setPurchaser(Entity.NO_PURCHASER);
                        }
                        Encryption.removeEntity(entity);
                        asset.setEntities();
                    }
                }
            }
            Tree.setTrees();
            Encryption.saveJDF();
        }
    }

    private ObservableList<Asset> searchButtonAction(ActionEvent e)
    {
        Dialog<ObservableList<Asset>> search = new Dialog<>();
        search.setTitle("Search");

        ButtonType searchBtn = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        ObservableList<String> assetProperties = observableArrayList(
                "Asset Name",
                "Asset Tag",
                "Owner Name",
                "Owner ID",
                "Owner Email",
                "Location",
                "Type",
                "Manufacturer",
                "Model",
                "Serial",
                "Property Manager Name",
                "Property Manager ID",
                "Purchaser Name",
                "Purchaser ID"
        );

        VBox master = new VBox();

        HBox searchFor = new HBox();
        Label searchForLabel = new Label("Search by: ");
        ComboBox<String> assetProperty = new ComboBox<>();
        assetProperty.getItems().addAll(assetProperties);
        searchFor.getChildren().addAll(searchForLabel, assetProperty);

        HBox searchBox = new HBox();
        Label searchLabel = new Label("For: ");
        TextField query = new TextField();
        searchBox.getChildren().addAll(searchLabel, query);

        search.setResultConverter(dialogButton -> {
            if(dialogButton == searchBtn)
            {
                int searchBy = assetProperty.getSelectionModel().getSelectedIndex();
                String searchQuery = query.getText();
                ArrayList<Asset> resultArr = new ArrayList<>();
                switch(searchBy)
                {
                    case 0:
                        if(Tree.customIDBST.get(searchQuery) != null)
                            resultArr.addAll(Tree.customIDBST.get(searchQuery));
                        break;
                    case 1:
                        if(Tree.assetTagBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.assetTagBST.get(searchQuery));
                        break;
                    case 2:
                        if(Tree.ownerFullNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.ownerFullNameBST.get(searchQuery));
                        if(Tree.ownerFirstNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.ownerFirstNameBST.get(searchQuery));
                        if(Tree.ownerLastNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.ownerLastNameBST.get(searchQuery));
                        break;
                    case 3:
                        if(Tree.ownerIDBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.ownerIDBST.get(searchQuery));
                        break;
                    case 4:
                        if(Tree.emailBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.emailBST.get(searchQuery));
                        break;
                    case 5:
                        if(Tree.locationBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.locationBST.get(searchQuery));
                        break;
                    case 6:
                        if(Tree.typeBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.typeBST.get(searchQuery));
                        break;
                    case 7:
                        if(Tree.manufacturerBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.manufacturerBST.get(searchQuery));
                        break;
                    case 8:
                        if(Tree.modelBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.modelBST.get(searchQuery));
                        break;
                    case 9:
                        if(Tree.serialBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.serialBST.get(searchQuery));
                        break;
                    case 10:
                        if(Tree.propertyManagerFullNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.propertyManagerFullNameBST.get(searchQuery));
                        if(Tree.propertyManagerFirstNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.propertyManagerFirstNameBST.get(searchQuery));
                        if(Tree.propertyManagerLastNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.propertyManagerLastNameBST.get(searchQuery));
                        break;
                    case 11:
                        if(Tree.propertyManagerIDBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.propertyManagerIDBST.get(searchQuery));
                        break;
                    case 12:
                        if(Tree.purchaserFullNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.purchaserFullNameBST.get(searchQuery));
                        if(Tree.purchaserFirstNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.purchaserFirstNameBST.get(searchQuery));
                        if(Tree.purchaserLastNameBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.purchaserLastNameBST.get(searchQuery));
                        break;
                    case 13:
                        if(Tree.purchaserIDBST.get(searchQuery)!=null)
                            resultArr.addAll(Tree.purchaserIDBST.get(searchQuery));
                        break;
                }
                return observableArrayList(resultArr);
            } else {
                return null;
            }
        });

        master.getChildren().addAll(searchFor, searchBox);
        search.getDialogPane().setContent(master);
        search.getDialogPane().getButtonTypes().addAll(searchBtn, cancel);

        Optional<ObservableList<Asset>> result = search.showAndWait();
        if(result.isPresent())
        {
            return result.get();
        } else {
            Dialog noSearchResults = new Dialog();
            noSearchResults.setTitle("No Assets Found");
            noSearchResults.setContentText("No assets matching the searchDialog query were found.");
            ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.CANCEL_CLOSE);
            noSearchResults.getDialogPane().getButtonTypes().add(ok);
            noSearchResults.showAndWait();

            return null;
        }
    }

    private void deleteAssetButtonAction(ActionEvent ae, Asset a)
    {
        Dialog assetDeleteWarning = new Dialog();
        assetDeleteWarning.setTitle("Confirm Action");
        assetDeleteWarning.setHeaderText("Are you sure you want to delete this asset?");
        assetDeleteWarning.setContentText("Canceling this dialog will not affect your data. Selecting \"Delete\" will remove the asset.");

        ButtonType delete = new ButtonType("Delete", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        assetDeleteWarning.getDialogPane().getButtonTypes().addAll(delete, cancel);

        assetDeleteWarning.setResultConverter(dialogButton -> dialogButton.equals(delete));
        assetDeleteWarning.setOnCloseRequest(event -> assetDeleteWarning.close());

        Optional<Boolean> result = assetDeleteWarning.showAndWait();
        if(result.isPresent())
        {
            if(result.get())
            {
                Encryption.removeAsset(a);
                Tree.setTrees();
                Encryption.saveJDF();
            }
        }
    }

    private ArrayList<TableColumn<Asset, String>> setColumns()
    {
        // Table Columns
        TableColumn<Asset, String> assetTagColumn = new TableColumn<>("Asset Tag");
        TableColumn<Asset, String> assetNameColumn = new TableColumn<>("Asset Name");
        TableColumn<Asset, String> assetOwnerColumn = new TableColumn<>("Owner");
        TableColumn<Asset, String> assetLocationColumn = new TableColumn<>("Location");
        TableColumn<Asset, String> assetTypeColumn = new TableColumn<>("Type");
        TableColumn<Asset, String> assetManufacturerColumn = new TableColumn<>("Manufacturer");
        TableColumn<Asset, String> assetModelColumn = new TableColumn<>("Model");
        TableColumn<Asset, String> assetSerialColumn = new TableColumn<>("Serial");
        TableColumn<Asset, String> assetPropertyManagerColumn = new TableColumn<>("Property Manager");
        TableColumn<Asset, String> assetPriceColumn = new TableColumn<>("Price");
        TableColumn<Asset, String> assetPurchaserColumn = new TableColumn<>("Purchaser");
        TableColumn<Asset, String> assetPurchaseDateColumn = new TableColumn<>("Purchase Date");
        TableColumn<Asset, String> assetWarrantyExpiryDateColumn = new TableColumn<>("Warranty Expiry");
        TableColumn<Asset, String> assetLastCheckedDateColumn = new TableColumn<>("Last Checked");

        assetTagColumn.setCellValueFactory(cellData -> cellData.getValue().assetTagProperty());
        assetNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        assetOwnerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerFullNameProperty());
        assetLocationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        assetTypeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        assetManufacturerColumn.setCellValueFactory(cellData -> cellData.getValue().manufacturerProperty());
        assetModelColumn.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
        assetSerialColumn.setCellValueFactory(cellData -> cellData.getValue().serialProperty());
        assetPropertyManagerColumn.setCellValueFactory(cellData -> cellData.getValue().propertyManagerFullNameProperty());
        assetPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asString());
        assetPurchaserColumn.setCellValueFactory(cellData -> cellData.getValue().purchaserFullNameProperty());
        assetPurchaseDateColumn.setCellValueFactory(cellData -> cellData.getValue().purchaseDateProperty().asString());
        assetWarrantyExpiryDateColumn.setCellValueFactory(cellData -> cellData.getValue().warrantyExpiryDateProperty().asString());
        assetLastCheckedDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastCheckedDateProperty().asString());

        ArrayList<TableColumn<Asset, String>> columns = new ArrayList<>();
        
        columns.add(assetTagColumn);
        columns.add(assetNameColumn);
        columns.add(assetOwnerColumn);
        columns.add(assetLocationColumn);
        columns.add(assetTypeColumn);
        columns.add(assetManufacturerColumn);
        columns.add(assetModelColumn);
        columns.add(assetSerialColumn);
        columns.add(assetPropertyManagerColumn);
        columns.add(assetPriceColumn);
        columns.add(assetPurchaserColumn);
        columns.add(assetPurchaseDateColumn);
        columns.add(assetWarrantyExpiryDateColumn);
        columns.add(assetLastCheckedDateColumn);

        return columns;
    }

    InventoryPane()
    {
        // Containers
        HBox buttonContainer = new HBox(); // New, Edit, View, Entities, Search, Refresh button container

        // Main menu bar
        MenuBar inventoryMenuBar = new MenuBar(); // Menu bar

        // Menus
        Menu fileMenu = new Menu("File"); // File menu
        //Menu editMenu = new Menu("Edit"); // Edit menu, further implementation to come in later versions
        Menu helpMenu = new Menu("Help"); // Help menu

        // Submenus
        Menu newSubmenu = new Menu("New"); // New Asset or Entity submenu
        Menu exportSubmenu = new Menu("Export"); // Export either assets or entities
        Menu importSubmenu = new Menu("Import"); // Import assets or entities

        // Menu items
        MenuItem newAssetMenuItem = new MenuItem("Asset"); // New Asset menu item; action displays empty Asset Editor
        MenuItem newEntityMenuItem = new MenuItem("Entity"); // New entity menu item; action displays empty Entity Editor
        MenuItem exportAllAssetsMenuItem = new MenuItem("All Assets"); // Export all assets; displays save dialog
        MenuItem exportCurrentTableMenuItem = new MenuItem("Current Table"); // Export assets in the current table view; displays save dialog
        MenuItem exportAllEntitiesMenuItem = new MenuItem("All Entities"); // Export all entities; displays save dialog
        MenuItem importAssetsMenuItem = new MenuItem("Assets"); // Import assets from a properly formatted CSV file
        MenuItem importEntitiesMenuItem = new MenuItem("Entities"); // Import entities from a properly formatted CSV file
        MenuItem quitMenuItem = new MenuItem("Quit"); // Quits Bits; action also saves JDF file
        MenuItem aboutMenuItem = new MenuItem("About"); // Displays information about Bits

        // Menu separators
        SeparatorMenuItem fileMenuSeparator = new SeparatorMenuItem();

        // Buttons
        Button newAssetButton = new Button("New Asset"); // New Asset button; action displays empty AssetEditor dialog
        Button viewAssetButton = new Button("View Asset"); // New Entity button; action displays empty EntityEditor dialog
        Button editAssetButton = new Button("Edit Asset"); // Edit asset button; action displays populated, editable AssetEditor dialog
        Button deleteAssetButton = new Button("Delete Asset"); // Delete asset button; displays confirmation dialog
        Button entityListDialogButton = new Button("Entities"); // Entity list button; action displays list of entities
        Button searchButton = new Button("Search"); // Search button; action displays searchDialog Dialog
        Button exportButton = new Button("Export CSV"); // Export assets; asks if current view or all assets
        Button refreshButton = new Button("Refresh Table"); // Refresh button; refreshes table view to all Assets in the map

        // Button accelerators
        //viewAssetButton.setAccelerator

        // Table
        TableView<Asset> assetTable = new TableView<>();

        // Tableview context menus
        assetTable.setRowFactory(assetTable1 -> {
            final TableRow<Asset> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            Menu newAssetMenu = new Menu("New");

            MenuItem newAsset = new MenuItem("Empty Asset");
            MenuItem newAssetFromExisting = new MenuItem("From Existing Asset");
            MenuItem editAsset = new MenuItem("Edit");
            MenuItem viewAsset = new MenuItem("View");
            MenuItem removeAsset = new MenuItem("Delete");

            newAsset.setOnAction(ae -> {
                assetDialogButtonAction(ae, new Asset(), NEW);
                assetTable.setItems(Asset.getMap().getAllAssets());
            });
            newAssetFromExisting.setOnAction(ae -> {
                assetDialogButtonAction(ae, row.getItem(), NEW);
                assetTable.setItems(Asset.getMap().getAllAssets());
            });
            editAsset.setOnAction(ae -> {
                assetDialogButtonAction(ae, row.getItem(), EDIT);
                assetTable.setItems(Asset.getMap().getAllAssets());
            });
            viewAsset.setOnAction(ae -> assetDialogButtonAction(ae, row.getItem(), VIEW));
            removeAsset.setOnAction(ae -> {
                deleteAssetButtonAction(ae, row.getItem());
                assetTable.setItems(Asset.getMap().getAllAssets());
            });

            newAssetMenu.getItems().addAll(newAsset,newAssetFromExisting);

            rowMenu.getItems().addAll(newAssetMenu,editAsset,viewAsset,removeAsset);

            // only display context menu for non-null items:
            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowMenu)
                            .otherwise((ContextMenu) null));
            return row;
        });

        // Add actions to buttons
        newAssetMenuItem.setOnAction(a ->
        {
            assetDialogButtonAction(a,new Asset(),NEW);
            assetTable.setItems(Asset.getMap().getAllAssets());
        });
        newAssetButton.setOnAction(a ->
        {
            assetDialogButtonAction(a,new Asset(),NEW);
            assetTable.setItems(Asset.getMap().getAllAssets());
        });
        exportAllAssetsMenuItem.setOnAction(a -> exportAssetsAction(a, Asset.getMap().getAllAssets()));
        exportCurrentTableMenuItem.setOnAction(a -> exportAssetsAction(a, assetTable.getItems()));
        exportAllEntitiesMenuItem.setOnAction(this::exportAllEntitiesAction);
        viewAssetButton.setOnAction(a -> assetDialogButtonAction(a, assetTable.getSelectionModel().getSelectedItem(), VIEW));
        editAssetButton.setOnAction(a ->
        {
            assetDialogButtonAction(a, assetTable.getSelectionModel().getSelectedItem(), EDIT);
            assetTable.setItems(Asset.getMap().getAllAssets());
        });
        deleteAssetButton.setOnAction(a -> deleteAssetButtonAction(a, assetTable.getSelectionModel().getSelectedItem()));
        newEntityMenuItem.setOnAction(a -> entityDialogButtonAction(a, new Entity(), NEW));
        entityListDialogButton.setOnAction(a -> entitySelectorDialogAction());
        searchButton.setOnAction(a -> {
            ObservableList<Asset> tableSet = searchButtonAction(a);
            if(tableSet != null)
            {
                assetTable.setItems(tableSet);
            }
        });
        exportButton.setOnAction(ae -> {
            Dialog<Boolean> export = new Dialog<>();
            export.setTitle("Export");
            export.setContentText("Would you like to export all assets or the current view?");
            ButtonType all = new ButtonType("All Assets", ButtonBar.ButtonData.YES);
            ButtonType currentView = new ButtonType("Current View", ButtonBar.ButtonData.NO);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            export.getDialogPane().getButtonTypes().addAll(all,currentView,cancel);

            export.setResultConverter(dialogButton -> {
                if(dialogButton == all)
                {
                    return true;
                } else if(dialogButton == currentView) {
                    return false;
                } else {
                    export.close();
                    return null;
                }
            });
            Optional<Boolean> result = export.showAndWait();
            if(result.isPresent())
            {
                if(result.get())
                {
                    exportAssetsAction(ae, Asset.getMap().getAllAssets());
                } else {
                    exportAssetsAction(ae, assetTable.getItems());
                }
            }
        });
        refreshButton.setOnAction(a -> assetTable.setItems(Asset.getMap().getAllAssets()));
        aboutMenuItem.setOnAction(this::aboutMenuItemAction);

        // Set disablers
        viewAssetButton.disableProperty().bind(Bindings.isEmpty(assetTable.getSelectionModel().getSelectedItems()));
        editAssetButton.disableProperty().bind(Bindings.isEmpty(assetTable.getSelectionModel().getSelectedItems()));

        // Populates menus and menubar
        newSubmenu.getItems().addAll(newAssetMenuItem,newEntityMenuItem);
        exportSubmenu.getItems().addAll(exportAllAssetsMenuItem, exportCurrentTableMenuItem, exportAllEntitiesMenuItem);
        importSubmenu.getItems().addAll(importAssetsMenuItem, importEntitiesMenuItem);
        fileMenu.getItems().addAll(newSubmenu,exportSubmenu,importSubmenu,fileMenuSeparator,quitMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);
        inventoryMenuBar.getMenus().addAll(fileMenu,helpMenu);

        // Populate and configure buttonContainer
        buttonContainer.setSpacing(5);
        buttonContainer.getChildren().addAll(newAssetButton,viewAssetButton,editAssetButton, deleteAssetButton,
                entityListDialogButton,searchButton,exportButton,refreshButton);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        // Add columns to assetTable and set resize policy
        assetTable.setItems(Asset.getMap().getAllAssets());
        assetTable.getColumns().addAll(setColumns());
        assetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Populate and configure container
        setFillWidth(true);
        setSpacing(5);
        getChildren().addAll(inventoryMenuBar,buttonContainer,assetTable);
        setPadding(new Insets(5, 5, 5, 5));
    }
}
