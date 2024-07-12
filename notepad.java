import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Main extends  JFrame  {
    // Creating all needed variables
    JPanel panel = new JPanel();
    String fileName3 = "";
    String fileName = "";
    boolean SavedOrNot = false;
    UndoManager undoManager = new UndoManager();
    JTextArea textArea = new JTextArea();
    JMenuBar bar = new JMenuBar();
    JMenu File = new JMenu("File");
    JMenu Edit = new JMenu("Edit");
    JMenuItem View = new JMenu("View");
    JMenuItem openItem = new JMenuItem("Open (CTRL+O)");
    JMenuItem saveItem = new JMenuItem("Save (CTRL+S)");
    JMenuItem exitItem = new JMenuItem("Exit (CTRL+E)");
    JMenuItem newItem = new JMenuItem("New (CTRL+N)");
    JMenuItem undoItem = new JMenuItem("Undo (CTRL+Z)");
    JMenuItem redoItem = new JMenuItem("Redo (CTRL+R)");
    JMenuItem fontItem = new JMenuItem("Font");
    JMenuItem timeItem = new JMenuItem("Time/Date");
    JMenu zoomMenu = new JMenu("Zoom");
    JMenuItem zoomInItem = new JMenuItem("Zoom in (CTRL+P)");
    JMenuItem zoomOutItem = new JMenuItem("Zoom Out (CTRL+M)");
    JMenuItem defaultZoomItem = new JMenuItem("Default Zoom");
    JCheckBoxMenuItem statusBar = new JCheckBoxMenuItem("Status Bar");
    JCheckBoxMenuItem WordWrap = new JCheckBoxMenuItem("Word Wrap");
    JFileChooser fileChooser = new JFileChooser();
    int FontSize = 40;
    public Font Default = new Font("Georgia", Font.PLAIN, FontSize);
    JScrollPane barPane = new JScrollPane(textArea);
    JLabel statusBarLC = new JLabel("Line: 1 Cols: 1");
    JLabel statusBarC = new JLabel("                                                       Characters: 0");
    int percentage = 100;
    JLabel percentageLabel = new JLabel("Zoom: "+percentage+"%");

    Main() {
        //Main code For GUI
        WordWrap.setState(true);
        statusBar.setState(true);
        setTitle("Notepad");
        setSize(600, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(barPane, BorderLayout.CENTER);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(Default);
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        textArea.addCaretListener(e-> {
            try {
                //Updating Status Bar
                updateStatusBar();
                updateStatusBarCaret();
            } catch (BadLocationException ex) {
                System.out.println("problem in first try");
                throw new RuntimeException(ex);
            }
        });
        //Implementing all GUI Elements
        addAndImplement();
        functionButtons();
        addButtons();
        //Adding GUI Components
        panel.setLayout(new BorderLayout());
        panel.add(statusBarLC, BorderLayout.WEST);
        panel.add(statusBarC,BorderLayout.CENTER);
        panel.add(percentageLabel,BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);
        add(bar, BorderLayout.NORTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        //Instance of Main code
        Main main = new Main();
        System.out.println(main);
    }

    public void addAndImplement() {
        //Adding Menu Items to Menu
        File.add(newItem);
        File.add(openItem);
        File.add(saveItem);
        File.add(exitItem);
        Edit.add(undoItem);
        Edit.add(redoItem);
        Edit.add(fontItem);
        Edit.add(timeItem);
        zoomMenu.add(zoomInItem);
        zoomMenu.add(zoomOutItem);
        zoomMenu.add(defaultZoomItem);
        View.add(zoomMenu);
        View.add(statusBar);
        View.add(WordWrap);
        //Adding Menu to MenuBar
        bar.add(File);
        bar.add(Edit);
        bar.add(View);
    }

    public void functionButtons() {
        //Calling Functions for the Methods
        openItem.addActionListener(e -> open());
        saveItem.addActionListener(e -> save());
        exitItem.addActionListener(e -> System.exit(0));
        newItem.addActionListener(e -> newFile());
        undoItem.addActionListener(e -> undo());
        redoItem.addActionListener(e -> redo());
        fontItem.addActionListener(e -> fontAction());
        zoomInItem.addActionListener(e -> zoomIn());
        zoomOutItem.addActionListener(e -> zoomOut());
        defaultZoomItem.addActionListener(e -> {
            textArea.setFont(Default);
            percentage = 100;
            percentageLabel.setText("Zoom: "+percentage+"%"+"\n");
        });
        WordWrap.addActionListener(e -> {
            if (WordWrap.getState()) {
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
            } else {
                textArea.setWrapStyleWord(false);
                textArea.setLineWrap(false);
            }
        });
        statusBar.addActionListener(e -> {
            if(!statusBar.getState()){
                panel.setVisible(false);
            }
            if(statusBar.getState()) {
                panel.setVisible(true);
            }
        });
        timeItem.addActionListener(e->{
            LocalTime time = LocalTime.now();
            LocalDate time2 = LocalDate.now();
            int hour = time.getHour();
            int min = time.getMinute();
            String dateTime = hour+":"+min+" "+ time2;
            textArea.setText(textArea.getText()+dateTime);
        });

    }
    public void addButtons(){
        textArea.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //do Nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //Reacting to ShortCut Keys
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_N){
                    newFile();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_O){
                    open();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_S){
                    save();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_E){
                    System.exit(0);
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_Z){
                    undo();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_R){
                    redo();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_P){
                    zoomIn();
                }
                if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_M){
                    zoomOut();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //do nothing
            }
        });
    }
    public void updateStatusBar() throws BadLocationException {
        int caret = textArea.getCaretPosition();
        int Lines = textArea.getLineOfOffset(caret)+1;
        int Columns = caret - textArea.getLineStartOffset(Lines -1)+1;
        statusBarLC.setText("Lines: "+Lines+" Cols: "+Columns);
    }
    public void updateStatusBarCaret(){
        //Updating Status Bar
        String caret = textArea.getText();
        int characters = caret.length();
        statusBarC.setText("                                                       Characters: "+characters);
    }
    public void open(){
        //Open Method
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.toString();
            int lastIndex = fileName.lastIndexOf('\\');
            if (lastIndex != -1) {
                setTitle(fileName.substring(lastIndex + 1));
            } else {
                setTitle(fileName);
            }
            if (file.exists() && fileName.endsWith(".txt")) {
                try {
                    FileReader reader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuilder content = new StringBuilder();
                    String lines;
                    while ((lines = bufferedReader.readLine()) != null) {
                        content.append(lines).append("\n");
                    }
                    textArea.setText(content.toString());
                    bufferedReader.close();
                } catch (IOException ex) {
                    System.out.println("problem in 2 try");
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Not a Text File!");
            }
        }
    }
    public void save(){
        //Save Method
        if(SavedOrNot){
            File file = new File(fileName);
            try {
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(textArea.getText());
                bufferedWriter.close();
            } catch (IOException ex) {
                System.out.println("problem in 3 try");
                throw new RuntimeException(ex);
            }
        }else {
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String fileName2 = file.getPath();
                if (!fileName2.contains(".")) {
                    file = new File(fileName2 + ".txt");
                } else {
                    file = new File(fileName2);
                }
                fileName = file.getPath();
                fileName3 = file.getName();
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    SavedOrNot = true;
                } catch (IOException ex) {
                    System.out.println("problem in 4 try");
                    throw new RuntimeException(ex);
                }
            }
        }
        setTitle(fileName3);
    }
    public void newFile(){
        //Create New File
        textArea.setText("");
        SavedOrNot = false;
        setTitle("Notepad");
    }
    public void undo(){
        //Undo Function
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
    public void redo(){
        //Redo Function
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    public void zoomIn(){
        //Zoom in Function
        if (FontSize <= 220) {
            FontSize = FontSize + 5;
            textArea.setFont(new Font(Default.getName(), Default.getStyle(), FontSize));
            percentage = percentage+5;
            percentageLabel.setText("Zoom: "+percentage+"%"+"\n");
        }
    }
    public void zoomOut(){
        //Zoom Out Function
        if (FontSize >= 5) {
            FontSize = FontSize - 5;
            textArea.setFont(new Font(Default.getName(), Default.getStyle(), FontSize));
            percentage = percentage-5;
            percentageLabel.setText("Zoom: "+percentage+"%"+"\n");
        }
    }
    public void fontAction() {
        JFrame fontFrame = new JFrame("Fonts");
        fontFrame.setSize(300,200);
        fontFrame.setResizable(false);
        String[] styles = {"Plain", "Bold", "Italic", "Bold Italic"};
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontBox = new JComboBox<>(fonts);
        JComboBox<String> fontStyle = new JComboBox<>(styles);
        JTextField SizeField = new JTextField();
        JPanel panelOfFonts = new JPanel();
        JPanel panelOfStyle = new JPanel();
        JPanel panelOfSize = new JPanel();
        InitializeFonts(panelOfFonts, fontBox,fontFrame);
        InitializeStyle(panelOfStyle, fontStyle, fontFrame);
        InitializeSize(panelOfSize, SizeField, fontFrame);
        fontFrame.setVisible(true);
    }

    public void InitializeFonts(JPanel panelOfFonts, JComboBox<String> fontBox, JFrame fontFrame) {
        fontBox.setSelectedItem(Default.getFamily());
        JLabel labelForFonts = new JLabel("Choose Font- ");
        panelOfFonts.add(labelForFonts);
        panelOfFonts.add(fontBox);
        fontFrame.add(panelOfFonts, BorderLayout.NORTH);
        addActionToFont(fontBox);
    }
    public void addActionToFont(JComboBox<String> fontBox){
        fontBox.addActionListener(e -> {
            String font = String.valueOf(fontBox.getSelectedItem());
            Default = new Font(font, Default.getStyle(), Default.getSize());
            textArea.setFont(Default);
        });
    }
    public void InitializeStyle(JPanel panelOfStyle, JComboBox<String> fontStyle, JFrame fontFrame){
        if(Default.getStyle() == Font.PLAIN){
            fontStyle.setSelectedItem("Plain");
        } else if (Default.getStyle() == Font.BOLD) {
            fontStyle.setSelectedItem("Bold");
        }else if(Objects.equals(Default.getFontName(), Default.getFamily() + " Bold" + " Italic")){
            fontStyle.setSelectedItem("Bold Italic");
        }
        JLabel styleLabel = new JLabel("Choose Style- ");
        panelOfStyle.add(styleLabel);
        panelOfStyle.add(fontStyle);
        fontFrame.add(panelOfStyle, BorderLayout.CENTER);
        addActionToStyle(fontStyle);
    }
    public void addActionToStyle(JComboBox<String> fontStyle){
        fontStyle.addActionListener(e -> {
            String style = String.valueOf(fontStyle.getSelectedItem());
            switch (style) {
                case "Plain" -> {
                        Default = new Font(Default.getFamily(), Font.PLAIN, Default.getSize());
                    textArea.setFont(Default);
                    System.out.println(Default);
                }
                case "Bold" -> {
                        Default = new Font(Default.getFamily(), Font.BOLD, Default.getSize());
                    textArea.setFont(Default);
                    System.out.println(Default);
                }
                case "Italic" -> {
                        Default = new Font(Default.getFamily(), Font.ITALIC, Default.getSize());
                    textArea.setFont(Default);
                    System.out.println(Default);
                }case "Bold Italic" ->{
                    Default = new Font(Default.getFamily()+" Bold"+" Italic", Font.ITALIC, Default.getSize());
                    textArea.setFont(Default);
                    System.out.println(Default);
                }
                default -> System.out.println("Invalid font style selected: " + style);
            }
        });
    }
    public void InitializeSize(JPanel panelOfSize, JTextField SizeField, JFrame fontFrame){
        JLabel sizeLabel = new JLabel("Font Size- ");
        SizeField.setPreferredSize(new Dimension(30,20));
        panelOfSize.add(sizeLabel);
        panelOfSize.add(SizeField);
        SizeField.setText(String.valueOf(FontSize));
        fontFrame.add(panelOfSize, BorderLayout.SOUTH);
        addActionToSize(SizeField);
    }
    public void addActionToSize(JTextField SizeField){
        SizeField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                 if(e.getKeyCode() == KeyEvent.VK_ENTER){
                     try{
                         FontSize = Integer.parseInt(SizeField.getText());
                         Default = new Font(Default.getFontName(), Default.getStyle(), FontSize);
                         textArea.setFont(Default);
                         SizeField.setText("");
                     }catch(Exception ex){
                         JOptionPane.showMessageDialog(SizeField,"Invalid!");
                     }
                 }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}
