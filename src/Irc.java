package src;

import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Irc extends Frame {
	public static String s;
    public TextArea     text;
    public TextField    data;
    public SharedObject sentence;
    public static String myName;

    public static void main(String argv[]) {
		
        if (argv.length != 1) {
            System.out.println("java Irc <name>");
            return;
        }
        myName = argv[0];
	
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
	
        setLayout(new FlowLayout());
	
        this.text=new TextArea(10,60);
        this.text.setEditable(false);
        this.text.setForeground(Color.red);
        add(this.text);
	
        this.data=new TextField(60);
        add(this.data);
	
        Button write_button=new Button("write");
        write_button.addActionListener(new writeListener(this));
        this.add(write_button);
        Button read_button=new Button("read");
        read_button.addActionListener(new readListener(this));
        this.add(read_button);

        Button button1=new Button("lock read");
        button1.addActionListener(new lockReadListener(this));
        this.add(button1);
        Button button2=new Button("lock write");
        button2.addActionListener(new lockWriteListener(this));
        this.add(button2);
        Button button3=new Button("unlock");
        button3.addActionListener(new unlockListener(this));
        this.add(button3);
		
        this.setSize(550,300);
        this.text.setBackground(Color.black); 
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
        ((Sentence)(this.irc.sentence.obj)).write(Irc.myName + " wrote " + s);
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



