package src;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Irc extends JFrame {
	public static String s;
    public TextArea     text;
    public TextField    data;
    public SharedObject sentence;
    public static String myName;

    public static void main(String argv[]) {
    	Irc.myName=JOptionPane.showInputDialog(null,"","Pseudo ?",JOptionPane.QUESTION_MESSAGE);
		
        if (Irc.myName.equals(""))
            Debug.exit("Aucun pseudo fourni.");
        Client.debug_id=Irc.myName;
	
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
        new Irc(so);
    }

    public Irc(SharedObject s) {
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.setLayout(new FlowLayout());
	
        this.text=new TextArea("",10,45,TextArea.SCROLLBARS_VERTICAL_ONLY);
        this.text.setEditable(false);
        this.text.setForeground(Color.WHITE);
        this.add(this.text);
	
        this.data=new TextField(45);
        this.add(this.data);

        Button button1=new Button("[ lock read ]");
        button1.addActionListener(new lockReadListener(this));
        this.add(button1);
        Button button2=new Button("[ lock write ]");
        button2.addActionListener(new lockWriteListener(this));
        this.add(button2);
        Button button3=new Button("[ unlock ]");
        button3.addActionListener(new unlockListener(this));
        this.add(button3);
        Button button4=new Button("[ flush remote so ]");
        button4.addActionListener(new flushListener(this));
        this.add(button4);
        Button read_button=new Button("< read content");
        read_button.addActionListener(new readListener(this));
        this.add(read_button);
        Button write_button=new Button("write content >");
        write_button.addActionListener(new writeListener(this));
        this.add(write_button);
        Button button5=new Button("clear window");
        button5.addActionListener(new clearListener(this));
        this.add(button5);
		
        this.setSize(470,300);
        this.setResizable(false);
        this.text.setBackground(Color.black); 
        this.setTitle(Irc.myName);
        this.show();
		
        this.sentence=s;
    }
}



class readListener implements ActionListener {
    Irc irc;
    public readListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
		
        // lock the object in read mode
    	//this.irc.sentence.lock_read();
		
        // invoke the method
        Irc.s = ((Sentence)(this.irc.sentence.obj)).read();
		
        // unlock the object
        //this.irc.sentence.unlock();
		
        // display the read value
        this.irc.text.append(Irc.s+"\n");
    }
}

class writeListener implements ActionListener {
    Irc irc;
    public writeListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
		
        // get the value to be written from the buffer
        String s = this.irc.data.getText();
        	
        // lock the object in write mode
        //this.irc.sentence.lock_write();
		
        // invoke the method
        ((Sentence)(this.irc.sentence.obj)).write(Irc.myName + ": " + s);
        this.irc.data.setText("");
		
        // unlock the object
        //this.irc.sentence.unlock();
    }
}

class lockReadListener implements ActionListener {
    Irc irc;
    public lockReadListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // lock the object in write mode
        this.irc.sentence.lock_read();
    }
}

class lockWriteListener implements ActionListener {
    Irc irc;
    public lockWriteListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // lock the object in write mode
        this.irc.sentence.lock_write();
    }
}

class unlockListener implements ActionListener {
    Irc irc;
    public unlockListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        // unlock the object
        this.irc.sentence.unlock();
    }
}

class flushListener implements ActionListener {
    Irc irc;
    public flushListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        this.irc.sentence.unlock();
    }
}

class clearListener implements ActionListener {
    Irc irc;
    public clearListener(Irc i) {
    	this.irc = i;
    }
    public void actionPerformed(ActionEvent e) {
        this.irc.text.setText("");
    }
}



