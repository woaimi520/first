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
	String[] instrumentNames={"1","2","3","4","5","11","12","13",
	"14","15","21","22","23","24","25",};
	
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
		assert(connected):"Î´ÄÜÁ¬½Ó"; 
		
	
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
	
			public void buildGUI(){
			theFrame = new JFrame("Cyber BeatBox");
			BorderLayout layout = new BorderLayout();
			JPanel background = new JPanel(layout);
			background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			
			checkBoxList= new ArrayList<JCheckBox>();
			
			Box buttonBox=	new Box(BoxLayout.Y_AXIS);
			JButton start = new JButton("Start");
			start.addActionListener(new MyStartListener());
			buttonBox.add(start);
			
			JButton stop = new JButton("Stop");
			start.addActionListener(new MyStopListener());
			buttonBox.add(stop);
			
			JButton upTempo = new JButton("Tempo up");
			start.addActionListener(new MyUpTempoListener());
			buttonBox.add(upTempo);
			
			JButton downTempo = new JButton("Tempo down");
			start.addActionListener(new MyDownTempoListener());
			buttonBox.add(downTempo);
			
			JButton sendIt = new JButton("sendIt");
			start.addActionListener(new MySendListener());
			buttonBox.add(sendIt);
			
			userMessage = new JTextField();
			buttonBox.add(userMessage);
			
			incominglist = new JList();
			incominglist.addListSelectionListener(new MyListSelectionListener());
			incominglist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane theList=new JScrollPane(incominglist);
			buttonBox.add(theList);
			incominglist.setListData(ListVector);
			
			Box nameBox=new Box(BoxLayout.Y_AXIS);
			for(int i=0;i<16;i++){
				nameBox.add(new Label(instrumentNames[i]));
				
			}
			
			background.add(BorderLayout.EAST,buttonBox);
			background.add(BorderLayout.WEST,nameBox);
			
			theFrame.getContentPane().add(background);
			GridLayout grid =new GridLayout(16,16);
   			grid.setVgap(1);
			grid.setVgap(2);
			mainPanel=new JPanel(grid);
			background.add(BorderLayout.CENTER,mainPanel);
			
			for(int i =0;i<256;i++){
				JCheckBox c= new JCheckBox();
				c.setSelected(false);
				checkBoxList.add(c);
				mainPanel.add(c);
				
				
			}
			theFrame.setBounds(50,50,300,300);
			theFrame.pack();
			theFrame.setVisible(true);
			
		}
		
		public void setUpMidi(){
			try{
				sequencer=MidiSystem.getSequencer();
				sequencer.open();
				sequence = new Sequence(Sequence.PPQ,4);
				track = sequence.createTrack();
				sequencer.setTempoInBPM(120); //??
			}catch(Exception e){
				
				e.printStackTrace();
			}
			
		}
		
		
			public class MyStartListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
				buildTrackAndStart();
				
			}
			
			
			
			
		}
			public class MyStopListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
			sequencer.stop();
				
			}
			
			
			
			
		}
			public class MyUpTempoListener implements ActionListener{
			
			public void actionPerformed(ActionEvent a){
 				
				float tempoFactor = sequencer.getTempoFactor();
				sequencer.setTempoFactor((float)(tempoFactor *1.03));
				
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
			  sequence.deleteTrack(track);
			  track = sequence.createTrack();
			  for(int i = 0;i<16;i++){
				  
				  trackList = new ArrayList<Integer>();
				  for(int j=0;j<16;j++){
					   JCheckBox jc=(JCheckBox)checkBoxList.get(j+(16*i));
					   if(jc.isSelected()){
						   
						   int key=instruments[i];
						   trackList.add(new Integer(key));
					   }else{
						   trackList.add(null);
					   }
					  
				  }
				  makeTracks(trackList);
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
				  event = new MidiEvent(a,tick);
			  }catch(Exception e){
				 
			  }
			 return event;
			  
		  }
		  
		  
}
	  
	  
	  
