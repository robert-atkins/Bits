import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.*;

/**
 * Created by RobertAtkins on 5/30/16.
 */
class Encryption
{
    // Easily access AES when selecting encryption type
    private static final String AES = "AES";

    // Hardcoded password for when a user declines to use encryption
    private static final String HARD_CODED_PASSWORD = "L6={nV";

    // *.jkf and *.jdf are JAIMS Key Files and JAIMS Data Files, respectively
    private static final String KEY_PATH = ".key.jkf"; // Generated key location (hidden)
    private static final String DATA_PATH = ".data.jdf"; // Data storage file location (hidden)

    // Key and Data file objects
    private static final File KEY_FILE = new File(KEY_PATH);   // Key file object
    private static final File DATA_FILE = new File(DATA_PATH); // Data file object

    /** Returns a SecretKeySpec made from the keyfile
     *
     * @param keyFile
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static SecretKeySpec getSecretKeySpec(File keyFile) throws NoSuchAlgorithmException, IOException
    {
        byte[] key = readKeyFile(keyFile);
        SecretKeySpec sks = new SecretKeySpec(key, AES);
        return sks;
    }

    /** Get a crypto-usable Byte array of the the key file
     *
     * @param keyFile
     * @return      Byte array representation of the key file
     * @throws FileNotFoundException
     */
    private static byte[] readKeyFile(File keyFile) throws FileNotFoundException
    {
        // Ensure the entire file is read in with \Z delimiter
        Scanner scanner = new Scanner(keyFile).useDelimiter("\\Z");
        String keyValue = scanner.next();
        scanner.close();
        return hexStringToByteArray(keyValue);
    }

