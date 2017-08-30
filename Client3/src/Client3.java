import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client2 {
	private static DataOutputStream toServer;
	private static DataInputStream fromServer;
	final static String remoteIP = "52.33.18.161";
	final static int port = 50123;
	int  i =0;
	
	
	public static void main(String args[]) {
		String localpath = "D:\\networks project";
		System.out.println("The path to save files is" + " D:\\networks project");
		System.out.println();
		
		// get full file lists including files under directory nested directory.
		List<String> fullfiles = new ArrayList<String>();
		Filesnew2 profile = new Filesnew2();
		String defaultpath = profile.getCurrentPath();
		System.out.println("The default path is: "+defaultpath);
		fullfiles = profile.getFlist(localpath, fullfiles);
		
		//set Locflist to store abstract name of file and size of file from full file lists.
		ArrayList<fnode> Locflist = new ArrayList<fnode>();
		ArrayList<fnode> Remoteflist = new ArrayList<fnode>();
		for (int i=0;i<fullfiles.size();i++) {
			profile.set(fullfiles, Locflist, i, localpath);
		}
		System.out.println("The Locflist has been created successfully!");
		profile.new_file(Locflist);
		
//		for (int i=0;i<Locflist.size();i++) {
//			System.out.println(Locflist.get(i).filename);
//		}

		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(remoteIP,port);
			
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
			

				toServer.writeUTF("sync");
				toServer.flush();

			
			String cmd = fromServer.readUTF();
			
			if (cmd.contentEquals("send file")) {
				profile.receivefile(socket, "rmtfile.txt","default");
				profile.rmtflist("rmtfile.txt", Remoteflist);

				System.out.println();
				System.out.println();
				profile.fileCompare(Remoteflist, Locflist);
				
				Socket socket1 = new Socket(remoteIP,port);
				// Create an input stream to receive data from the server
				fromServer = new DataInputStream(socket1.getInputStream());
				// Create an output stream to send data to the server
				toServer = new DataOutputStream(socket1.getOutputStream());
				profile.sendfile("addfilelist.txt", socket1,"default", "recceive addfilelist");
								
				
				Socket socket2 = new Socket(remoteIP,port);
				// Create an input stream to receive data from the server
				fromServer = new DataInputStream(socket2.getInputStream());
				// Create an output stream to send data to the server
				toServer = new DataOutputStream(socket2.getOutputStream());
				profile.sendfile("deletefilelist.txt", socket2,"default", "receive deletefilelist");
				}
			
			
			ArrayList<String> flist = new ArrayList<String>();
			File f = new File("addfilelist.txt");
			FileReader read = new FileReader(f);
			BufferedReader bffer = new BufferedReader(read);
			while (bffer.ready()) {
				flist.add(bffer.readLine());
			}
			bffer.close();
			read.close();
			
			/* send files that do not exist at Server to Server */
			for(String temp : flist){
				// Create a socket to connect to the server
				Socket socket_bundle = new Socket(remoteIP,port);
						
				profile.sendfile(temp, socket_bundle, localpath);
			}
			
		}
		catch (IOException ex) {
		}
		
	}
}

