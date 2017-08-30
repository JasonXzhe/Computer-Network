
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.nio.file.*;

public class Server {
	final String path = "C:\\Documents and Settings\\Administrator\\Desktop\\tmp";

	
	public static void main(String args[]) throws IOException{
		String path = "C:\\Documents and Settings\\Administrator\\Desktop\\Copy of tmp";
		
		System.out.println("The path to save files is " + "C:\\Documents and Settings\\Administrator\\Desktop\\Copy of tmp");
		
		String defaultpath = null;
			
		// get full file lists including files under directory nested directory.
		List<String> testlist = new ArrayList<String>();
		Filesnew2 test = new Filesnew2();
		testlist = test.getFlist(path, testlist);
		
		// create Locflist.
		ArrayList<fnode> Locflist = new ArrayList<fnode>();
		
		for (int i=0;i<testlist.size();i++) {
			test.set(testlist, Locflist, i, path);
		}


		// create file list in the directory of java project.
		test.new_file(Locflist);
		
		
		// set Locflist to store abstract path and size of file from full file lists.
		for (int i=0;i<testlist.size();i++) {
			test.set(testlist, Locflist, i, path);
		}
		System.out.println("The Locflist has been created successfully!");
		
		/* debugging to print each element within Locflist for name of file and size of file */
//		System.out.println();
//		for (int i=0;i<Locflist.size();i++) {
//			System.out.println("The "+i+" filename of Locflist is: "+Locflist.get(i).filename);	
//			System.out.println("The "+i+" filesize of Locflist is: "+Locflist.get(i).checksum);
//		}
		ServerSocket serverSocket = new ServerSocket(50123);
		
		// create socket interface for server side.
		try {
			
			while (true) {
				
			// Create a server socket

			System.out.println("Server started at "+ new Date()+'\n');
			
			// Listen for a connection request
			Socket socket = serverSocket.accept();
			
			// Create data input and output streams
			DataInputStream inputFromClient = new DataInputStream(
					socket.getInputStream());
			DataOutputStream outputToClient = new DataOutputStream(
					socket.getOutputStream());

					String cmd = inputFromClient.readUTF();
					System.out.println(cmd);
					if (cmd.contentEquals("sync")) {
						
						System.out.println("Send locfilelist.txt to client...");

						test.sendfile("locfilelist.txt", socket, "default");
						}
					else if (cmd.contentEquals("receive deletefilelist")) {
						
						
						test.receivefile(socket, "delete.txt","default");
						
						ArrayList<String> deletelist = new ArrayList<String>();
						File f = new File("delete.txt");
						FileReader read = new FileReader(f);
						BufferedReader bffer = new BufferedReader(read);
						while (bffer.ready()) {
							deletelist.add(bffer.readLine());
						}
						bffer.close();
						read.close();
						
						test.delete(deletelist, path, true);
									
						
						ArrayList<String> flistadd = new ArrayList<String>();
						File fadd = new File("addfile.txt");
						FileReader readadd = new FileReader(fadd);
						BufferedReader bfferadd = new BufferedReader(readadd);
						while (bfferadd.ready()) {
							flistadd.add(bfferadd.readLine());
						}
						bfferadd.close();
						readadd.close();
						
						System.out.println();
					
						for (String temp : flistadd){

							Socket socket_bundle = serverSocket.accept();
								
								test.receivefile(socket_bundle, temp, path);
						}
						
					}
					else if (cmd.contentEquals("recceive addfilelist")){
						test.receivefile(socket, "addfile.txt", "default");
					}
					}	
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
		
		
	}
}
