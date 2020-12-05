package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Main extends Application {

    Socket socket;
    PrintWriter pWriter;
    BufferedReader bReader;
    InetAddress ip;
    private Label label_prompt = new Label("当前状态: 未连接");
    private final Label label_ip = new Label("IP:");
    private final Label label_port = new Label("端口:");
    private final Label label_id = new Label("用户名:");
    private final TextField textField_ip = new TextField();
    private final TextField textField_port = new TextField();
    private final TextField textField_name = new TextField();
    private final Button button = new Button("连接服务端");
    private final Button button_send = new Button("发送(Enter)");
    private final Button button_save = new Button("保存消息记录");
    TextArea textArea = new TextArea();
    TextArea textArea_message = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane);
        HBox hBox = new HBox(label_ip, textField_ip, label_port, textField_port, button);
        HBox hBox_ID = new HBox(label_id, textField_name);
        HBox hBox_message = new HBox(textArea_message, button_send);

        textField_ip.setPromptText("请输入连接的IP地址");
        textField_port.setPromptText("请输入连接的端口");
        hBox.setSpacing(10);
        hBox_ID.setSpacing(5);
        hBox.setLayoutX(5);
        hBox.setLayoutY(20);
        hBox_message.setSpacing(10);
        textArea.setLayoutY(50);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(200);
        textArea.setPrefColumnCount(40);
        textArea.setScrollTop(10);
        textArea_message.setWrapText(true);
        textArea_message.setPrefSize(400, 50);
        textArea_message.setPromptText("在此输入文本消息");
        label_prompt.setLayoutX(400);
        label_prompt.setLayoutY(265);
        hBox_ID.setLayoutX(5);
        hBox_ID.setLayoutY(260);
        hBox_message.setLayoutX(5);
        hBox_message.setLayoutY(290);
        button_save.setLayoutX(290);
        button_save.setLayoutY(260);

//      连接按钮事件
        button.setOnAction(event -> {
            try {
                String host = textField_ip.getText();
                int port = Integer.parseInt(textField_port.getText());
                // 创建一个套接字
                socket = new Socket(host, port);
                // 创建一个往套接字中写数据的管道，即输出流，给服务器发送信息
                pWriter = new PrintWriter(socket.getOutputStream());
                // 创建一个从套接字读数据的管道，即输入流，读服务器的返回信息
                bReader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                ip = InetAddress.getLocalHost();
                String strIP = "本地IP为:[" + ip.getHostAddress() + "]的用户加入聊天室";
                pWriter.println(strIP);
                pWriter.flush();
                label_prompt.setText("当前状态: 已连接");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                label_prompt.setText("当前状态: 连接失败");
            } catch (IOException e) {
                e.printStackTrace();
                label_prompt.setText("当前状态: 传输错误");
            }
        });

//      发送消息按钮事件
        button_send.setOnAction(event -> {
            String strName = textField_name.getText();
            String strMsg = textArea_message.getText();
            if (!strMsg.equals("")) {
                pWriter.println("[" + strName + "]" + "(" + ip.getHostAddress() + ") 说:" + strMsg);
                pWriter.flush();
                textArea_message.setText("");
            }
        });

//        Enter快捷键发送
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.ENTER) {
                String strName = textField_name.getText();
                String strMsg = textArea_message.getText();
                if (!strMsg.equals("")) {
                    pWriter.println("[" + strName + "]" + "(" + ip.getHostAddress() + ") 说:" + strMsg);
                    pWriter.flush();
                    textArea_message.setText("");
                }
                ke.consume();
            }
        });

//        消息记录保存
        button_save.setOnAction(event -> {
            FileChooser chooser = new FileChooser(); // 创建一个文件对话框
            chooser.setTitle("保存文件"); // 设置文件对话框的标题
            chooser.setInitialDirectory(new File("D:\\")); // 设置文件对话框的初始目录
            // 创建一个文件类型过滤器
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件(*.txt)", "*.txt");
            // 给文件对话框添加文件类型过滤器
            chooser.getExtensionFilters().add(filter);
            File file = chooser.showSaveDialog(primaryStage); // 显示文件保存对话框
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(file.getAbsolutePath(), false);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(textArea.getText(), 0, textArea.getText().length());
                bufferedWriter.close();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        });
//        开启从客户端接收消息线程
        new GetMsgFromServer().start();

        pane.getChildren().addAll(hBox, textArea, label_prompt, hBox_ID, hBox_message, button_save);
        pane.setPrefSize(500, 350);
        primaryStage.setTitle("聊天室客户端");
        primaryStage.getIcons().add(new Image("file:src/res/client.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    class GetMsgFromServer extends Thread {
        String path = "src/res/notification.mp3";
        Media media = new Media(new File(path).toURI().toString());

        public void run() {
            while (this.isAlive()) {
                try {
                    String strMsg = bReader.readLine();
                    if (strMsg != null) {
                        textArea.appendText(strMsg + "\n");
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.play();
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stop() {
        try {
            String strIP = "本地IP为:[" + ip.getHostAddress() + "]的用户退出聊天室";
            pWriter.println(strIP);
            pWriter.flush();
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
