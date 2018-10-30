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
			System.out.println("�߳�������");
			try{
				System.out.println("�߳̽���try");
				// �����һֱ�ȴ� �߳���socket�Ͽ���ʱ��Ż����
				while((o1=in.readObject())!=null){
					System.out.println("�߳̽���ѭ����");
					o2=in.readObject();
					System.out.println("read two objects");
					tellEveryone(o1,o2);
				}
				System.out.println("�߳��˳�ѭ����");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			System.out.println("�߳̽���");
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
					System.out.println("���߳̽���ѭ����");
					Socket clientScoket =serveSock.accept();//����Ҳ������
					System.out.println("���߳̽���ѭ���� accept");
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