/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceprofileclient;

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	// TODO code application logic here
	SimpleClient client = new SimpleClient(1);
	client.start();
	for (int i = 1; i < 20000; i ++){
	    SimpleClient clientx = new SimpleClient(i+1);
	    clientx.start();
	}
    }
    
    private static class SimpleClient extends Thread{

	public SimpleClient(int i){
	    System.out.println("client" + i);
	}
	@Override 
	public void run(){
	    try {
		//
		TFramedTransport transport;
		transport = new TFramedTransport(new TSocket("localhost", 9696));
		transport.open();

//		TTransport transport = new TSocket("localhost", 9696);
//		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		ProfileService.Client client = new ProfileService.Client(protocol);

		//if (args[0] == "test"){
		if (true) {
		    ProfileInfo result = client.getProfile("2");
		    if (result.id.equals("null")) {
			System.out.println("Not found");
		    } else {
			System.out.println(result);
		    }

//		    if (client.setProfile(new ProfileInfo("C", "C@abc", "22783", new Day(9, 9, 2834), "3"))) {
//			System.out.println("set success");
//		    } else {
//			System.out.println("set failed");
//		    }

		    ProfileInfo result1 = client.getProfile("3");
		    if (result1.id.equals("null")) {
			System.out.println("Not found");
		    } else {
			System.out.println(result1);
		    }
		    ProfileInfo result2 = client.getProfile("4");
		    if (result2.id.equals("null")) {
			System.out.println("Not found");
		    } else {
			System.out.println(result2);
		    }
		transport.close();
	    }
	    } catch (TException e) {
		e.printStackTrace();
	    }
	    
	}
    }
}
