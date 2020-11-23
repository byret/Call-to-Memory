package com.mycompany.calltomemory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import org.apache.commons.lang3.StringUtils;



/**
 * JavaFX App
 */
public class App extends Application {

    BorderPane borderPane = new BorderPane();
    VBox textVBox = new VBox();
    File file;
    int numOfWords;
    int numOfRows;
    int percentOfWords = 5;
    int plus = 5;
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.setMaximized(true);
        stage.setTitle("Call to Memory");       
        
        borderPane.setTop(menuLoad(stage));
        Label center = new Label("CALL TO MEMORY");
        borderPane.setCenter(center);
        Scene firstScene = new Scene(borderPane);
        stage.setScene(firstScene);
        stage.show();
    }
    
    public VBox menuLoad(Stage stage){
        MenuBar menuBar = new MenuBar();
        Menu menu1 = new Menu();
        Label l1 = new Label("File");
        l1.setStyle("-fx-text-fill: white;");
        menu1.setGraphic(l1);
        MenuItem menuItem1 = new MenuItem("New file...");
        MenuItem menuItem2 = new MenuItem("Open file...");
        MenuItem menuItem3 = new MenuItem("Save");
        menu1.getItems().addAll(menuItem1, menuItem2, menuItem3);
        
        // TODO 
        Menu menu2 = new Menu();
        Label l2 = new Label("Edit");
        l2.setStyle("-fx-text-fill: white;");
        menu2.setGraphic(l2);
        
        Menu menu3 = new Menu();
        Label l3 = new Label("Window");
        l3.setStyle("-fx-text-fill: white;");
        menu3.setGraphic(l3);
        
        Menu menu4 = new Menu();
        Label l4 = new Label("Export");
        l4.setStyle("-fx-text-fill: white;");
        menu4.setGraphic(l4);
        
        Menu menu5 = new Menu();
        Label l5 = new Label("Help");
        l5.setStyle("-fx-text-fill: white;");
        menu5.setGraphic(l5);
        
        menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5);
        VBox vBox = new VBox(menuBar);
        menuBar.setStyle("-fx-background-color : #819dff;");
        
        // New file clicked
        menuItem1.setOnAction((ActionEvent e) -> {
            createFile(stage);
        });
        
        // Open file clicked
        menuItem2.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            String fileName = "resources/" + selectedFile.getName();
            file = new File(fileName);
            try {
                Files.copy(selectedFile.toPath(), file.toPath());
                workWithText(stage);
            } catch (IOException ex) {
                // TODO WYJATEK - NIE UDALO SIE SKOPIOWAC
                System.err.println("Problem reading file.");
            }
        });
        
        // Save clicked
        menuItem3.setOnAction((ActionEvent e) -> {
            if (file != null){
             RandomAccessFile raf;
                try {
                    raf = new RandomAccessFile(file.getPath(), "rw");
                    long length = raf.length() - 1;
                    byte b;
                    do {                     
                        length -= 1;
                        raf.seek(length);
                        b = raf.readByte();
                    } while(b != 10);
                    raf.setLength(length+1);
                    Files.writeString(file.toPath(), "endOfFile " + numOfWords + ' ' + percentOfWords, StandardOpenOption.APPEND);
                    raf.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }      
            }
        });
        
        return vBox;
    }
    
    public void workWithText(Stage stage) throws FileNotFoundException, IOException{
        Locale.setDefault(new Locale("ru"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        Scanner sc = new Scanner(bufferedReader);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Label top = new Label("Try to remember the text");
        top.setStyle("-fx-font-size: 30pt;");
        textVBox.setAlignment(Pos.CENTER);
        
        boolean isFirstTime = true;
        
        for (int i = 0; sc.hasNextLine(); i++) {
            //int row = 0;
            int word = 0;
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(10);
            String line = sc.nextLine();
            if (line.contains("endOfFile")){
                isFirstTime = false;
                int index = line.substring(10).indexOf(" ");
                numOfWords = Integer.parseInt(line.substring(10, 10 + index));
                percentOfWords = Integer.parseInt(line.substring(11 + index));          
            }
            else {
                int wordLength = 0;
                for (int j = 0; j < line.length(); j++){                  
                    if (!Character.isWhitespace(line.charAt(j)) && j + 1 < line.length())
                        wordLength++;
                    else {
                        Label label = new Label(line.substring(j - wordLength, j));
                        if (j + 1 == line.length())
                            label = new Label(line.substring(j - wordLength, j + 1));
                        System.setProperty("file.encoding","utf-8");
                        System.out.println("aaaaaa " + System.getProperty("file.encoding"));
                        label.setStyle("-fx-font-size: 15pt; -fx-font-family: \"Arial\";");
                      
                        hBox.getChildren().add(label);
                        wordLength = 0;
                        word++;
                    }
                }          
            }               
            textVBox.getChildren().add(hBox);
            numOfRows++;
            if (isFirstTime){
                    numOfWords += word;
                }    
        }
        if (isFirstTime){
            Files.writeString(file.toPath(), "\nendOfFile " + numOfWords + ' ' + percentOfWords, StandardOpenOption.APPEND);
        }
        Label progress = new Label();
        progress.setStyle("-fx-font-size: 20pt; -fx-background-color : #819dff; -fx-text-fill : white;");
        Button buttonOk = new Button("Ok!");
        vbox.setSpacing(30);
        vbox.getChildren().addAll(top, textVBox, progress, buttonOk);
        
        buttonOk.setOnAction((ActionEvent e) -> {
            top.setText("Complete the text");
            removeWords(stage, vbox);
        });
        
        borderPane.setTop(menuLoad(stage));
        borderPane.setCenter(vbox);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }
    

    
    public void removeWords(Stage stage, VBox vbox){ 
        final boolean isLastTime;
        Button buttonCheck = new Button("Check");
        vbox.getChildren().remove(3);
        vbox.getChildren().add(buttonCheck);
        Random rand = new Random();
        int numOfWordsToRemove = numOfWords * percentOfWords / 100;
        if (percentOfWords >= 100){
            numOfWordsToRemove = numOfWords;
            isLastTime = true;
        }
        else isLastTime = false;
        
        ((Label)vbox.getChildren().get(2)).setText(String.valueOf(numOfWordsToRemove) + '/' + String.valueOf(numOfWords));
        
        MyNode [] nodes = new MyNode[numOfWordsToRemove];
        List<MyPoint> list = new ArrayList<>();
        
        for (int i = 0; i < numOfWordsToRemove; i++){
            nodes[i] = new MyNode();
            int indexRow; // X
            int index;    // Y
            MyPoint point = new MyPoint(0, 0);
            while (true){ 
                indexRow = rand.nextInt(numOfRows);
                int numOfWordsInPerLine =((HBox)(textVBox.getChildren().get(indexRow))).getChildren().size();
                if (numOfWordsInPerLine == 0)
                    continue;
                index = rand.nextInt(numOfWordsInPerLine);
                point.setX(indexRow); 
                point.setY(index);
                if (!list.contains(point))
                    break;
            }
            list.add(point);
            if (textVBox.getChildren().get(indexRow) instanceof HBox){
                if (((HBox)(textVBox.getChildren().get(indexRow))).getChildren().get(index) instanceof Label){
                //nodes[i].setIndex(index);
                nodes[i].setIndexX(indexRow);
                nodes[i].setIndexY(index);
                nodes[i].setString(((Label)((HBox)(textVBox.getChildren().get(indexRow))).getChildren().get(index)).getText());
                nodes[i].writeAll();
                ((HBox)(textVBox.getChildren().get(indexRow))).getChildren().remove(index);
                TextField textField = new TextField ();
                textField.setPrefWidth(60);
                textField.textProperty().addListener((ov, prevText, currText) -> {
                    Platform.runLater(() -> {
                    Text text = new Text(currText);
                    text.setFont(textField.getFont());
                    double width = text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d;
                    textField.setPrefWidth(width); 
                    });
                });
                ((HBox)(textVBox.getChildren().get(indexRow))).getChildren().add(index, textField);
                }
            }
        }
       
        buttonCheck.setOnAction((ActionEvent e) -> {
            checkWords(stage, vbox, nodes, isLastTime);
        });
        
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();    
    }
    
    public void checkWords(Stage stage, VBox vbox, MyNode [] nodes, boolean isLastTime){ 
        int numOfRight = 0, numOfWrong = 0;
        for (MyNode node : nodes){
            if (((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY()) instanceof TextField){
                if (compareTwoStrings(node.getString(), ((TextField)((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY())).getText())){
                    numOfRight++;
                    ((TextField)((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY())).setStyle("-fx-control-inner-background: #cafeca;");                  
                }
                else{
                    numOfWrong++;
                    ((TextField)((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY())).setStyle("-fx-control-inner-background: #ffcbcb;");                    
                }
            }
        }
        plus = 5;
        if (numOfWrong == 0){
            ((Label)vbox.getChildren().get(0)).setText("Excellent!");
        }
        else{
            ((Label)vbox.getChildren().get(0)).setText("You made " + numOfWrong + " mistakes");
            plus -= numOfWrong;
            if (plus < 0) plus = 0;
        }
        percentOfWords += plus;
        
        Button buttonOk = new Button("Ok");
        vbox.getChildren().remove(3);
        vbox.getChildren().add(buttonOk);
        buttonOk.setOnAction((ActionEvent e) -> {
            ((Label)vbox.getChildren().get(0)).setText("Complete the text");
            for (MyNode node : nodes){
                if (((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY()) instanceof TextField){
                    ((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().remove(node.getIndexY());
                    Label label = new Label(node.getString());
                    label.setStyle("-fx-font-size: 15pt;");
                    ((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().add(node.getIndexY(), label);
                }
            }
            removeWords(stage, vbox);
        });
    }
    
    // TODO - POROWNYWANIE SLOW
    public boolean compareTwoStrings(String firstString, String secondString){
        firstString = firstString.toLowerCase();
        secondString = secondString.toLowerCase();
        firstString = removeNonAlphanumerics(firstString);
        secondString = removeNonAlphanumerics(secondString);
        int i = StringUtils.getLevenshteinDistance(firstString, secondString);
        return i <= firstString.length()/4;
    }
    
    public String removeNonAlphanumerics (String string){
        String[] stringArray = string.split("\\W+");
        String result = new String();
        for (int i = 0; i < stringArray.length; i++)
            result += stringArray[i];
        return result;
    }
    
    public void createFile(Stage stage){ 
        VBox vBox = new VBox();
        double width = stage.getWidth();
        double height = stage.getHeight();
        Label createText = new Label("Create your text");
        createText.setStyle("-fx-font-size: 30pt;");
        TextArea textArea = new TextArea ();   
        textArea.setMinSize(width/2.5, height/2);
        textArea.setMaxSize(width/2.5, height/2);
        textArea.setPromptText("Enter the text...");
        textArea.setStyle("-fx-control-inner-background: #d1dbff; -fx-prompt-text-fill: #ffffff; -fx-font-size: 18pt;");  
        
        Button buttonCreateFile = new Button("Create file");    
        buttonCreateFile.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File newFile = fileChooser.showSaveDialog(stage);
            
            if (newFile != null) {
                try {
                    PrintWriter writer;
                    writer = new PrintWriter(newFile);
                    writer.println(textArea.getText());
                    writer.close();
                    file = newFile;
                    workWithText(stage);
                } catch (IOException ex) {
        }
            }
        });
        
        vBox.getChildren().addAll(createText, textArea, buttonCreateFile);
        vBox.setSpacing(50);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setTop(menuLoad(stage));
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }
     
    public static void main(String[] args) {
        launch();
    }
}