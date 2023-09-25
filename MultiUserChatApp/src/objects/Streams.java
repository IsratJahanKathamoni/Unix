/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 *
 * @author dark_
 */
public class Streams {
    
    private ObjectInputStream in;
	private ObjectOutputStream out;
          
       

	public Streams (ObjectInputStream in, ObjectOutputStream out) {
		this.out = out;
		this.in = in;
	}

	public ObjectInputStream getIS() {
		return this.in;
	}

	public ObjectOutputStream getOS() {
		return this.out;
	}
        
      
            
          
}
