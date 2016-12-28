public class dataset_docTable {
    
    private String headline = "";
    private int docLength = 0;
    private String snippet = "";
    private String path = "";
    
    //accessor and mutator methods
    public void setHeadLine(String h)
    {
        headline = h;
    }
    public String getHeadLine()
    {
        return headline;
    }
    
    public void setDocLength(int x)
    {
        docLength = x;
    }
    public void incrementDocLength()
    {
        docLength++;
    }
    public int getDocLength()
    {
        return docLength;
    }
    public String getSnippet()
    {
        return snippet;
    }
    public void setSnippet(String x)
    {
        snippet = x;
    }
    public void setPath(String x)
    {
        path = x;
    }
    public String getPath()
    {
        return path;
    }
}
