

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Irc_test extends JFrame {
	public static String s;
    public TextArea     text;
    public TextField    data;
    public SharedObject sentence;
    public static String myName;

    public static void main(String argv[]) {
    	Irc_test.myName=JOptionPane.showInputDialog(null,"","Pseudo ?",JOptionPane.QUESTION_MESSAGE);
		
        if (Irc_test.myName.equals(""))
            Debug.exit("Aucun pseudo fourni.");
        Client.debug_id=Irc_test.myName;
	
        // initialize the system
        Client.init();
		
        // look up the IRC object in the name server
        // if not found, create it, and register it in the name server
        SharedObject so=Client.lookup("IRC");
        if (so == null) {
            so=Client.create(new Sentence());
            Client.register("IRC",so);
        }
        // create the graphical part
        new Irc_test(so);
    }

    public Irc_test(SharedObject s) {
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.setLayout(new FlowLayout());
	
        this.text=new TextArea("",14,100,TextArea.SCROLLBARS_VERTICAL_ONLY);
        this.text.setEditable(false);
        this.text.setForeground(Color.WHITE);
        this.add(this.text);
        Debug.output=this.text;
        
        Panel pane=new Panel();
    	pane.setLayout(new GridLayout(8,1));
        
        this.data=new TextField(20);
        pane.add(this.data,BorderLayout.EAST);

        Button button1=new Button("[ lock read ]");
        button1.addActionListener(new lockReadListener_(this));
        pane.add(button1);
        Button button2=new Button("[ lock write ]");
        button2.addActionListener(new lockWriteListener_(this));
        pane.add(button2);
        Button button3=new Button("[ unlock ]");
        button3.addActionListener(new unlockListener_(this));
        pane.add(button3);
        //Button button4=new Button("[ flush remote so ]");
        Button button4=new Button("[ ---------- ]");
        button4.addActionListener(new flushListener_(this));
        //button4.setEnabled(false);
        pane.add(button4);
        Button read_button=new Button("< read content");
        read_button.addActionListener(new readListener_(this));
        pane.add(read_button);
        Button write_button=new Button("write content >");
        write_button.addActionListener(new writeListener_(this));
        pane.add(write_button);
        Button button5=new Button("clear window");
        button5.addActionListener(new clearListener_(this));
        pane.add(button5);
        pane.setSize(100,1);
        this.add(pane);

        //this.setSize(470,300);
        this.setSize(1200,260);
        this.setResizable(false);
        this.text.setBackground(Color.black); 
        this.setTitle(Irc_test.myName);
        this.show();
		
        this.sentence=s;
    }
}



class readListener_ implements ActionListener {
    Irc_test irc;
    public readListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
		
        // lock the object in read mode
    	//this.irc.sentence.lock_read();
		
        // invoke the method
        Irc_test.s = ((Sentence)(this.irc.sentence.obj)).read();
		
        // unlock the object
        //this.irc.sentence.unlock();
		
        // display the read value
        this.irc.text.append(Irc_test.s+"\n");
    }
}

class writeListener_ implements ActionListener {
    Irc_test irc;
    public writeListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
		
        // get the value to be written from the buffer
        String s = this.irc.data.getText();
        	
        // lock the object in write mode
        //this.irc.sentence.lock_write();
		
        // invoke the method
        ((Sentence)(this.irc.sentence.obj)).write(Irc_test.myName + ": " + s);
        this.irc.data.setText("");
		
        // unlock the object
        //this.irc.sentence.unlock();
    }
}

class lockReadListener_ implements ActionListener {
    Irc_test irc;
    public lockReadListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // lock the object in write mode
        this.irc.sentence.lock_read();
    }
}

class lockWriteListener_ implements ActionListener {
    Irc_test irc;
    public lockWriteListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // lock the object in write mode
        this.irc.sentence.lock_write();
    }
}

class unlockListener_ implements ActionListener {
    Irc_test irc;
    public unlockListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // unlock the object
        this.irc.sentence.unlock();
    }
}

class flushListener_ implements ActionListener {
    Irc_test irc;
    public flushListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        //this.irc.sentence.flush();
    	Debug.msg("----------------------------------------------------------------");
    }
}

class clearListener_ implements ActionListener {
    Irc_test irc;
    public clearListener_(Irc_test i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        this.irc.text.setText("");
    }
}



