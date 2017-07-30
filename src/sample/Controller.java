package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



public class Controller {

    private String CLIENT_URL = "http://mc9.serva4ok.ru/server664198/www/launcher/client.zip";
    private String MODS_URL = "http://mc9.serva4ok.ru/server664198/www/launcher/mods,zip";

    @FXML
    CheckBox cb_mods;

    @FXML
    Button button_launch;

    @FXML
    Button button_update;

    @FXML
    TextArea textArea;

    @FXML
    AnchorPane anchorPane;

    public void button_launch_clicked(ActionEvent actionEvent) {
        launchGame();
    }

    public void button_update_clicked(ActionEvent actionEvent) {
        modsThread.start();
        anchorPane.setDisable(true);
    }

    public void button_client_clicked(ActionEvent actionEvent) {
        clientDownloadThread.start();
        anchorPane.setDisable(true);
    }

    private Thread modsThread = new Thread() {
        @Override
        public void run() {
            button_update.setVisible(false);
            cb_mods.setDisable(true);
            if (updateMods()) {
                extractFolder("mods.zip", System.getProperty("user.dir"));
            }
            button_launch.setDisable(false);
            anchorPane.setDisable(false);
        }

        private boolean updateMods() {
            URL website = null;
            try {
                log("Downloading mods archive...");
                website = new URL("" + MODS_URL);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("mods.zip");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                log("Mods archive downloaded successfully.");


            } catch (MalformedURLException e) {
                log(e);
                return false;
            } catch (FileNotFoundException e) {
                log("404 File not found on the server.");
                return false;
            } catch (IOException e) {
                log(e);
                return false;
            }

            return true;
        }

        private boolean extractFolder(String zipFile, String extractFolder) {
            log("Trying to extract mods...");
            try {
                int BUFFER = 2048;
                File file = new File(zipFile);

                ZipFile zip = new ZipFile(file);
                String newPath = extractFolder;

                new File(newPath).mkdir();
                Enumeration zipFileEntries = zip.entries();

                // Process each entry
                while (zipFileEntries.hasMoreElements()) {
                    // grab a zip file entry
                    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                    String currentEntry = entry.getName();

                    File destFile = new File(newPath, currentEntry);
                    //destFile = new File(newPath, destFile.getName());
                    File destinationParent = destFile.getParentFile();

                    // create the parent directory structure if needed
                    destinationParent.mkdirs();

                    if (!entry.isDirectory()) {
                        BufferedInputStream is = new BufferedInputStream(zip
                                .getInputStream(entry));
                        int currentByte;
                        // establish buffer for writing file
                        byte data[] = new byte[BUFFER];

                        // write the current file to disk
                        FileOutputStream fos = new FileOutputStream(destFile);
                        BufferedOutputStream dest = new BufferedOutputStream(fos,
                                BUFFER);

                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                        dest.close();
                        is.close();
                    }

                }
            } catch (Exception e) {
                log(e);
                return false;
            }
            log("Extracting finished successfully.");
            return true;
        }
    };

    private Thread clientDownloadThread = new Thread() {
        @Override
        public void run() {
            button_update.setVisible(false);
            if (downloadClient()) {
                extractFolder("client.zip", System.getProperty("user.dir"));
            }
            anchorPane.setDisable(false);
        }

        private boolean downloadClient() {
            URL website = null;
            try {
                log("Downloading client archive...");
                website = new URL(""+CLIENT_URL);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("client.zip");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                log("Client archive downloaded successfully.");


            } catch (MalformedURLException e) {
                log(e);
                return false;
            } catch (FileNotFoundException e) {
                log("404 File not found on the server.");
                return false;
            } catch (IOException e) {
                log(e);
                return false;
            }

            return true;
        }

        private boolean extractFolder(String zipFile, String extractFolder) {
            log("Trying to extract client...");
            try {
                int BUFFER = 2048;
                File file = new File(zipFile);

                ZipFile zip = new ZipFile(file);
                String newPath = extractFolder;

                new File(newPath).mkdir();
                Enumeration zipFileEntries = zip.entries();

                // Process each entry
                while (zipFileEntries.hasMoreElements()) {
                    // grab a zip file entry
                    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                    String currentEntry = entry.getName();

                    File destFile = new File(newPath, currentEntry);
                    //destFile = new File(newPath, destFile.getName());
                    File destinationParent = destFile.getParentFile();

                    // create the parent directory structure if needed
                    destinationParent.mkdirs();

                    if (!entry.isDirectory()) {
                        BufferedInputStream is = new BufferedInputStream(zip
                                .getInputStream(entry));
                        int currentByte;
                        // establish buffer for writing file
                        byte data[] = new byte[BUFFER];

                        // write the current file to disk
                        FileOutputStream fos = new FileOutputStream(destFile);
                        BufferedOutputStream dest = new BufferedOutputStream(fos,
                                BUFFER);

                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                        dest.close();
                        is.close();
                    }

                }
            } catch (Exception e) {
                log(e);
                return false;
            }
            log("Extracting finished successfully.");
            return true;
        }
    };


    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();

    private void log(Exception e) {

        String text = textArea.getText();
        text += dateFormat.format(date) + ":  " + e.getMessage() + "\n";
        textArea.setText(text);
    }

    private void log(String str) {
        date = new Date();

        String text = textArea.getText();
        text += dateFormat.format(date) + ":  " + str + "\n";
        textArea.setText(text);
    }

    private void launchGame() {
        boolean error = false;
        try {
            log("Running TLauncher.exe.");
            Runtime.getRuntime().exec("TLauncher.exe");
            log("Enjoy!");
        } catch (IOException e) {
            error = true;
            log(e);
        }
        if (!error) {
            System.exit(0);
        }
    }

    private boolean checkModsUpdate() {
        return cb_mods.isSelected();
    }

    public void switchUpdateMods(ActionEvent actionEvent) {
        if (checkModsUpdate()) {
            button_update.setVisible(true);
            button_launch.setDisable(true);
        } else {
            button_update.setVisible(false);
            button_launch.setDisable(false);
        }
    }

}
