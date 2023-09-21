import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.text.DecimalFormat;
import java.io.*;

/** TypeRacer.java
* @author Lucca DiMario
* @since  June 2022
* Creates the outline a type racer game
*/
public class TypeRacer{
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private DecimalFormat twoPlaces = new DecimalFormat("#.##");
    private Highlighter.HighlightPainter orangePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
    private Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    private Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
    private ArrayList<Word> words = new ArrayList<Word>();
    private String[] prompt;
    private String text = "";
    private JFrame frame = new JFrame();
    private JPanel textHolder = new JPanel();
    private JPanel gameInfo = new JPanel();
    private JPanel progressBars = new JPanel();
    private JProgressBar easy, medium, hard, you;
    private JTextArea textEnter = new JTextArea("Start Typing And Your Text Will Appear Here");
    private Highlighter textHighlighter = textEnter.getHighlighter();
    private JTextArea promptHolder = new JTextArea();
    private Highlighter promptHighlighter = promptHolder.getHighlighter();
    private JLabel WPMDisplay = new JLabel("Current WPM(Based on Correctness): 0");
    private JLabel timer = new JLabel("0:00");
    private JLabel accuracyDisplay = new JLabel("Accuracy: 0%");
    private JLabel numberOfWords = new JLabel();
    private JLabel centerDisplay = new JLabel();
    private String currentWord = "";
    private String typed = "";
    private boolean endOfText = false;
    private double accuracy = 0.0;
    private boolean timerStarted, easyStarted, mediumStarted, hardStarted = false;
    private int numCorrect;
    private JPanel typeHolder = new JPanel();
    private int easyWords, mediumWords, hardWords, charsTyped, charsCorrect, wordNum, WPM, charCount, time, endTime;
    private TextReader tr;
    private TextReader tr2;
    private TextWriter tw;
    private String checkWord;
    private boolean wordCorrect = true;
    private int correctChar = -1;


    


