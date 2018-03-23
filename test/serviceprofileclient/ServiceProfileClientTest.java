/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serviceprofileclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class ServiceProfileClientTest {
    static int clientNumber = 3;
    static AtomicLong totalGetTime = new AtomicLong(0);
    static AtomicLong totalSetTime = new AtomicLong(0);
    static AtomicLong totalRemoveTime = new AtomicLong(0);
    static AtomicLong getReq = new AtomicLong(0);
    static AtomicLong setReq = new AtomicLong(0);
    static AtomicLong removeReq = new AtomicLong(0);
    static AtomicLong lastGetTime = new AtomicLong(0);
    static AtomicLong lastSetTime = new AtomicLong(0);
    static AtomicLong lastRemoveTime = new AtomicLong(0);
    static BufferedReader br = null;
    static {
	File fl = new File("output.txt");
	try {
	    FileReader fr = new FileReader(fl);
	    br = new BufferedReader(fr);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    class SimpleClient extends Thread{
	private int index;
	public SimpleClient(int i){
	    index = i;
	}
	
	@Override
	public void run(){
	    try {
		TFramedTransport transport;
		transport = new TFramedTransport(new TSocket("localhost", 9696));
		transport.open();
		
//		TTransport transport = new TSocket("localhost", 9696);
//		transport.open();
		
		TProtocol protocol = new TBinaryProtocol(transport);
		ProfileService.Client client = new ProfileService.Client(protocol);
		
		for (int i = 0; i < 30000; i++){
		   
		    String line = null;
		    if ((line = br.readLine()) == null){
			break;
		    }
		    //System.out.println(line);
		    String[] lineArray = line.split(",");
		    
		    if (i % index == 2){
			long start = System.nanoTime();
			ProfileInfo pi = client.getProfile(lineArray[0]);
			lastGetTime.set(System.nanoTime() - start);
			totalGetTime.addAndGet(lastGetTime.get());
			getReq.addAndGet(1);
			
		    }
		    if (i % index == 1){
			Day d = new Day(Integer.parseInt(lineArray[4]),
				Integer.parseInt(lineArray[5]),
				Integer.parseInt(lineArray[6]));
			ProfileInfo profile = new ProfileInfo(lineArray[1],
				lineArray[2],
				lineArray[3],
				d, lineArray[0]);
			long start = System.nanoTime();
			client.setProfile(profile);
			lastSetTime.set(System.nanoTime() - start);
			totalSetTime.addAndGet(lastSetTime.get());
			setReq.addAndGet(1);
		    }
		    if (i % index == 0){
			long start = System.nanoTime();
			client.removeProfile(lineArray[0]);
			lastRemoveTime.set(System.nanoTime() - start);
			totalRemoveTime.addAndGet(lastRemoveTime.get());
			removeReq.addAndGet(1);
		    }
		}
		transport.close();
	    } catch (Exception e) {
	    }
	}
    }
    
    public ServiceProfileClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ServiceProfileClient.
     */
    @Test
    public void clientRequest() {
        List<Thread> threadList = new LinkedList<>(); 
	for (int i = 0; i < clientNumber; i ++){
	    SimpleClient clientx = new SimpleClient(i+1);
	    threadList.add(clientx);
	    clientx.start();
	}
	for (Thread thread : threadList){
	    try {
		thread.join();
	    } catch (InterruptedException ex) {
		Logger.getLogger(ServiceProfileClientTest.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	

	
	
	System.out.println("SetReq = " + setReq);
	System.out.println("TotalTimeSet = " + totalSetTime);
	System.out.println("LastProcTime = " + lastSetTime);
	System.out.println("AverageProcRate = " + setReq.get()*1000000/(totalSetTime.get()));
	
	System.out.println("GetReq = " + getReq);
	System.out.println("TotalTimeGet = " + totalGetTime);
	System.out.println("LastProcTime = " + lastGetTime);
	System.out.println("AverageProcRate = " + getReq.get()*1000000/(totalGetTime.get()));
	
	System.out.println("RemvReq = " + removeReq);
	System.out.println("TotalTimeRemove = " + totalRemoveTime);
	System.out.println("LastProcTime = " + lastRemoveTime);
	System.out.println("AverageTimeProcRate = " + removeReq.get()*1000000/(totalRemoveTime.get()));
    }
    //Load data from kyotocabinet to use as source for client queries
    
}
