
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Filesnew {		
		
		public List<String> getFlist(String fil, List<String> resultFilename) {
			File f = new File(fil);
			File files[] = f.listFiles();
			
			// to see if the path is empty
			if( f==null ) return resultFilename;
			
			for (File tmpf : files) {
				if (tmpf.isDirectory()) {
					resultFilename.add(tmpf.getPath());
					getFlist(tmpf.getPath(), resultFilename);
				}
				else 
				{
					resultFilename.add(tmpf.getPath());
				}
			}
			return resultFilename;
		}
		
		public String getCurrentPath() {
			String rootPath = getClass().getResource("./").getPath().toString();
			return rootPath;
		}
		
		/* 1. change absolutepath to abstractpath.
		 * 2. add checksum to complete the structure arrays. */
		public void set(List<String> src, ArrayList<fnode> tgt, int idx,String path) {
			File tmp_file = new File(src.get(idx));
			String depath = path.replaceAll("\\\\", "\\\\\\\\");		
			fnode tmp_store = new fnode(src.get(idx).replaceAll(depath, ""),getFileMD5(tmp_file));
			if (!tmp_file.isDirectory()){
				tgt.add(tmp_store);
			}
		}
		
		/* create Locflist to new file so as to send to client easily.
		 * the new file path is the java project path.*/
		public void new_file ( ArrayList<fnode> Locflist ) {
			String newfile = "locfilelist.txt";
			try {
				File newTextfile = new File(newfile);
				FileWriter fw;
				fw = new FileWriter(newTextfile);
				for ( fnode x: Locflist ) {
					fw.write(x.filename);
					fw.write("\n");
					fw.write(x.checksum);
					fw.write("\n");
				}
				fw.close();
				System.out.println("The new file path is: "+ newTextfile.getAbsolutePath());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public void sendfile (String filename, Socket socket, String path) throws IOException {
			int length 		 	 = 0;
			double sumL 	 	 = 0;
			byte[] sendBytes 	 = null;
//			Socket socket 	 	 = null;
			DataOutputStream dos = null;
			FileInputStream fis  = null;
			DataInputStream dis  = null;
			
			boolean bool 	 = false;
			if (!path.equals("default")) {
				filename = path.concat("\\").concat(filename);
			}
			
			try {			
				File file = new File(filename);
				long l = file.length();
//				socket = new Socket();
				
//				socket = new Socket(remoteIP,port);
				
				dos = new DataOutputStream(socket.getOutputStream());
				fis = new FileInputStream(file);
				
				dos.writeUTF("send file");
				dos.flush();
				
				sendBytes = new byte[1024];
				while ((length = fis.read(sendBytes, 0, sendBytes.length))>0) {
					sumL += length;
					System.out.println(filename+" Transfered: "+((sumL/l)*100)+"%");
					dos.write(sendBytes, 0, length);
					dos.flush();				
					if(sumL==l) {
						bool = true;
					}
				} 
				fis.close();
				dos.close();
				socket.close();
		}catch (Exception e) {
					System.out.println("There is error happenning for transferring");
					bool = false;
					e.printStackTrace();
				} 
			finally {
					if (dos!=null)
						dos.close();
					if (fis!=null)
						fis.close();
					if (socket!=null)
						socket.close();
				}
				
				System.out.println(bool?"sucess":"fail");;
				}

		
		public void receivefile (Socket socket,String filename, String path) throws IOException {
			byte[]				inputByte 	= null;
			int					length		= 0;
			DataInputStream		dis 		= null;
			FileOutputStream 	fos 		= null;
			
			try {
				try {
					if (!path.equals("default")) {
						filename = path.concat("\\").concat(filename);
					}
					
					dis = new DataInputStream(socket.getInputStream());
					File f = new File(filename);
					if (!f.exists()) {
//						f.mkdirs();
						f.createNewFile();
						
					}
					
					fos = new FileOutputStream(new File(filename));
					inputByte = new byte[1024];
					System.out.println("Begin to receive file.");
					 while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
		                    fos.write(inputByte, 0, length);  
		                    fos.flush();
//		                    System.out.println("Begin to receive file.");
		                }
					 System.out.println("Finished receiving: "+filename);  
	            } finally {  
	                if (fos != null)  
	                    fos.close();  
	                if (dis != null)  
	                    dis.close();  
	                if (socket != null)  
	                    socket.close();   
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
		
		/* change file to structure arraylist */
		public void rmtflist (String remotefile,ArrayList<fnode> rmtflist ) throws IOException {
			String 	fname = null;
			String	fchm = null;
			
			File f = new File(remotefile);
			FileReader read = new FileReader(f);
			BufferedReader bffer = new BufferedReader(read);
			
			while(bffer.ready()) {

				String str = bffer.readLine();
				if (str.startsWith("\\")) fname = str;
				else {
					fchm = str;
					fnode tmp = new fnode(fname,fchm,true);
					rmtflist.add(tmp);
					fname = null;
					fchm = null;
				}			
			}
			bffer.close();
			read.close();
		}
		
		/* compare structure arraylist between localist and remotelist at Client side 
		 * localist: the flag of node to be deleted is 1
		 * remotelist: the flag of node to be added is 1
		 * create addfilelist.txt*/
		public void fileCompare(ArrayList<fnode> server, ArrayList<fnode> client) throws IOException{
			
			  for(fnode x : client){
				  for(fnode y : server){
					  if (x.filename.equals(y.filename)){
						  x.flag = false;
						  if(x.checksum.equals(y.checksum)){
							  y.flag = false;
							  break;
							  }
						  else continue;
					  }
					  else{
						  x.flag = true; 
						  }
				  }
					
			  }
			  
			  String newfile = "addfilelist.txt";
			  File newTextfile = new File(newfile);
				FileWriter fw;
				fw = new FileWriter(newTextfile);
			  for(int i=0; i<server.size(); i++){
				  if(server.get(i).flag == true){
						try {
							fw.write(server.get(i).filename);
							fw.write("\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				  }else continue;
			  }
			  fw.close();
			  if(newTextfile.length() == 0){
				  System.out.println("The Dir of Client is the same as the Dir of Server");
			  }
			  System.out.println("The new file of addfile path is: "+ newTextfile.getAbsolutePath());
		  }
		  
		  public void delete(ArrayList<fnode> client, String path){
			  try {
				  for(int i=0; i< client.size(); i++){
					  if(client.get(i).flag == true){
						  client.get(i).filename = path.concat(client.get(i).filename);
						  File f = new File(client.get(i).filename);
						  f.delete();	
						  System.out.println("The file "+f.getName().toString()+"has been deleted!");
					  }
				  }  
			  }
			  catch (Exception e){
				  e.printStackTrace();
			  }
		  }
		  
		  /* get MD5 value of files  */
			
		  public static String getFileMD5(File file) {
		    if (!file.isFile()){
		      return null;
		    }
		    MessageDigest digest = null;
		    FileInputStream in=null;
		    byte buffer[] = new byte[1024];
		    int len;
		    try {
		      digest = MessageDigest.getInstance("MD5");
		      in = new FileInputStream(file);
		      while ((len = in.read(buffer, 0, 1024)) != -1) {
		        digest.update(buffer, 0, len);
		      }
		      in.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
		    BigInteger bigInt = new BigInteger(1, digest.digest());
		    return bigInt.toString(16);
		  }
	
		
		public static void main(String args[]){
			String path = "C:/Documents and Settings/Administrator/Desktop/Copy of tmp";
			Filesnew file = new Filesnew();
			List<String> resultFilename = new ArrayList<String>();
			file.getFlist(path, resultFilename);
			for (int i=0;i<resultFilename.toArray().length;i++) {
				System.out.println(resultFilename.toArray()[i]);
			}
			
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}
