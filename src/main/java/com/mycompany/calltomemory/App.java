package com.mycompany.calltomemory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import org.apache.commons.lang3.StringUtils;



/**
 * JavaFX App
 */
public class App extends Application {

    VBox textVBox = new VBox();
    File file;
    int numOfWords;
    int numOfRows;
    int percentOfWords;
    int memorisingSpeed = 3;
    int mistakeTolerance = 3;
    boolean autosave;
    
    @Override
    public void start(Stage stage) {
        System.setProperty("file.encoding","utf-8");
        stage.setMaximized(true);
        stage.setTitle("Call to Memory");    
        
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuLoad(stage));

        Scene scene = new Scene(borderPane, Color.WHITE);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
        startScene(stage);
    }
    
    public void startScene(Stage stage){
        //Image image = new Image(getClass().getResource("/flower.png").toExternalForm(), 100, 100, false, false);
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #FFFFFF;");
        vbox.setAlignment(Pos.CENTER);
        VBox vbox2 = new VBox();
        vbox2.setStyle("-fx-background-image: url('start_menu.png');" +
        "-fx-background-repeat: stretch;" +
        "-fx-background-size: 832 459;" +
        "-fx-background-position: center center;");
        vbox2.setPrefSize(832, 459);
        vbox2.setAlignment(Pos.CENTER);
        vbox2.setSpacing(30);
        Label label = new Label("Start memorizing your texts!");
        label.setId("large-text");
        
        Button buttonNew = new Button();    
        buttonNew.setId("create-new-button");
        Button buttonOpenPrev = new Button();  
        buttonOpenPrev.setId("open-prev-button");
        Button buttonOpenNew = new Button();
        buttonOpenNew.setId("open-button");
        vbox2.getChildren().addAll(buttonNew, buttonOpenPrev, buttonOpenNew);
        vbox.getChildren().addAll(label, vbox2);
        buttonNew.setOnAction((ActionEvent e) -> {
            createFile(stage);
        });  
        buttonOpenPrev.setOnAction((ActionEvent e) -> {
            openPrevious(stage);
        }); 
        
        buttonOpenNew.setOnAction((ActionEvent e) -> {
            openFile(stage);
        }); 
        
        vbox.setSpacing(30);
        ((BorderPane)(stage.getScene().getRoot())).setCenter(vbox);
    }    
    
    public MenuBar menuLoad(Stage stage){
        MenuBar menuBar = new MenuBar();
        Menu menu1 = new Menu();
        Label l1 = new Label("File");

        menu1.setGraphic(l1);
        MenuItem menuItem1 = new MenuItem("New file...");
        MenuItem menuItem2 = new MenuItem("Open file...");
        MenuItem menuItem3 = new MenuItem("Open previous...");
        MenuItem menuItem4 = new MenuItem("Save");
        menu1.getItems().addAll(menuItem1, menuItem2, menuItem3, menuItem4);
        
        // TODO 
        Menu menu2 = new Menu();
        Label l2 = new Label("Edit");
        menu2.setGraphic(l2);
        
        Menu menu3 = new Menu();
        Label l3 = new Label("Window");
        menu3.setGraphic(l3);
        
        Menu menu4 = new Menu();
        Label l4 = new Label("Options");
        menu4.setGraphic(l4);
        menu4.getItems().add(new MenuItem("Personalization"));
        
        Menu menu5 = new Menu();
        Label l5 = new Label("Help");
        menu5.setGraphic(l5);
        
        menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5);
        menuBar.setStyle("-fx-background-color : #819dff;");
        
        // New file clicked
        menuItem1.setOnAction((ActionEvent e) -> {
            createFile(stage);
        });
        
        // Open file clicked
        menuItem2.setOnAction((ActionEvent e) -> {
            openFile(stage);
        });
        
        // Open previous clicked
        menuItem3.setOnAction((ActionEvent e) -> {
            openPrevious(stage);
        });
        
        
        // Save clicked
        menuItem4.setOnAction((ActionEvent e) -> {
            save(stage);
        });
        
        // Options clicked
        menu4.getItems().get(0).setOnAction((ActionEvent e) -> {
            options(stage);
        });
        
        return menuBar;
    }
    
    public void openFile(Stage stage){
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            String fileName = "resources/" + selectedFile.getName();
            int i = 0;
            if (new File(fileName).exists())
                if (compareFiles(new File(fileName), selectedFile))
                    i = -1;
                else
                    for (i = 1; ; i++)
                        if (!new File(fileName.substring(0, fileName.indexOf(".")) + " (" + i + ")" + fileName.substring(fileName.indexOf("."))).exists())
                            break;
            if (i == 0){
                file = new File(fileName);
                try {
                    Files.copy(selectedFile.toPath(), file.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }             
            else if (i == -1)
                file = new File(fileName);
            else{
                file = new File(fileName.substring(0, fileName.lastIndexOf(".")) + " (" + i + ")" + fileName.substring(fileName.indexOf(".")));
                try {
                    Files.copy(selectedFile.toPath(), file.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            workWithText(stage);
    }
    
    public void workWithText(Stage stage){
        numOfWords = 0;
        numOfRows = 0;
        percentOfWords = 5;
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            Scanner sc = new Scanner(bufferedReader);
            VBox vbox = new VBox();
            vbox.setStyle("-fx-background-color: #FFFFFF;");
            vbox.setAlignment(Pos.CENTER);
            Label top = new Label("Try to remember the text");
            top.setId("large-text");
            textVBox.setAlignment(Pos.CENTER);
            textVBox.getChildren().clear();
        
            boolean isFirstTime = true;
        
            for (int i = 0; sc.hasNextLine(); i++) {
                int word = 0;
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(10);
                //HBox hBox2 = new HBox();
                //hBox2.setAlignment(Pos.CENTER);
                //hBox2.setSpacing(10);
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
                            Label label2 = new Label(line.substring(j - wordLength, j));
                            if (j + 1 == line.length()){                          
                                label = new Label(line.substring(j - wordLength, j + 1));
                                //label2 = new Label(line.substring(j - wordLength, j + 1));
                            }
                            label.setStyle("-fx-font-size: 15pt;");
                            //label2.setStyle("-fx-font-size: 13pt;");
                            // label2.setVisible(false);                     
                            hBox.getChildren().add(label);
                            //hBox2.getChildren().add(label2);
                            wordLength = 0;
                            word++;
                        }
                    }          
                }               
                textVBox.getChildren().add(hBox);
                //textVBox.getChildren().add(hBox2);
                numOfRows++;
                if (isFirstTime)
                    numOfWords += word; 
            }
            if (isFirstTime)
                Files.writeString(file.toPath(), "\nendOfFile " + numOfWords + ' ' + percentOfWords, StandardOpenOption.APPEND);
            Label progress = new Label();
            progress.setStyle("-fx-font-size: 20pt; -fx-background-color : #b5c5ff;");
            Button buttonOk = new Button();
            buttonOk.setId("ok-button");
            vbox.setSpacing(30);
            //textVBox.setSpacing(-10);
            vbox.getChildren().addAll(top, textVBox, progress, buttonOk);
        
            buttonOk.setOnAction((ActionEvent e) -> {
                top.setText("Complete the text");
                removeWords(stage, vbox);
            });
        
            /* TO DO - ENTER PRESSED
        
            stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    if (e.getCode().equals(KeyCode.ENTER)){
                        System.out.println(" !!! k >>>>>  " + new Object(){}.getClass().getEnclosingMethod().getName());
                        top.setText("Complete the text");
                        removeWords(stage, vbox);
                    }      
                }
            });*/
        
            ((BorderPane)(stage.getScene().getRoot())).setTop(menuLoad(stage));
            ((BorderPane)(stage.getScene().getRoot())).setCenter(vbox);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    

    
    public void removeWords(Stage stage, VBox vbox){ 
        final boolean isLastTime;
        Button buttonCheck = new Button();
        buttonCheck.setId("check-button");
        vbox.getChildren().remove(3);
        vbox.getChildren().add(buttonCheck);
        Random rand = new Random();
        int numOfWordsToRemove = percentOfWords;
            System.out.println("1>>>> " + numOfWordsToRemove + "   " + numOfWords);
        if (numOfWordsToRemove >= numOfWords){
            numOfWordsToRemove = numOfWords;
            isLastTime = true;
            System.out.println("2>>>> " + numOfWordsToRemove + "   " + numOfWords);
        }
        else isLastTime = false;
        ((Label)vbox.getChildren().get(2)).setText("  " + String.valueOf(numOfWordsToRemove) + '/' + String.valueOf(numOfWords) + "  ");
        
        MyNode [] nodes = new MyNode[numOfWordsToRemove];
        List<MyPoint> list = new ArrayList<>();
        List<MyPoint> listOfTextFields = new ArrayList<>();
        
        for (int i = 0; i < numOfWordsToRemove; i++){
            nodes[i] = new MyNode();
            int indexRow = 0; // X
            int index = 0;    // Y
            MyPoint point = new MyPoint(0, 0);
            a:
            while(true){ 
                indexRow = rand.nextInt(numOfRows);
                int numOfWordsInPerLine =((HBox)(textVBox.getChildren().get(indexRow))).getChildren().size();
                if (numOfWordsInPerLine == 0)
                    continue;
                index = rand.nextInt(numOfWordsInPerLine);
                point.setX(indexRow); 
                point.setY(index);
                for (MyPoint p: list)
                    if((Integer)p.getX() == indexRow && (Integer)p.getY() == index)
                        continue a;
                break;
            }
            list.add(point);
            if (textVBox.getChildren().get(indexRow) instanceof HBox){               
                if (((HBox)(textVBox.getChildren().get(indexRow))).getChildren().get(index) instanceof Label){
                nodes[i].setIndexX(indexRow);
                nodes[i].setIndexY(index);
                nodes[i].setString(((Label)((HBox)(textVBox.getChildren().get(indexRow))).getChildren().get(index)).getText());
                nodes[i].writeAll();
                ((HBox)(textVBox.getChildren().get(indexRow))).getChildren().remove(index);
                TextField textField = new TextField ();
                textField.setPrefWidth(60);
                point = new MyPoint(indexRow * 10 + index, textField);
                listOfTextFields.add(point);
                textField.textProperty().addListener((ov, prevText, currText) -> {
                    Platform.runLater(() -> {
                    Text text = new Text(currText);
                    text.setFont(textField.getFont());
                    double width = text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d;
                    textField.setPrefWidth(width); 
                    });
                });
                textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                     @Override
                     public void handle(KeyEvent keyEvent) {
                        Platform.runLater(() -> {
                            if (keyEvent.getCode() == KeyCode.ENTER)  {
                                for (MyPoint point: listOfTextFields)
                                    if (point.getY() == textField){
                                        if (listOfTextFields.indexOf(point) == listOfTextFields.size() - 1)
                                            checkWords(stage, vbox, nodes, isLastTime);
                                        else
                                            ((TextField)(listOfTextFields.get(listOfTextFields.indexOf(point) + 1).getY())).requestFocus();
                                        break;
                                    } 
                            }
                        });
                     }
                });  
                
                ((HBox)(textVBox.getChildren().get(indexRow))).getChildren().add(index, textField);
                }
            }
        }
        Comparator<MyPoint> comparator = new Comparator<MyPoint>(){
            @Override
            public int compare(final MyPoint p1, final MyPoint p2){
                return Integer.compare((Integer)p1.getX(), (Integer)p2.getX());
            }
        };
        listOfTextFields.sort(comparator);
        buttonCheck.setOnAction((ActionEvent e) -> {
            checkWords(stage, vbox, nodes, isLastTime);
        });
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
        if (isLastTime && numOfWrong == 0)
            endScene(stage);
        else {
            if (numOfWrong <= mistakeTolerance){
                int plus = memorisingSpeed;
                if (numOfWrong == 0) {
                    plus +=  mistakeTolerance;
                    ((Label)vbox.getChildren().get(0)).setText("Excellent!");
                }      
                else {
                    ((Label)vbox.getChildren().get(0)).setText("You made " + numOfWrong + " mistakes");
                    plus =  mistakeTolerance + plus / numOfWrong ;
                    System.out.println(numOfWrong + " " + plus);
                    if (plus < 0) plus = 0;
                }
                percentOfWords += plus;
            }
        
            Button buttonOk = new Button();           
            buttonOk.setId("ok-button");
            vbox.getChildren().remove(3);
            vbox.getChildren().add(buttonOk);
            
            buttonOk.setOnAction((ActionEvent e) -> {
                if (autosave)
                    save(stage);
                ((Label)vbox.getChildren().get(0)).setText("Complete the text");
                for (MyNode node : nodes){
                    if (((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().get(node.getIndexY()) instanceof TextField){
                        ((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().remove(node.getIndexY());
                        Label label = new Label(node.getString());
                        label.setStyle("-fx-font-size: 15pt;");
                        ((HBox)(textVBox.getChildren().get(node.getIndexX()))).getChildren().add(node.getIndexY(), label);
                        //((Label)((HBox)(textVBox.getChildren().get(node.getIndexX() - 1))).getChildren().get(node.getIndexY())).setVisible(false);
                        // ((Label)((HBox)(textVBox.getChildren().get(node.getIndexX() - 1))).getChildren().get(node.getIndexY())).setStyle("-fx-font-size: 13pt;");
                    }
                }
                removeWords(stage, vbox);
            });        
        }    
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
    
    
    public boolean compareFiles(File firstFile, File secondFile){
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(firstFile), "UTF8"));
            Scanner sc = new Scanner(bufferedReader);
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(secondFile), "UTF8"));
            Scanner sc2 = new Scanner(bufferedReader2);
            while (sc.hasNextLine() && sc2.hasNextLine()){
                String line1 = sc.nextLine();
                String line2 = sc2.nextLine();
                if (line1.compareTo(line2) != 0){
                    System.out.println(line1 + "\n1\n" + line2);
                    return false;
                }
            }
            
            while (sc.hasNextLine()){
                    String line = sc.nextLine();
                    if (line.length() == 0)
                        continue;
                    if (!line.contains("endOfFile"))
                        return false;
            }
            while (sc2.hasNextLine()){
                    String line = sc2.nextLine();
                    if (line.length() == 0)
                        continue;
                    if (!line.contains("endOfFile"))
                        return false;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return true;
    }
    
    public String removeNonAlphanumerics (String string){
        String[] stringArray = string.split("\\W+");
        String result = new String();
        for (int i = 0; i < stringArray.length; i++)
            result += stringArray[i];
        return result;
    }
    
    public void endScene(Stage stage){ 
        VBox vbox = new VBox();
        Label label = new Label ("Koniec");
        label.setId("large-text");
        label.setStyle("-fx-font-size: 30pt;");
        Label label2 = new Label ("tu powinno cos byc...");
        label2.setStyle("-fx-font-size: 20pt;");
        
        Button buttonOk = new Button();    
        buttonOk.setId("ok-button");
        vbox.getChildren().addAll(label, label2, buttonOk);
        vbox.setSpacing(70);
        vbox.setAlignment(Pos.CENTER);

        ((BorderPane)(stage.getScene().getRoot())).setCenter(vbox);
        buttonOk.setOnAction((ActionEvent e) -> {
            startScene(stage);
        });
    }
    
    
    public void createFile(Stage stage){ 
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #FFFFFF;");
        double width = stage.getWidth();
        double height = stage.getHeight();
        Label createText = new Label("Enter your text");
        createText.setId("large-text");

        createText.setStyle("-fx-font-size: 30pt;");
        TextArea textArea = new TextArea ();   
        textArea.setMinSize(width/2.5, height/2);
        textArea.setMaxSize(width/2.5, height/2);
        textArea.setPromptText("Enter the text...");
        textArea.setStyle("-fx-control-inner-background: #d1dbff; -fx-prompt-text-fill: #000000; -fx-font-size: 18pt;");  
        
        Button buttonCreateFile = new Button();  
        buttonCreateFile.setId("create-button");
        buttonCreateFile.setOnAction((ActionEvent e) -> {
            if (!textArea.getText().isEmpty()){
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
                        String fileName = "resources/" + newFile.getName();
                        int i = 0;
                        if (new File(fileName).exists())
                            if (compareFiles(new File(fileName), newFile))
                                i = -1;
                            else
                                for (i = 1; ; i++)
                                    if (!new File(fileName + " (" + i + ")").exists())
                                        break;
                                        
                        if (i == 0){
                            file = new File(fileName);
                            Files.copy(newFile.toPath(), file.toPath());
                        }
                        else if (i == -1)
                            file = new File(fileName);
                        else {
                            file = new File(fileName.substring(0, fileName.indexOf(".")) + " (" + i + ")" + fileName.substring(fileName.indexOf(".")));
                            Files.copy(newFile.toPath(), file.toPath());
                        }

                        workWithText(stage);
                    } 
                    catch (IOException ex) {
                         ex.printStackTrace();
                     }
                }
            }
            
        });
        
        vBox.getChildren().addAll(createText, textArea, buttonCreateFile);
        vBox.setSpacing(50);
        vBox.setAlignment(Pos.CENTER);
        ((BorderPane)(stage.getScene().getRoot())).setTop(menuLoad(stage));
        ((BorderPane)(stage.getScene().getRoot())).setCenter(vBox);
    }
    
    public void save(Stage stage){
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
    }
    
    public void openPrevious(Stage stage){
        Stage newStage = new Stage();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        newStage.setTitle("Previous imports");
        List files = new ArrayList<File>();
        File repo = new File (System.getProperty("user.dir") + "/resources");
        File[] fileList = repo.listFiles();
        int i = 0;
        for (File f : fileList) 
            if (f.getPath().contains(".txt")){
                System.out.println(f);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER);
                if (i % 2 == 1)
                    hbox.setStyle("-fx-background-color: #E4EAFF;");
                Button button = new Button(f.getPath().substring(f.getPath().lastIndexOf("\\") + 1));
                button.setStyle("-fx-font-size: 15pt;");
                hbox.getChildren().add(button);
                vbox.getChildren().add(hbox);
                hbox.setMinHeight(stage.getHeight()/18);
                i++;
                button.setOnAction((ActionEvent e) -> {
                    newStage.close();
                    file = f;
                    workWithText(stage);
                });
            }       
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, stage.getWidth()/3, stage.getHeight()/2);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        newStage.setScene(scene);
        newStage.show();
    }    
    
    public void options(Stage stage){
        Stage newStage = new Stage();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        
        HBox hbox1 = new HBox();
        hbox1.setAlignment(Pos.CENTER);
        Label label1 = new Label("Mistake tolerance: ");
        RadioButton radioButton1 = new RadioButton("low");
        RadioButton radioButton2 = new RadioButton("moderate");
        RadioButton radioButton3 = new RadioButton("high");
        ToggleGroup radioGroup1 = new ToggleGroup();
        radioButton1.setToggleGroup(radioGroup1);
        radioButton2.setToggleGroup(radioGroup1);
        radioButton3.setToggleGroup(radioGroup1);
        hbox1.getChildren().addAll(label1, radioButton1, radioButton2, radioButton3);    
        
        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        Label label2 = new Label("Memorising speed: ");
        RadioButton radioButton4 = new RadioButton("low");
        RadioButton radioButton5 = new RadioButton("moderate");
        RadioButton radioButton6 = new RadioButton("high");
        ToggleGroup radioGroup2 = new ToggleGroup();
        radioButton4.setToggleGroup(radioGroup2);
        radioButton5.setToggleGroup(radioGroup2);
        radioButton6.setToggleGroup(radioGroup2);
        hbox2.getChildren().addAll(label2, radioButton4, radioButton5, radioButton6);       
        
        CheckBox checkbox = new CheckBox("Autosave");
        
        newStage.setTitle("Personalization");
        Scene scene = new Scene(vbox, stage.getWidth()/4, stage.getHeight()/4);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        newStage.setScene(scene);
        newStage.show();    
        Button buttonSave = new Button("Save"); 
        buttonSave.setOnAction((ActionEvent e) -> {
            newStage.close();
            if (radioButton1.isSelected())
                mistakeTolerance = 0;
            else if (radioButton2.isSelected())
                mistakeTolerance = 3;
            else if (radioButton3.isSelected())
                mistakeTolerance = 5;
            
            if (radioButton4.isSelected())
                memorisingSpeed = 1 - mistakeTolerance;
            else if (radioButton5.isSelected())
                memorisingSpeed = 3 - mistakeTolerance;
            else if (radioButton6.isSelected())
                memorisingSpeed = 5 - mistakeTolerance;
            
            if (checkbox.isSelected())
                autosave = true;
        });
        vbox.getChildren().addAll(hbox1, hbox2, checkbox, buttonSave);
    }
     
    public static void main(String[] args) {
        launch();
    }
}