    /** Convert a Byte array into a hex String for storing encrypted data
     *
     * @param       b Byte array to be converted
     * @return      The byte array as a String variable
     */
    private static String byteArrayToHexString(byte[] b)
    {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++)
        {
            int v = b[i] & 0xff;
            if (v < 16)
            {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    /** Convert a hex String into a Byte array to get decrypted text from file
     *
     * @param s     String to be converted
     * @return      The hex String as a Byte array
     */
    private static byte[] hexStringToByteArray(String s)
    {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++)
        {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }

    /**
     *
     */
    private static void generateKeyFile()
    {
        try
        {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES);
            keyGen.init(128);
            SecretKey sk = keyGen.generateKey();
            FileWriter fw = new FileWriter(KEY_PATH);
            fw.write(byteArrayToHexString(sk.getEncoded()));
            fw.flush();
            fw.close();
        } catch(IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    static void removeAsset(Asset asset)
    {
        try{
            Bits.BITS_PROPERTIES.remove(encrypt("A::" + Integer.toString(asset.getKey())));
            Asset.getMap().remove(asset);
        } catch(IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
        }
    }

    static void removeEntity(Entity entity)
    {
        try{
            Bits.BITS_PROPERTIES.remove(encrypt("E::" + Integer.toString(entity.getKey())));
            Entity.getMap().remove(entity.getKey());
        } catch(IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
        }
    }

    static void saveJDF()
    {
        try
        {
            Bits.BITS_PROPERTIES.setProperty(encrypt("EntityCount"), encrypt(Integer.toString(Bits.TOTAL_ENTITIES_WRITTEN.getValue())));
            Bits.BITS_PROPERTIES.setProperty(encrypt("AssetCount"), encrypt(Integer.toString(Bits.TOTAL_ASSETS_WRITTEN.getValue())));
            for(Entity e : Entity.getMap().values()) // Write entities to the JDF properties object
            {
                if(e != Entity.NO_OWNER && e != Entity.NO_PROPERTY_MANAGER && e != Entity.NO_PURCHASER)
                {
                    Bits.BITS_PROPERTIES.setProperty(encrypt("E::" + Integer.toString(e.getKey())), encrypt(e.toJdfString()));
                }
            }
            for(Asset a : Asset.getMap().keySet()) // Write assets to the JDF properties object
                Bits.BITS_PROPERTIES.setProperty(encrypt("A::" + Integer.toString(a.getKey())), encrypt(a.toJdfString()));
            FileWriter fw = new FileWriter(DATA_PATH, false);
            Bits.BITS_PROPERTIES.store(fw, "");
            fw.close();
        } catch(IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
        }
    }

    /** Encrypt data using the keyFile
     *
     * @param value Data to be encrypted
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static String encrypt(String value) throws GeneralSecurityException, IOException
    {
        SecretKeySpec sks = getSecretKeySpec(KEY_FILE);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return byteArrayToHexString(encrypted);
    }

    public static String encryptPassword(String value)
    {
        try
        {
            value = encrypt(value);
        } catch(GeneralSecurityException | IOException e)
        {
            e.printStackTrace();
        }
        return value;
    }

    private static String firstRun()
    {
        boolean promptProtect = true;
        String returnString = "";

        Dialog<String> firstRun = new Dialog<>();
        firstRun.setTitle("File Protection");
        firstRun.setHeaderText("Would you like to use a password?");

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        firstRun.getDialogPane().getButtonTypes().addAll(yes, no);

        firstRun.setResultConverter(dialogButton -> {
            if(dialogButton == yes)
            {
                return setPassword();
            } else {
                if(noPasswordConfirm())
                {
                    return HARD_CODED_PASSWORD;
                } else {
                    return "R E P R O M P T";
                }
            }
        });

        while(promptProtect)
        {
            Optional<String> result = firstRun.showAndWait();
            if(result.isPresent())
            {
                returnString = result.get();
                if(returnString != "R E P R O M P T")
                {
                    promptProtect = false;
                }
            }
        }
        return returnString;
    }

    private static String setPassword()
    {
        boolean promptForPassword = true;

        String returnPassword = HARD_CODED_PASSWORD;
        Dialog<String> setPassword = new Dialog<>();
        setPassword.setTitle("Set Password");
        setPassword.setHeaderText("Please set your password below");

        ButtonType commitPassword = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        setPassword.getDialogPane().getButtonTypes().addAll(commitPassword, cancel);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField passwordReentry = new PasswordField();
        password.setPromptText("Re-Enter Password");


        Node commitButton = setPassword.getDialogPane().lookupButton(commitPassword);
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            commitButton.setDisable(!newValue.trim().equals(passwordReentry.getText()) || newValue.trim().equals(""));
        });
        passwordReentry.textProperty().addListener((observable, oldValue, newValue) -> {
            commitButton.setDisable(!newValue.trim().equals(password.getText()) || newValue.trim().equals(""));
        });
        commitButton.setDisable(true);

        VBox promptContainer = new VBox();
        promptContainer.setPadding(new Insets(5, 5, 5, 5));
        promptContainer.setSpacing(5);
        promptContainer.getChildren().addAll(passwordReentry, password);

        setPassword.getDialogPane().setContent(promptContainer);

        setPassword.setResultConverter(dialogButton -> {
            String p = "";
            if(dialogButton == commitPassword)
            {
                p = password.getText();
            } else {
                if(noPasswordConfirm())
                {
                    p = HARD_CODED_PASSWORD;
                } else {
                    p = "NOPASSWORDTRYAGAIN";
                }
            }
            return p;
        });

        while(promptForPassword)
        {
            Optional<String> result = setPassword.showAndWait();
            if(result.isPresent())
            {
                returnPassword = result.get();
                if(!returnPassword.equals("NOPASSWORDTRYAGAIN"))
                {
                    promptForPassword = false;
                }
            }
        }

        return returnPassword;
    }

    private static boolean noPasswordConfirm()
    {
        Dialog <Boolean> noPasswordConfirm = new Dialog<>();
        noPasswordConfirm.setTitle("Set Password");
        noPasswordConfirm.setHeaderText("You have not set a password");
        noPasswordConfirm.setContentText("Are you sure you do not want to set a password?");

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        noPasswordConfirm.getDialogPane().getButtonTypes().addAll(yes, no);

        noPasswordConfirm.setResultConverter(dialogButton -> dialogButton == yes);
        Optional<Boolean> result = noPasswordConfirm.showAndWait();

        return result.get();
    }

    /** Creates a new JDF file and stores it at the root. The resulting file is
     * preceded by a period, hiding it from the user. This is to ensure the data
     * are not tampered with unintentionally.
     *
     */
    private static void createNewJdf()
    {
        generateKeyFile();
        String pass = firstRun();
        try
        {
            if(pass != HARD_CODED_PASSWORD)
            {
                Bits.BITS_PROPERTIES.setProperty("PassProtect", "YES");
            } else {
                Bits.BITS_PROPERTIES.setProperty("PassProtect", "NO");
            }

            Bits.BITS_PROPERTIES.setProperty("Pass", encrypt(pass));
            Bits.BITS_PROPERTIES.setProperty(encrypt("AssetCount"), encrypt("0"));
            Bits.BITS_PROPERTIES.setProperty(encrypt("EntityCount"), encrypt("0"));
            FileWriter fw = new FileWriter(DATA_PATH, false);
            Bits.BITS_PROPERTIES.store(fw, "");
            fw.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Decrypt data with the provided key file
     *
     * @param key     Encrypted data to be decrypted
     * @param keyFile   Key file to use to decrypt data
     * @throws          GeneralSecurityException
     * @throws          IOException
     */
    private static String decrypt(String key, File keyFile) throws GeneralSecurityException, IOException
    {
        SecretKeySpec sks = getSecretKeySpec(keyFile);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(key));
        return new String(decrypted);
    }

    /** Public decrypt method that only takes a key to decrypt
     *
     * @param key   The String containing a key or value to decrypt
     * @return  String containing the decrypted value for the specified key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static String decrypt(String key) throws GeneralSecurityException, IOException
    {
        return decrypt(key, KEY_FILE);
    }

    private static Boolean passwordPrompt()
    {
        String encryptedPass = Bits.BITS_PROPERTIES.getProperty("Pass");
        boolean isPasswordCorrect = false;
        int attemptCount = 0;

        Dialog<Boolean> passwordPrompt = new Dialog<>();
        passwordPrompt.setTitle("Enter Password");
        ButtonType commitPassword = new ButtonType("Unlock", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        passwordPrompt.getDialogPane().getButtonTypes().addAll(commitPassword, cancel);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Node commitButton = passwordPrompt.getDialogPane().lookupButton(commitPassword);
        commitButton.setDisable(true);

        commitButton.disableProperty().bind(password.textProperty().isEmpty());

        VBox promptContainer = new VBox();
        promptContainer.setPadding(new Insets(5, 5, 5, 5));
        promptContainer.setSpacing(5);
        promptContainer.getChildren().add(password);

        passwordPrompt.getDialogPane().setContent(promptContainer);

        password.requestFocus();

        passwordPrompt.setResultConverter(dialogButton -> {
            String passwordAttempt = "";
            boolean isCorrect = false;
            if(dialogButton == cancel)
            {
                System.exit(0);
            } else {
                passwordAttempt = password.getText();

                isCorrect = Encryption.encryptPassword(passwordAttempt).equals(encryptedPass);
            }
            return isCorrect;
        });

        while(!isPasswordCorrect)
        {
            switch(attemptCount)
            {
                case 0:
                    passwordPrompt.setHeaderText("Please Enter Your Password");
                    break;
                default:
                    passwordPrompt.setHeaderText("Incorrect Password.");
                    password.setText("");
                    break;
            }
            Optional<Boolean> result = passwordPrompt.showAndWait();
            isPasswordCorrect = result.get();

            if(!isPasswordCorrect)
            {
                attemptCount++;
                if(attemptCount > 2)
                {
                    tooManyAttemptsError();
                    System.exit(3);
                }
            }
        }
        return isPasswordCorrect;
    }

    private static void tooManyAttemptsError()
    {
        Alert tooManyAttempts = new Alert(Alert.AlertType.ERROR);
        tooManyAttempts.setTitle("Incorrect Password");
        tooManyAttempts.setHeaderText("Too Many Attempts");
        tooManyAttempts.setContentText("You have entered the incorrect password too many times. Bits will now terminate.");
        tooManyAttempts.setOnCloseRequest(event -> System.exit(0));

        tooManyAttempts.showAndWait();
    }

    static void loadFile()
    {
        loadFilePrivate();
    }

    private static void loadFilePrivate()
    {
        try{
            if(!KEY_FILE.exists())
            {
                createNewJdf();
            } else {
                loadDataProperties();
                if(Bits.BITS_PROPERTIES.getProperty("PassProtect").equals("YES"))
                {
                    boolean okToDecrypt = false;
                    while(!okToDecrypt)
                    {
                        okToDecrypt = passwordPrompt();
                        if(okToDecrypt)
                        {
                            try{
                                processBitsFile();
                            } catch (GeneralSecurityException | IOException e){e.printStackTrace();}
                        }
                    }
                } else {
                    try
                    {
                        processBitsFile();
                    } catch (GeneralSecurityException | IOException e){e.printStackTrace();}
                }
            }
        } catch(Exception e){e.printStackTrace();}
    }

    /**
     *
     *
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void processBitsFile() throws GeneralSecurityException, IOException
    {
        for(String key : Bits.BITS_PROPERTIES.stringPropertyNames())
        {
            processEntry(key);
        }
        processEntity();
        processAsset();
        Tree.setTrees();
    }

    private static Stack<String[]> entityStack = new Stack<>();
    private static Stack<String[]> assetStack = new Stack<>();
    private static void processEntry(String key) throws GeneralSecurityException, IOException
    {
        if(!key.equals("Pass") && !key.equals("PassProtect"))
        {
            if(!decrypt(key).equals("AssetCount") && !decrypt(key).equals("EntityCount"))
            {
                String entryPropString = decrypt(Bits.BITS_PROPERTIES.getProperty(key));
                String[] entryPropArray = entryPropString.split("&!");
                if(entryPropArray[0].contains("E::"))
                    entityStack.add(entryPropArray);
                if(entryPropArray[0].contains("A::"))
                    assetStack.add(entryPropArray);

            } else {
                if(decrypt(key).equals("AssetCount"))
                    Bits.TOTAL_ASSETS_WRITTEN.setValue(Integer.parseInt(decrypt(Bits.BITS_PROPERTIES.getProperty(key))));
                else if(decrypt(key).equals("EntityCount"))
                    Bits.TOTAL_ENTITIES_WRITTEN.setValue(Integer.parseInt(decrypt(Bits.BITS_PROPERTIES.getProperty(key))));
            }
        }
    }

    private static void processEntity()
    {
        while(!entityStack.isEmpty())
        {
            String[] entityProperties = entityStack.pop();
            Entity e = new Entity();
            e.setKey(Integer.parseInt(entityProperties[Entity.ENTITY_KEY].substring(3)));
            e.setCustomId(entityProperties[Entity.ENTITY_CUSTOM_ID]);
            e.setFirstName(entityProperties[Entity.ENTITY_FIRST_NAME]);
            e.setLastName(entityProperties[Entity.ENTITY_LAST_NAME]);
            e.setEmail(entityProperties[Entity.ENTITY_EMAIL]);
            e.setLocation(entityProperties[Entity.ENTITY_LOCATION]);
            e.setPhone(Long.parseLong(entityProperties[Entity.ENTITY_PHONE]));
            e.setDivision(entityProperties[Entity.ENTITY_DIVISION]);

            Entity.getMap().put(e.getKey(), e);
        }
    }

    private static void processAsset()
    {
        while(!assetStack.isEmpty())
        {
            String[] assetProperties = assetStack.pop();
            Asset a = new Asset();
            a.setKey(Integer.parseInt(assetProperties[Asset.ASSET_KEY].substring(3)));
            a.setAssetTag(assetProperties[Asset.ASSET_TAG]);
            a.setName(assetProperties[Asset.ASSET_NAME]);
            a.setOwner(Entity.getMap().get(Integer.parseInt(assetProperties[Asset.ASSET_OWNER_KEY])));
            a.setLocation(assetProperties[Asset.ASSET_LOCATION]);
            a.setType(assetProperties[Asset.ASSET_TYPE]);
            a.setManufacturer(assetProperties[Asset.ASSET_MANUFACTURER]);
            a.setModel(assetProperties[Asset.ASSET_MODEL]);
            a.setSerial(assetProperties[Asset.ASSET_SERIAL]);
            a.setPropertyManager(Entity.getMap().get(Integer.parseInt(assetProperties[Asset.ASSET_PROPERTY_MANAGER_KEY])));
            a.setPrice(Double.parseDouble(assetProperties[Asset.ASSET_PRICE]));
            a.setPurchaser(Entity.getMap().get(Integer.parseInt(assetProperties[Asset.ASSET_PURCHASER_KEY])));
            a.setPurchaseDate(LocalDate.parse(assetProperties[Asset.ASSET_PURCHASE_DATE], Asset.DATE_FORMATTER));
            a.setWarrantyExpiryDate(LocalDate.parse(assetProperties[Asset.ASSET_WARRANTY_EXPIRY_DATE], Asset.DATE_FORMATTER));
            a.setLastCheckedDate(LocalDate.parse(assetProperties[Asset.ASSET_LAST_CHECKED_DATE], Asset.DATE_FORMATTER));

            a.setEntities();

            Asset.getMap().put(a, a.getOwner());
        }
    }

    /** Load the properties stored in the data file to a Properties object
     * @throws IOException
     */
    private static void loadDataProperties() throws IOException
    {
        Bits.BITS_PROPERTIES.load(new FileReader(DATA_FILE));
    }
}
