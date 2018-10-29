import java.io.*;
import java.net.*;
import java.util.*;
public class MusicServer{
	ArrayList<ObjectOutputStream> clientOutputStream;
	public static void main(String [] args){
		
		new MusicServer().go();
		
	}
	
	public class ClientHandler implements Runnable{
		
		ObjectInputStream in;
		Socket clientSocket;
		public ClientHandler(Socket socket){
			try{
				
				clientSocket=socket;
				in = new ObjectInputStream(clientSocket.getInputStream());
				
			}catch(Exception ex){ex.printStackTrace();}
			
			
		}
		
		public void run(){
			
			Object o2 =null;
			Object o1=null;
			try{
				
				while((o1=in.readObject())!=null){
					
					o2=in.readObject();
					System.out.println("read two objects");
					
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
		}
	
		
		
		
		
	}
	
	public void tellEveryone(Object one, Object two){
			
			Iterator it = clientOutputStream.iterator();
			while(it.hasNext()){
				try{
					ObjectOutputStream out =(ObjectOutputStream)it.next();
					out.writeObject(one);
					out.writeObject(two);
				}catch(Exception ex){
					
					ex.printStackTrace();
				}
				
			}
			
		}
		public void go(){
			
			clientOutputStream = new ArrayList<ObjectOutputStream>();
			try{
				ServerSocket serveSock =new ServerSocket(4242);
				while(true){
					Socket clientScoket =serveSock.accept();
					ObjectOutputStream out =new ObjectOutputStream(clientScoket.getOutputStream());
					clientOutputStream.add(out);
					Thread t = new Thread(new ClientHandler(clientScoket));
					t.start();
					
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
				
			}
			
			
		}
	
	
}