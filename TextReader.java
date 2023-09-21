import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.*;
import java.nio.file.*;
import java.util.*;

public class TextReader {
    private String fileName;
    private File file;
    public TextReader(String fileName) throws IOException {
        this.fileName = fileName;
        file = new File(fileName);
        
    }
    /**
    * reads a random line from the list of prompts and appends each word to a string array then returns said array
    */
    public String[] readText() throws IOException {
        String promptLine;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            Path path = Paths.get(fileName);
            long numLines = Files.lines(path).count();
            int randomLine = (int)(Math.random() * numLines);
            String line = "";
            for(int x = 0; x < randomLine; x++){ //selects a random line from the prompt texts
                br.readLine();
            }
            promptLine = br.readLine();
            
        }
        return(promptLine.split(" ")); //turns the string into a String[] split at spaces
        
    }
    /**
    * reads through the save file saves and finds greatest WPM, returns it and the accuracy for that type
    */
    public String findGreatestWPM() throws IOException {
        int bestWPM = 0;
        double bestAverage = 0.0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) { //parses through the save file lines
                if(Integer.parseInt(line.substring(9,findNextSpace(9,line))) >= bestWPM){
                    if(Integer.parseInt(line.substring(9,findNextSpace(9,line))) == bestWPM) {
                        if(Double.parseDouble(line.substring(findLastSpace(line), line.length() - 1)) > bestAverage) {
                            bestWPM = Integer.parseInt(line.substring(9,findNextSpace(9,line)));
                            bestAverage = Double.parseDouble(line.substring(findLastSpace(line), line.length() - 1));
                        }
                        else {
                            //do nothing, keep old best
                        }
                    }
                    else {
                        bestWPM = Integer.parseInt(line.substring(9,findNextSpace(9,line))); 
                        bestAverage = Double.parseDouble(line.substring(findLastSpace(line), line.length() - 1));
                    }
                }
            }
        }
        
        catch(Exception e) {}
        return (bestWPM + " " + bestAverage); //returns a string so both average and WPM can be returned
    }
    /**
    * finds the next space in a string after a certain char
    */
    public int findNextSpace(int x, String str)
    {
        for(int i = x; i < str.length(); i++)
        {
            if(str.charAt(i) == ' ')
            {
                return i;
            }
        }
        return -1;
    }
    /**
    * finds the last space in a string
    */
    public int findLastSpace(String str)
    {
        for(int i = str.length() - 1; i >= 0; i--)
        {
            if(str.charAt(i) == ' ') 
            {
                return i;
            }
        }
        return -1;
    }


    public static void main(String[] args) throws IOException {
        TextReader tr = new TextReader("Save.txt");
    }
}
