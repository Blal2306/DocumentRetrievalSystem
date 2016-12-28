import java.util.Map;
import java.util.TreeMap;

public class dictionary_data {
    
    //instance variables
    private int cf = 0;
    private int df = 0;
    private int offset = 0;
    private Map<Integer, Integer> postingList = new TreeMap<Integer, Integer>();
    
    //constructor
    public dictionary_data(int cf, int df, int offset)
    {
        this.cf = cf;
        this.df = df;
        this.offset = offset;
    }
    
    //setter methods
    public void setCF(int x){cf = x;}
    public void setDF(int x){df = x;}
    public void setOffset(int x){offset = x;}
    
    //getter methods
    public int getCF(){return cf;}
    public int getDF(){return df;}
    public int getOffset(){return offset;}
    
    public void insertToPostings(int docID, int tf)
    {
        postingList.put(docID, tf);
    }
    public int getFromPostings(int docID)
    {
        if(postingList.containsKey(docID))
        {
            return postingList.get(docID);
        }
        else
            return 0;
    }
    public int postingsSize()
    {
        return postingList.size();
    }
    public Map<Integer, Integer> getPostingsList()
    {
        return postingList;
    }
    
}
