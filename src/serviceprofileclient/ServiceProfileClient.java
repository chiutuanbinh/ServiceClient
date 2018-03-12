/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceprofileclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 *
 * @author root
 */
public class ServiceProfileClient {
    private static int clientNumber = 0;
    
    private static BufferedReader br = null;
    static{
	File fl= new File("output.txt");
	try {
	    FileReader fr = new FileReader(fl);
	    br = new BufferedReader(fr);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(ServiceProfileClient.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	// TODO code application logic here
	SimpleClient client = new SimpleClient(1);
	client.start();
	for (int i = 1; i < clientNumber; i ++){
	    SimpleClient clientx = new SimpleClient(i+1);
	    clientx.start();
	}
    }
    
    private static class SimpleClient extends Thread{
	private int index;
	public SimpleClient(int i){
	    index = i;
	    System.out.println("client" + i);
	}
	@Override 
	public void run(){
	    try {
		//
		TFramedTransport transport;
		transport = new TFramedTransport(new TSocket("localhost", 9696));
		transport.open();

		TProtocol protocol = new TBinaryProtocol(transport);
		ProfileService.Client client = new ProfileService.Client(protocol);
		
		for (int i = 0; i < 100000; i++) {
		    String line = null;
		    if ((line = br.readLine()) == null)
			break;
		    String[] lineArray = line.split(",");

		    if (i%index == 0)
			client.getProfile(lineArray[0]);
		    if (i%index == 1){
			Day d = new Day(Integer.parseInt(lineArray[4]),
				Integer.parseInt(lineArray[5]),
				Integer.parseInt(lineArray[6]));
			ProfileInfo profile = new ProfileInfo(lineArray[1],
				lineArray[2],
				lineArray[3],
				d, lineArray[0]);
			client.setProfile(profile);
		    }
//		    if (i%index == 2){
//			
//		    }
		}
		
		transport.close();
	    } catch (TException e) {
		e.printStackTrace();
	    } catch (IOException e){
		e.printStackTrace();
	    }
	}
    }
}