    public TypeRacer(String readName, String writeName) throws IOException, BadLocationException {
        // initalizes text readers and writers for files that are needed
        tr = new TextReader(readName); 
        tr2 = new TextReader(writeName);
        tw = new TextWriter(writeName);
        prompt = tr.readText();
        
        
        progressBars.setLayout(new GridLayout(1,4));

        easy = new JProgressBar(0,prompt.length); //progress bar for easy bot
        easy.setStringPainted(true);
        easy.setString("Easy 30WPM: " + easyWords + "/" + prompt.length);
        easy.setForeground(Color.GREEN);

        medium = new JProgressBar(0,prompt.length); //progress bar for medium bot
        medium.setStringPainted(true);
        medium.setString("Medium 60WPM: " + mediumWords + "/" + prompt.length);
        medium.setForeground(Color.ORANGE);

        hard = new JProgressBar(0,prompt.length); //progress bar for hard bot
        hard.setStringPainted(true);
        hard.setString("Hard 120WPM: " + hardWords + "/" + prompt.length);
        hard.setForeground(Color.RED);

        you = new JProgressBar(0,prompt.length); //progress bar for typers typing
        you.setStringPainted(true);
        you.setString("You: " + wordNum + "/" + prompt.length);
        you.setForeground(Color.MAGENTA);

        progressBars.add(easy); //adds all bars to a jpanel
        progressBars.add(medium);
        progressBars.add(hard);
        progressBars.add(you);

        numberOfWords.setText("Words typed: " + wordNum + "/" + prompt.length); 

        frame.setLayout(new BorderLayout());
        frame.add(textHolder, BorderLayout.WEST);
        frame.add(typeHolder, BorderLayout.EAST);
        frame.add(gameInfo, BorderLayout.NORTH);
        frame.add(progressBars, BorderLayout.SOUTH);
        frame.add(centerDisplay, BorderLayout.CENTER);

        centerDisplay.setText("<html>Press Any Key To Start<br><br>Press SPACE To Check The Word That You Most Recently Typed<html>");

        gameInfo.setLayout(new GridLayout(1,4));
        gameInfo.add(timer);
        gameInfo.add(WPMDisplay);
        gameInfo.add(accuracyDisplay);
        gameInfo.add(numberOfWords);

        textHolder.add(promptHolder);
        typeHolder.add(textEnter);

        promptHolder.setLineWrap(true);
        promptHolder.setWrapStyleWord(true);

        textEnter.setLineWrap(true);
        textEnter.setWrapStyleWord(true);
        textEnter.addKeyListener(new typeListener());
        textEnter.setPreferredSize(new Dimension(screen.width*13/32, screen.height*3/4)); //sets size of text field
        textEnter.setFont(new Font(Font.SERIF, Font.BOLD, 15));

        promptHolder.addKeyListener(new typeListener());
        promptHolder.setEditable(false);
        promptHolder.setPreferredSize(new Dimension(screen.width*13/32,screen.height*3/4)); //sets size of text field
        promptHolder.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        textEnter.setEditable(false);

        
        for(String str: prompt) { // for loop to append each string returned from the text reader to a string and then creates an arrayList which holds the word objects
            text += str + " ";
            words.add(new Word(charCount, charCount + str.length(), null, str));
            charCount += (str.length() + 1);
        }
        charCount = 0;
        promptHolder.setText(text);

        words.get(wordNum).setTag(promptHighlighter.addHighlight(words.get(wordNum).getStart(), words.get(wordNum).getEnd(), orangePainter)); //sets first highlight




        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0,0,1280,660);
        frame.setResizable(true);
        frame.setVisible(true);



    }
    /**
    * handles the highlighting of the word, also removes highlights of the word being highlighted
    */
    public void highlightWord() throws BadLocationException
    {
        if(words.get(wordNum).getTag() != null) { //removes highlight if there is a highlight on current word
            promptHighlighter.removeHighlight(words.get(wordNum).getTag());
            words.get(wordNum).setTag(null);
        }
       
        currentWord = prompt[wordNum];
        
        words.get(wordNum).setTag(promptHighlighter.addHighlight(words.get(wordNum).getStart(), words.get(wordNum).getEnd(), orangePainter));

    }
    /**
    * handles the logic when space is pressed, checks the word, updates WPM and Average, rehighlights, checks if text is over.
    */
    public void spacePressed()
    {
        if(!endOfText) {
            if(correctChar == 0) { //sets the word to be checked
                checkWord = typed.substring(correctChar, typed.length());
            }
            else {
                
                checkWord = typed.substring(correctChar + 1, typed.length());
                
            }
            
            if(wordCheck(checkWord))
            {
                numCorrect++; //num correct words for WPM
                charsCorrect += (words.get(wordNum).getWord().length() + 1); 
                charsTyped += (words.get(wordNum).getWord().length());
                try {
                    promptHighlighter.removeHighlight(words.get(wordNum).getTag());
                    words.get(wordNum).setTag(null);
                    words.get(wordNum).setTag(promptHighlighter.addHighlight(words.get(wordNum).getStart(), words.get(wordNum).getEnd(), greenPainter));
                    
                }
                catch(Exception e) {}
                correctChar = correctChar + (words.get(wordNum).getWord().length()) + 1;
                wordNum++;
                wordCorrect = true;

            }
            else {
                try { //removes highlight on word and sets it to a red Highlihgt
                    promptHighlighter.removeHighlight(words.get(wordNum).getTag());
                    words.get(wordNum).setTag(null);
                    words.get(wordNum).setTag(promptHighlighter.addHighlight(words.get(wordNum).getStart(),words.get(wordNum).getEnd(),redPainter));
                }
                catch(Exception e) {}
                wordCorrect = false;
                charsTyped += (typed.length() - correctChar);
            }
            
            
            if(wordNum == prompt.length)
            {
                endOfText = true;
            }
            numberOfWords.setText("Words typed: " + wordNum);
            you.setString("You: " + wordNum + "/" + prompt.length);
            you.setValue(wordNum);
            WPM = (int)(numCorrect/(((double)time)/60));
            WPMDisplay.setText("Current WPM(Based on Correctness):" + WPM);
            if(charsTyped != 0) {
                accuracy = ((double)charsCorrect)/(charsTyped);
            }
            else {
                accuracy = 0.0;
            }
            
            accuracyDisplay.setText("Accuracy: " + twoPlaces.format(accuracy*100) + "%");

            if(wordCorrect) { //if the word is correct, highlights next word
                try {
                highlightWord();
                }
                catch(Exception e)
                {
                    endOfText = true; //if cant highlight then text is over
                }
            }
            
            if(endOfText == true) //if text is over runs game over method
            {
                runGameOver();
            }
        }
        
        
        
        
    }
    /**
    * if text is over this method runs, handles setting up displays for how the typer performed as well as reads and writes to the save file
    */
    public void runGameOver()
    {
        endTime = time; //gets end time to make sure of no time addition issues
        String strSeconds = "";
        int minutes = 0;
        if(endTime%60 < 10) {
            strSeconds = ("0" + endTime%60);
        }
        else {
            strSeconds = (endTime%60 + "");
        }
        minutes = endTime/60;
        timer.setText(minutes + ":" + strSeconds);
        
        String bestWPMAccuracy = "";
        try{
            bestWPMAccuracy = tr2.findGreatestWPM(); //finds greatest wpm as a string that also includes the accuracy
        }
        catch(Exception e) {
            
        }
        String[] bestArray = bestWPMAccuracy.split(" "); //splits info from save file
        int bestWPM = Integer.parseInt(bestArray[0]);
        double bestAccuracy = Double.parseDouble(bestArray[1]);
        
        //handles all conditions of ending scenerios
        if(bestWPM < WPM) 
        {
            int oldBest = bestWPM;
            double oldAccuracy = bestAccuracy;
            bestWPM = WPM;
            bestAccuracy = accuracy * 100;
            centerDisplay.setText("<html>GOOD JOB YOU FINISHED!<br><br>AVG WPM: " + WPM + "<br><br>Final Time: " + minutes + ":" + strSeconds + "<br><br>Prompt Length: " + wordNum + " Words<br><br>Accuracy: "+ twoPlaces.format(accuracy*100) + "%<br><br> You got a new best WPM record of " + bestWPM + " WPM with an accuracy of " + twoPlaces.format(bestAccuracy) + "%<br><br>You beat the old record of " +oldBest + "WPM that had an accuracy of " + oldAccuracy + "%<html>");
        }
        else if(bestWPM == WPM) {
            if(accuracy > bestAccuracy)
            {
                double oldAccuracy = bestAccuracy;
                bestAccuracy = accuracy;
                centerDisplay.setText("<html>GOOD JOB YOU FINISHED!<br><br>AVG WPM: " + WPM + "<br><br>Final Time: " + minutes + ":" + strSeconds + "<br><br>Prompt Length: " + wordNum + " Words<br><br>Accuracy: "+ twoPlaces.format(accuracy*100) + "%<br><br> You matched the record of " + bestWPM + " WPM with an accuracy of " + twoPlaces.format(bestAccuracy) + "%<br><br>You beat the old accuracy of " + oldAccuracy + "%<html>");
            }
            else if(accuracy < bestAccuracy){
                centerDisplay.setText("<html>GOOD JOB YOU FINISHED!<br><br>AVG WPM: " + WPM + "<br><br>Final Time: " + minutes + ":" + strSeconds + "<br><br>Prompt Length: " + wordNum + " Words<br><br>Accuracy: "+ twoPlaces.format(accuracy*100) + "%<br><br> You matched the record of " + bestWPM + " WPM with an accuracy of " + twoPlaces.format(bestAccuracy) + "%<br><br> Unfortunately you lost in accuracy with an accuracy of " + twoPlaces.format(accuracy) + "%<html>");
            }
            else {
                centerDisplay.setText("<html>GOOD JOB YOU FINISHED!<br><br>AVG WPM: " + WPM + "<br><br>Final Time: " + minutes + ":" + strSeconds + "<br><br>Prompt Length: " + wordNum + " Words<br><br>Accuracy: "+ twoPlaces.format(accuracy*100) + "%<br><br> You matched the record of " + bestWPM + " WPM with an accuracy of " + twoPlaces.format(bestAccuracy) + "html>");
            }
        }
        else {
            centerDisplay.setText("<html>GOOD JOB YOU FINISHED!<br><br>AVG WPM: " + WPM + "<br><br>Final Time: " + minutes + ":" + strSeconds + "<br><br>Prompt Length: " + wordNum + "<br><br>Accuracy: "+ twoPlaces.format(accuracy*100) + "%<br><br> The current WPM record is " + bestWPM + " WPM with an accuracy of " + bestAccuracy + "%<html>");
        }
        try {
            tw.write("AVG WPM: " + WPM + " Final Time: " + minutes + ":" + strSeconds + " Prompt Length: " + wordNum + " Accuracy: " + twoPlaces.format(accuracy*100) + "%"); //writes info string to save file
        }
        catch(Exception e) {}
        
        
        

    }
    /**
    * checks if the typed word matches the word in the prompt
    */
    public boolean wordCheck(String str)
    {
        try {
            if(str.equals(prompt[wordNum]))
            {
            return true;
            }
        }
        catch(Exception e) {}
        
        return false;
    }
    /** 
    * finds the last space in a string
    */
    public int findLastSpace(String str)
    {
        for(int i = str.length() - 1; i>=0; i--) {
            if(str.charAt(i) == ' ')
            {
                return i;
            }
        }
        return -1;
    }
    /*
    * starts the thread that updates the timer, WPM, and accuracy(by character) every second
    */
    public void timerStart()
    {
        if(!timerStarted) //makes sure more than one threads doest start
        {
            new Thread(() -> {
                while(true) {
                    if(!endOfText) {
                        time++;
                        String strSeconds = "";
                        int minutes = 0;
                        if(time%60 < 10) {
                            strSeconds = ("0" + time%60);
                        }
                        else {
                            strSeconds = (time%60 + "");
                        }
                        minutes = time/60;
                        timer.setText(minutes + ":" + strSeconds);
                        WPM = (int)(numCorrect/(((double)time)/60));
                        WPMDisplay.setText("Current WPM(Based on Correctness): " + WPM);
                        if(charsTyped != 0) {
                            accuracy = ((double)charsCorrect)/(charsTyped);
                        }
                        else {
                            accuracy = 0.0;
                        }
                        accuracyDisplay.setText("Accuracy: " + twoPlaces.format(accuracy*100) + "%");
                        try {
                            Thread.sleep(1000);
                        }
                        catch(Exception e){}
                    }
                
                }   
            }).start();
        timerStarted = true;
        }
    }
    /**
    * Starts the thread that holds the easy bot the typer plays against
    */
    public void easyStart()  /*makes sure more than one threads doest start*/{
        if(!easyStarted) {
            easyStarted = true;
            new Thread(() -> {
                while(easyWords < prompt.length) {
                    try{
                        Thread.sleep(2000);
                    }
                    catch(Exception e) {}
                    if(!endOfText) {
                        easyWords++;
                        easy.setValue(easyWords);
                        easy.setString("Easy 30WPM: " + easyWords + "/" + prompt.length);
                    }
                    
                }
                easy.setString("Easy 30WPM: " + easyWords + "/" + prompt.length + "Done!");
            }).start();
        }
    }
    /**
    * Starts the thread that holds the medium bot the typer plays against
    */
    public void mediumStart()  /*makes sure more than one threads doest start*/{
        if(!mediumStarted) {
            mediumStarted = true;
            new Thread(() -> {
                while(mediumWords < prompt.length) {
                    try{
                        Thread.sleep(1000);
                    }
                    catch(Exception e) {}
                    if(!endOfText) {
                        mediumWords++;
                        medium.setValue(mediumWords);
                        medium.setString("Medium 60WPM: " + mediumWords + "/" + prompt.length);
                    }
                }
                medium.setString("Medium 60WPM: " + mediumWords + "/" + prompt.length + "Done!");
            }).start();
        }
    }
     /**
    * Starts the thread that holds the hard bot the typer plays against
    */
    public void hardStart() {
        if(!hardStarted) /*makes sure more than one threads doest start*/{
            hardStarted = true;
            new Thread(() -> {
                while(hardWords < prompt.length) {
                    try{
                        Thread.sleep(500);
                    }
                    catch(Exception e) {}
                    if(!endOfText) {
                        hardWords++;
                        hard.setValue(hardWords);
                        hard.setString("Hard 120WPM: " + hardWords + "/" + prompt.length);
                    }
                }
                hard.setString("Hard 120WPM: " + hardWords + "/" + prompt.length + "Done!");
            }).start();
        }
    }
   
    private class typeListener implements KeyListener 
    {
        /**
        * handles the keys pressed, different logic for space, backspace, and a character typed
        */
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == 8) {
                if(typed.charAt(typed.length() - 1) == ' ') //if backspacing through a space
                {
                    typed = typed.substring(0, typed.length() - 1);
                    wordNum--;
                    if(wordCorrect || (typed.length() <= correctChar))
                    {
                        typed = typed + " "; // does not let you backspace if the previous word was correct
                        wordNum++;
                    }
                    else{
                        wordNum++;
                    }
                
                }
                else if(typed.length() > 0) {
                    typed = typed.substring(0, typed.length() - 1);
                }
            }
            else if(e.getKeyCode() == 32)
            {
                //starts all threads if a space is pressed
                timerStart(); 
                easyStart();
                mediumStart();
                hardStart();
                charsTyped++;
                spacePressed();
                typed = typed + " ";
                
            }
            else if((e.getKeyCode() >= 44 && e.getKeyCode() <= 111) || (e.getKeyCode() == 222) || (e.getKeyCode() >512 && e.getKeyCode() <= 523))
            {
                //starts all threads if a character is typed
                timerStart();
                easyStart();
                mediumStart();
                hardStart();
                typed += e.getKeyChar();
                if(!wordCorrect) { //charsTyped handled in space pressed method if the word is correct
                    charsTyped++;
                }
                

            }
            textEnter.setText(typed);     
            if(!wordCorrect) { //highlights in the typed text if the word is not correct
                try {
                    textHighlighter.removeAllHighlights();
                    textHighlighter.addHighlight(correctChar + 1, typed.length(), redPainter);
                }
                catch(Exception x) {}
                
            }      
        }
        public void keyReleased(KeyEvent e)
        {

        }
        public void keyTyped(KeyEvent e)
        {
            
        }
    }


    public static void main(String[] args) throws IOException, BadLocationException {
        TypeRacer racer = new TypeRacer("Text.txt", "Save.txt");

    }
}
