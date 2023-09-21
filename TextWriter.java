import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TextWriter {
    private String fileName;
    private BufferedWriter bwriter;
    private FileWriter fwriter;
    private PrintWriter pwriter;
    private char[] charArray;
    public TextWriter(String fileName) throws IOException {
        this.fileName = fileName;
    }
    /**
    * writes a string to the save file
    */
    public void write(String str) throws IOException{
        fwriter = new FileWriter(fileName, true); 
        bwriter = new BufferedWriter(fwriter); 
        pwriter = new PrintWriter(bwriter);  //wraps a fileWriter into a bufferedWrite into a printWriter
        pwriter.println(str); 
        System.out.println("Data Successfully Written to " + fileName); 
        pwriter.flush(); 
        pwriter.close(); 
        bwriter.close(); 
        fwriter.close(); 
        //closes all writers
        
    }





    public static void main(String[] args) throws IOException {
        
    }
}
