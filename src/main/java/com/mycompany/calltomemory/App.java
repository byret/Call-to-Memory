package com.mycompany.calltomemory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
/**
 * JavaFX App
 */
public class App extends Application {
   
    BorderPane borderPane = new BorderPane();
    GridPane gridPane = new GridPane();
    int numOfWords;
    int percentOfWords;
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
        Label t = new Label("File");
        t.setStyle("-fx-text-fill: white;");
        menu1.setGraphic(t);
        MenuItem menuItem1 = new MenuItem("New file...");
        MenuItem menuItem2 = new MenuItem("Open file...");
        MenuItem menuItem3 = new MenuItem("Save");
        MenuItem menuItem4 = new MenuItem("Save as...");
        menu1.getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4);
        
        // TODO 
        Menu menu2 = new Menu("Edit");
        Menu menu3 = new Menu("Window");
        Menu menu4 = new Menu("Export");
        Menu menu5 = new Menu("Help");
        menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5);
        VBox vBox = new VBox(menuBar);
        menuBar.setStyle("-fx-background-color : #819dff;");
        
        // Open file clicked
        menuItem2.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            String fileName = "resources/" + selectedFile.getName();
            File oFile = new File(fileName);
            try {
                Files.copy(selectedFile.toPath(), oFile.toPath());
                workWithText(stage, oFile);
            } catch (IOException ex) {
                // TODO WYJATEK - NIE UDALO SIE SKOPIOWAC
            }
        });
        
        return vBox;
    }
    
    public void workWithText(Stage stage, File file) throws FileNotFoundException, IOException{
        Scanner sc = new Scanner(file);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Label top = new Label("Try to remember the text");
        top.setStyle("-fx-font-size: 30pt;");
        gridPane.setAlignment(Pos.CENTER);
        
        boolean isFirstTime = true;
        
        for (int i = 0; sc.hasNextLine(); i++) {
            String line = sc.nextLine();
            if (line.contains("endOfFile")){
                isFirstTime = false;
                int index = line.substring(10).indexOf(" ");
                numOfWords = Integer.parseInt(line.substring(10, 10 + index));
                percentOfWords = Integer.parseInt(line.substring(11 + index));          
            }
            else {
                int wordLength = 0;
                int word = 0;
                for (int j = 0; j < line.length(); j++){                  
                    if (!Character.isWhitespace(line.charAt(j)) && j + 1 < line.length())
                        wordLength++;
                    else {
                        Label label = new Label(line.substring(j - wordLength, j));
                        label.setStyle("-fx-font-size: 15pt;");
                        gridPane.add(label, word, i);
                        wordLength = 0;
                        word++;
                    }
                }
                if (isFirstTime)              
                    numOfWords += word;
            }    
        }
        if (isFirstTime){
            Files.writeString(file.toPath(), "\nendOfFile " + numOfWords + ' ' + percentOfWords, StandardOpenOption.APPEND);
        }
        Button buttonOk = new Button("Ok!");
        vbox.setSpacing(30);
        vbox.getChildren().addAll(top, gridPane, buttonOk);
        
        buttonOk.setOnAction((ActionEvent e) -> {
            top.setText("Complete the text");
            percentOfWords += 5; 
            removeWords(stage, vbox, numOfWords, percentOfWords);
        });
        
        borderPane.setTop(menuLoad(stage));
        borderPane.setCenter(vbox);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }
    
    public void removeWords(Stage stage, VBox vbox, final int numOfWords, final int percentOfWords){ 
        
        Button buttonCheck = new Button("Check");
        vbox.getChildren().remove(2);
        vbox.getChildren().add(buttonCheck);
        Random rand = new Random();
        int numOfWordsToRemove = numOfWords * percentOfWords / 100;
        MyNode [] nodes = new MyNode[numOfWordsToRemove];
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < numOfWordsToRemove; i++){
            nodes[i] = new MyNode();
            int index;
            do 
                index = rand.nextInt(numOfWords);
            while (list.contains(index));
            list.add(index);
            if (gridPane.getChildren().get(index) instanceof Label){
                nodes[i].setIndex(index);
                nodes[i].setIndexX(gridPane.getColumnIndex(gridPane.getChildren().get(index)));
                nodes[i].setIndexY(gridPane.getRowIndex(gridPane.getChildren().get(index)));
                nodes[i].setString(((Label)gridPane.getChildren().get(index)).getText());
                nodes[i].writeAll();
                gridPane.getChildren().remove(index);
                TextField textField = new TextField ();
                textField.setPrefWidth(25);
                gridPane.add(textField, nodes[i].getIndexX(), nodes[i].getIndexY());
            }
        }
       
        buttonCheck.setOnAction((ActionEvent e) -> {
            checkWords(stage, vbox, nodes, numOfWords);
        });
        
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();    
    }
    
    public void checkWords(Stage stage, VBox vbox, MyNode [] nodes, final int numOfWords){ 
        int numOfRight = 0, numOfWrong = 0;
        for (MyNode node : nodes){
            if (getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane) instanceof TextField){
                if ((((TextField)getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane)).getText()).equalsIgnoreCase(node.getString())){
                    numOfRight++;
                    ((TextField)getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane)).setStyle("-fx-control-inner-background: #cafeca;");                  
                }
                else{
                    numOfWrong++;
                    ((TextField)getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane)).setStyle("-fx-control-inner-background: #ffcbcb;");                    
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
        
        Button buttonOk = new Button("Ok");
        vbox.getChildren().remove(2);
        vbox.getChildren().add(buttonOk);
        buttonOk.setOnAction((ActionEvent e) -> {
            ((Label)vbox.getChildren().get(0)).setText("Complete the text");
            for (MyNode node : nodes){
                if (getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane) instanceof TextField){
                    gridPane.getChildren().remove(getNodeByRowColumnIndex(node.getIndexY(), node.getIndexX(), gridPane));    
                    Label label = new Label(node.getString());
                    label.setStyle("-fx-font-size: 15pt;");
                    gridPane.add(label, node.getIndexX(), node.getIndexY());
                }
            }
            removeWords(stage, vbox, numOfWords, percentOfWords + plus);
        });
    }
    
    // TODO - POROWNYWANIE SLOW
    
    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }
     
    public static void main(String[] args) {
        launch();
    }
}