public class docTable_data 
{
    private String headline = "";
    private int docLenght = 0;
    private String snippet = "";
    private String docPath = "";
    
    public docTable_data(String headline, int docLength, String snippet, String docPath)
    {
        this.headline = headline;
        this.docLenght = docLength;
        this.snippet = snippet;
        this.docPath = docPath;
    }
    
    //getter methods
    public String getHeadline()
    {
        return headline;
    }
    public int getDocLength()
    {
        return docLenght;
    }
    public String getSnippet()
    {
        return snippet;
    }
    public String getDocPath()
    {
        return docPath;
    }
    
    //setter methods
    public void setHeadLine(String x)
    {
        headline = x;
    }
    public void setDocLength(int x)
    {
        docLenght = x;
    }
    public void setSnippet(String x)
    {
        snippet = x;
    }
    public void setPath(String x)
    {
        docPath = x;
    }
}
