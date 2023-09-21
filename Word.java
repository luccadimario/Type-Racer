public class Word {
    private int startCord;
    private int endCord;
    private Object highlightTag;
    private String word;
    public Word(int start, int end, Object tag, String word) {
        startCord = start;
        endCord = end;
        highlightTag = tag;
        this.word = word;
    }
    /**
    * returns the start coordinate of the word object
    */
    public int getStart()
    {
        return startCord;
    }
    /**
    * returns the end coordinate of the word object
    */
    public int getEnd()
    {
        return endCord;
    }
    /**
    * returns the object that holds the Highlight tag of the word object
    */
    public Object getTag()
    {
        return highlightTag;
    }
    /**
    * returns the word String of the word object
    */
    public String getWord()
    {
        return word;
    }
    /**
    * sets the Highlight tag of the word Object
    */
    public void setTag(Object obj)
    {
        highlightTag = obj;
    }
}