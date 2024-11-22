package com.grader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bluej.extensions2.*;
import bluej.extensions2.event.ApplicationEvent;
import bluej.extensions2.event.ApplicationListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.jshell.execution.JdiDefaultExecutionControl;

public class BlueJExtension extends Extension {
    ArrayList<File> javaFiles = new ArrayList<>();
    @Override
    public void startup(BlueJ bluej) {
        System.out.println("HELLO");
        String url = "https://mcgrader.onrender.com";

        bluej.setMenuGenerator(new MenuGenerator() {
            @Override
            public MenuItem getToolsMenuItem(BPackage bp) {
                MenuItem menuItem = new MenuItem("MC-BlueJ");
                menuItem.setOnAction(event -> {
                    Stage popupStage = new Stage();
                    popupStage.setTitle("Actions");

                    Button button1 = new Button("6.1 - Taxes");        
                    button1.setOnAction(e -> {
                        Thread getResultThread = new Thread(() -> {
                            try {
                                ArrayList<File> targets = javaFiles.stream()
                                    .filter(file -> file.getName().contains("Taxes"))
                                    .collect(Collectors.toCollection(ArrayList::new));

                                    if (!targets.isEmpty()) {
                                        File[] targetFiles = targets.toArray(new File[0]);
                                        
                                        String result = Grader.getFormattedAIResult(url, "6.1-Taxes", targetFiles);
                                        System.out.println(result);
                                        showResultWindow(result);
                                    } else {
                                        System.out.println("No files found with 'Taxes' in their name.");
                                        showResultWindow("No files found with 'Taxes' in their name.");
                                    }
                            } catch (Exception error) {
                                System.out.println("Somehow managed to crash - [getResultThread]");
                            }
                        });
                        getResultThread.start();
                    });

                    Button button2 = new Button("6.2 - Regular Polygon");
                    button2.setOnAction(e -> {
                        Thread getResultThread = new Thread(() -> {
                            try {
                                ArrayList<File> targets = javaFiles.stream()
                                    .filter(file -> file.getName().contains("Polygon"))
                                    .collect(Collectors.toCollection(ArrayList::new));

                                    if (!targets.isEmpty()) {
                                        File[] targetFiles = targets.toArray(new File[0]);
                                        
                                        String result = Grader.getFormattedAIResult(url, "6.2-RegularPolygon", targetFiles);
                                        System.out.println(result);
                                        showResultWindow(result);
                                    } else {
                                        System.out.println("No files found with 'Polygon' in their name.");
                                        showResultWindow("No files found with 'Polygon' in their name.");
                                    }
                            } catch (Exception error) {
                                System.out.println("Somehow managed to crash - [getResultThread]");
                            }
                        });
                        getResultThread.start();
                    });

                    Button button3 = new Button("Option 3");
                    button3.setOnAction(e -> System.out.println("Option 3 clicked!"));

                    VBox layout = new VBox(10);
                    layout.setPadding(new Insets(10));
                    layout.getChildren().addAll(button1, button2, button3);

                    Scene scene = new Scene(layout, 200, 150);
                    popupStage.setScene(scene);
                    popupStage.show();
                });
                return menuItem;
            }
        });

        bluej.addApplicationListener(new ApplicationListener() {
            @Override
            public void blueJReady(ApplicationEvent e) {
                javaFiles = getAllJavaFiles(bluej);
                System.out.println("Files: " + javaFiles);
                for (File file : javaFiles) {
                    System.out.println("File Path: " + file.getAbsolutePath() + " File Name: " +
                    file.getName());
                }
                
                System.out.println("test2");
            }
        });
    }

    @Override
    public boolean isCompatible() {
        return true;
    }

    @Override
    public String getName() {
        return "MC BlueJ Extension";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public ArrayList<File> getAllJavaFiles(BlueJ bluej) {
        ArrayList<File> javaFiles = new ArrayList<>();
        try {
            BProject[] projects = bluej.getOpenProjects();
            if (projects.length == 0) {
                System.out.println("No projects are open.");
                return new ArrayList<>();
            }
            for (BProject project : projects) {
                File projectDir = project.getDir();
                if (projectDir != null) {
                    javaFiles.addAll(findJavaFiles(projectDir));
                }
            }
        } catch (ProjectNotOpenException e) {
            System.err.println("Error: Project not open.");
            e.printStackTrace();
        }
        return javaFiles;
    }

    private List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    private void showResultWindow(String result) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Grading Result");
            alert.setHeaderText(null);
            alert.setContentText(result);
            alert.showAndWait();
        });
    }

}
