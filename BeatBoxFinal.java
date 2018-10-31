import javax.swing.*;
import javax.swing.event.*;
import javax.sound.midi.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class BeatBoxFinal{
	JFrame theFrame;
	JPanel mainPanel;
	JList incominglist;
	JTextField userMessage;
	ArrayList<JCheckBox> checkBoxList;
	int nextNum;
	Vector<String> ListVector=new Vector<String>();
	String userName;
	ObjectOutputStream out;
	ObjectInputStream in;
	HashMap<String,boolean[]> otherSeqsMap=new HashMap<String,boolean[]>();
	
	Sequencer sequencer;
	Sequence sequence;
	Sequence mySequence=null;
	Track track;
	String[] instrumentNames={"1","2","3","4","5","6","7","8","9","10","11","12","13",
	"14","15","16"};
	
	int[] instruments={35,42,46,38,49,39,50,60,70,72,64,56,56,47,67,63};
	
	
	public static void main (String[] args){
		String temp = "renyu";
		if(args.length>0){
			temp = args[0];
		}
		new BeatBoxFinal().startUp(temp);
		
	}
	
	public void startUp(String arg){

		String userName = arg;
		Boolean connected = false;
		try{
		Socket sock =new Socket("127.0.0.1",4242);
		connected=true;
		
		out = new ObjectOutputStream(sock.getOutputStream());
		in = new ObjectInputStream(sock.getInputStream());
		
		Thread remote =new Thread(new RemoteReader());
		remote.start();
		setUpMidi();
		buildGUI();
		}catch(UnknownHostException e){
				 connected = false;
			System.out.println("UnknownHostException "+e);
			
		}catch(Exception e){
				 connected = false;
				System.out.println("Exception "+e);
		}
		assert(connected):"未能连接"; 
		
	
	}
	
	
	public class RemoteReader implements Runnable{
		boolean[] checkBoxStatus =null;
		String nameToShow=null;
		Object obj=null;
		public void run(){
			try{
				
				while((obj=in.readObject())!=null)
				{
					String nameToShow =(String)obj;
					checkBoxStatus=(boolean[])in.readObject();
					otherSeqsMap.put(nameToShow,checkBoxStatus);
					ListVector.add(nameToShow);
					incominglist.setListData(ListVector);
					
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}

		
		
		
		
	}
	
	public void writeFile(){
		try{
		FileOutputStream fileStream =new FileOutputStream("MyGame.ser");
		ObjectOutputStream os= new ObjectOutputStream(fileStream);
		os.writeObject(ListVector);  //保存 ListVector 显示的内容的保存
		os.close();
		}catch(Exception e){}
		
	}
	public void readFile(){
		try{
		FileInputStream fileStream =new FileInputStream("MyGame.ser");
		ObjectInputStream oi= new ObjectInputStream(fileStream);
		ListVector=(Vector)oi.readObject();  //保存 ListVector 显示的内容的保存
		oi.close();
			}catch(Exception e){}
	}
	
			public void buildGUI(){
			theFrame = new JFrame("任大侠2 BeatBox");//边框
		
			theFrame.addWindowListener(new WindowAdapter(){
				
				public void windowOpened(WindowEvent e){
					System.out.println("windowOpened");
					//ListVector  list 显示的内容的保存
				    readFile();
					System.out.println("readFile");
					
					
				}
				
				public void windowClosing(WindowEvent e){
					System.out.println("dispose");
					theFrame.dispose();
				}
				public void windowClosed(WindowEvent e){
				
					writeFile();
				    System.out.println("writeFile");
					System.out.println("system exit");
					System.exit(0);
				}
			});
				
		
			
			BorderLayout layout = new BorderLayout();//border NWSE 
			JPanel background = new JPanel(layout);//嵌板 改变布局
			background.setBorder(BorderFactory.createEmptyBorder(10,200,300,400));//设置panel的边框的
			background.setBackground(Color.BLUE);
			
			checkBoxList= new ArrayList<JCheckBox>();
			
			Box buttonBox=	new Box(BoxLayout.Y_AXIS);//从上到下垂直
			
			JButton start = new JButton("Start");
			start.addActionListener(new MyStartListener());
			buttonBox.add(start);
			
			JButton stop = new JButton("Stop");
			stop.addActionListener(new MyStopListener());
			buttonBox.add(stop);
			
			JButton upTempo = new JButton("Tempo up");
			upTempo.addActionListener(new MyUpTempoListener());
			buttonBox.add(upTempo);
			
			JButton downTempo = new JButton("Tempo down");
			downTempo.addActionListener(new MyDownTempoListener());
			buttonBox.add(downTempo);
			
			JButton sendIt = new JButton("sendIt");
			sendIt.addActionListener(new MySendListener());
			buttonBox.add(sendIt);
			
			userMessage = new JTextField();
			buttonBox.add(userMessage);
			
			incominglist = new JList();
			incominglist.addListSelectionListener(new MyListSelectionListener());
			incominglist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//一次只能选择一项
			JScrollPane theList=new JScrollPane(incominglist);
			buttonBox.add(theList);
			incominglist.setListData(ListVector);// 设置list 显示的内容来源
			//box 设置完成 box 装了button message scrol 
			
			Box nameBox=new Box(BoxLayout.Y_AXIS);
			for(int i=0;i<16;i++){
				nameBox.add(new Label(instrumentNames[i]));
				
			}
				//box 设置完成 box 装了label
			background.add(BorderLayout.EAST,buttonBox);//东
			background.add(BorderLayout.WEST,nameBox);//西
			
			theFrame.getContentPane().add(background);//frame 大脚盆 pane 脚盆底  panel 盘子 box 盒子 button、label、scrol 水果
			GridLayout grid =new GridLayout(16,16);//网格布局
   			grid.setHgap(1);//设置水平间距
			grid.setVgap(2);//设置垂直间距
			mainPanel=new JPanel(grid);//先横着
			background.add(BorderLayout.CENTER,mainPanel);// box panel box 放在大盘子 panel 上
			
			for(int i =0;i<256;i++){
				JCheckBox c= new JCheckBox();
				c.setSelected(false);
				checkBoxList.add(c);
				mainPanel.add(c);
				
				
			}
			theFrame.setBounds(5,50,300,300);//x y 长 宽 ，xy 决定脚盆出来的位置
			theFrame.pack();//满足子布局 选择最合适大小
			theFrame.setVisible(true);
			
		}
		
		public void setUpMidi(){
			try{
				sequencer=MidiSystem.getSequencer();//播放器
				sequencer.open();//打开播放器
				sequence = new Sequence(Sequence.PPQ,4);//碟片
				track = sequence.createTrack();//碟片信息
				sequencer.setTempoInBPM(12); //1分钟120次
			}catch(Exception e){
				
				e.printStackTrace();
			}
			
		}
		
		
			public class MyStartListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
				System.out.println("sequancer start ");
				buildTrackAndStart();
				
			}
			
			
			
			
		}
			public class MyStopListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
			System.out.println("sequencer.stop");	
			sequencer.stop();//播放器关闭
				
			}
			
			
			
			
		}
			public class MyUpTempoListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
				float tempoFactor = sequencer.getTempoFactor();
				sequencer.setTempoFactor((float)(tempoFactor *1.03));//加快播放速度 减小播放速度
				
			}
			
		
		}
		
		public class MyDownTempoListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
				float tempoFactor = sequencer.getTempoFactor();
				sequencer.setTempoFactor((float)(tempoFactor *.97));
				
			}
			
			
			
			
		}
	
	
	  public class MySendListener implements ActionListener{
		  
		  public void actionPerformed(ActionEvent a){
			  boolean [] checkboxState =new boolean[256];
			  for(int i =0;i<256;i++){
				  JCheckBox check=(JCheckBox)checkBoxList.get(i);
				  if(check.isSelected()){
					  checkboxState[i]=true;
				  }
			  }
			  
			  String messageToSend = null;
			  try{
				  out.writeObject(userName+nextNum++ + ":"+userMessage.getText());
				  out.writeObject(checkboxState);
				  
			  }catch (Exception e){
                  System.out.println("Sorry");				  
				  
			  }
			  userMessage.setText("");
			  
		  }
	  }
		  public class MyListSelectionListener implements ListSelectionListener{
			  public void valueChanged(ListSelectionEvent le){
				  
				  if(!le.getValueIsAdjusting()){
					  
					  String selected = (String) incominglist.getSelectedValue();
					  if(selected!=null ){
						  
						  boolean[] selectedState=(boolean[])otherSeqsMap.get(selected);
						  changeSequence(selectedState);
						  sequencer.stop();
						  buildTrackAndStart();
						  
					  }
					  
				  }
				  
				  
			  }
			  
			  
			  
		  }
		  
		  public void changeSequence(boolean [] checkboxState ){
			  for (int i=0;i<256;i++){
				  JCheckBox check=(JCheckBox) checkBoxList.get(i);
				  if(checkboxState[i]){
					  check.setSelected(true);
				  }else{
					  check.setSelected(false);
				  }
			  }
			  
		  }
		  
		  
		  public void buildTrackAndStart(){
			  
			  ArrayList<Integer> trackList =null;
			  sequence.deleteTrack(track);//磁带清空
			  track = sequence.createTrack();
			  for(int i = 0;i<16;i++){
				  
				  trackList = new ArrayList<Integer>();
				  for(int j=0;j<16;j++){
					   JCheckBox jc=(JCheckBox)checkBoxList.get(j+(16*i));
					   if(jc.isSelected()){
						   
						   int key=instruments[i];
						   trackList.add(new Integer(key));//记录下哪些checkbox被点击
					   }else{
						   trackList.add(null);
					   }
					  
				  }
				  makeTracks(trackList);//一次处理一行
			  }
			  track.add(makeEvent(192,9,1,0,15));
			  try{
				  sequencer.setSequence(sequence);
				  sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
				  sequencer.start();
				  sequencer.setTempoInBPM(120);
			  }catch(Exception e){
				  e.printStackTrace();
			  }
		  }
		  
		  public void makeTracks(ArrayList list){
			  Iterator it = list.iterator();
			  for(int i =0;i<16;i++){
				  Integer num = (Integer)it.next();
				  if(num !=null){
					  int numKey = num.intValue();
					  track.add(makeEvent(144,9,numKey,100,i));
					  track.add(makeEvent(128,9,numKey,100,i+1));
				  }
				  
			  }
		  }
		  
		  public MidiEvent makeEvent(int comd,int chan,int one, int two,int tick){
			  
			  MidiEvent event =null;
			  try{
				  ShortMessage a =new ShortMessage();
				  a.setMessage(comd,chan,one,two);
				  event = new MidiEvent(a,tick);//shortmessage 转化为midi信息
			  }catch(Exception e){
				 
			  }
			 return event;
			  
		  }
		  
		  
}
	  
	  
	  
