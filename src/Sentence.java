package src;

public class Sentence implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String data;

    public Sentence() {
        this.data=new String("");
    }
	
    public void write(String text) {
        this.data=text;
    }
    public String read() {
        return this.data;	
    }
	
}
