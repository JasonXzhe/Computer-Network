import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client1 {
	private static DataOutputStream toServer;
	private static DataInputStream fromServer;
	final static String remoteIP = "52.33.18.161";
	final static int port = 50123;
	int  i =0;
	
	String path = "E:/private/";
	
//	public Client1() {
////	public Client1( ArrayList<fnode> flist[] ) {
//		try {
//			// Create a socket to connect to the server
//			Socket socket = new Socket(remoteIP,port);
//			
//			// Create an input stream to receive data from the server
//			fromServer = new DataInputStream(socket.getInputStream());
//			
//			
//			// Create an output stream to send data to the server
//			toServer = 
//					new DataOutputStream(socket.getOutputStream());
//			
//			toServer.writeUTF("sync");
//			toServer.flush();
//			
//
//			while (fromServer.readUTF()!=null) {
//				fnode tmp_file = new fnode();
//				String tmp_fname = null;
//				long   tmp_fsize = 0;
//				
//				tmp_file = fromServer.readUTF();
//				i++;
//			}
//			System.out.print(fromServer.readUTF());
//		}
//		catch (IOException ex) {
//		}
//	}

	
	public static void main(String args[]) {
		String localpath = "D:\\networks project";
		
		// get full file lists including files under directory nested directory.
		List<String> fullfiles = new ArrayList<String>();
		Filesnew profile = new Filesnew();
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
		
		for (int i=0;i<Locflist.size();i++) {
			System.out.println(Locflist.get(i).filename);
		}

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
				/* debugging for print structure arrays created by remote file. */
//				for (int i=0;i<Remoteflist.size();i++) {
//					System.out.println("Remoteflist filename is: "+Remoteflist.get(i).filename);
//					System.out.println("Remoteflist flag is: "+Remoteflist.get(i).flag);
//					System.out.println("Locflist filename is: "+Locflist.get(i).filename);
//					System.out.println("Locflist flag is: "+Locflist.get(i).flag);
//				}
				System.out.println();
				System.out.println();
				profile.fileCompare(Remoteflist, Locflist);
				/* debugging for check results of compare between Locflist and Remoteflist. */
//				for (int i=0;i<Remoteflist.size();i++) {
//				System.out.println("Remoteflist filename is: "+Remoteflist.get(i).filename);
//				System.out.println("Remoteflist flag is: "+Remoteflist.get(i).flag);
//				System.out.println("Locflist filename is: "+Locflist.get(i).filename);
//				System.out.println("Locflist flag is: "+Locflist.get(i).flag);
//			}
				
				profile.delete(Locflist, localpath);
				
				
				Socket socket1 = new Socket(remoteIP,port);
				// Create an input stream to receive data from the server
				fromServer = new DataInputStream(socket1.getInputStream());
				// Create an output stream to send data to the server
				toServer = new DataOutputStream(socket1.getOutputStream());
				profile.sendfile("addfilelist.txt", socket1,"default");
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
			
			for(int i=0; i<flist.size(); i++){
				System.out.println("the abd " + flist.get(i).toString());
			}
			
			for (String tmp:flist) {
				Socket socket_bundle = new Socket(remoteIP,port);
				// Create an input stream to receive data from the server
				fromServer = new DataInputStream(socket_bundle.getInputStream());
				// Create an output stream to send data to the server
				toServer = new DataOutputStream(socket_bundle.getOutputStream());
				String cmd_tmp = fromServer.readUTF();
				if (cmd_tmp.contentEquals("send file")) {
				profile.receivefile(socket_bundle, tmp,localpath);
			}

			}
			
		}
		catch (IOException ex) {
		}
		
	}
}
