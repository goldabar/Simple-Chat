package zad1;


import javax.jms.JMSException;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import javax.naming.Context;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.naming.NamingException;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;


public class ChatClient extends JFrame implements MessageListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageConsumer msgCons = null;
	private MessageProducer msgProd = null;
	private String userName;
	private String topicName;
	private Connection connect = null;
	private Session sess = null;
	private String connFactory = "ConnectionFactory";
	private JTextArea txtArea = null;
	private JTextField txtInpField = null;
	
		
	
	
	public ChatClient(String userName, String topicName) {
		
		this.userName = userName;
		this.topicName = topicName;
		initConn(topicName);
		initUserInterface();
	}
	
	public String getClientName() {
		return userName;
	}

	public void setClientName(String userName) {
		this.userName = userName;
	}
	
	private void initUserInterface(){	

		txtArea = new JTextArea(40, 60);
		txtArea.setEditable(false);
		
		txtInpField = new JTextField(40);
		txtInpField.addActionListener(new AbstractAction(){
			

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public void actionPerformed(ActionEvent e){
		    	TextMessage txtMsg;
				try{
					
					txtMsg = sess.createTextMessage(userName+": " + txtInpField.getText() + "\n");
					
					msgProd.send(txtMsg);
					txtInpField.setText("");
					
				} 
				catch (JMSException e1){
					
					e1.printStackTrace();
				}
		    }
		});
		this.add(new JScrollPane(txtArea), BorderLayout.CENTER);
		this.add(txtInpField, BorderLayout.SOUTH);
		this.addWindowListener(new WindowAdapter()
		{
			
			public void windowClosing(WindowEvent e)
			{
				try{
					
					connect.close();
				}
				catch (JMSException jmse){
					
					System.out.println(jmse.getMessage());
				}
				
				dispose();
				System.exit(0);
				
			}
		});
		
		this.setTitle("---SimpleChat--- Klient: "+this.userName+" Temat: "+"'"+topicName+"'");
		
		pack();
		this.setVisible(true);
	}
	
	private void initConn(String topicName)
	{
		
		Context ctx = null;
		
		try{
			Properties connProp = new Properties();
			connProp.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
			
			connProp.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
			ctx = new InitialContext(connProp); 
			
			ConnectionFactory connFact = (ConnectionFactory) ctx.lookup(connFactory);
			Destination dest = (Destination) ctx.lookup(topicName);
			
			connect = connFact.createConnection();			
			sess = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			msgCons = sess.createConsumer(dest);
			msgCons.setMessageListener(this);
			
			connect.start();
			
			msgProd = sess.createProducer(dest);
			
		} 
		
		catch (NamingException e) {
			System.out.println("Błąd: "+e.getMessage());
			
			System.exit(1);
			
		}
		
		catch (JMSException jmse) {
			System.out.println("Błąd: "+jmse.getMessage());
			
			System.exit(2);
			
		}
					
		
	}

	@Override
	public void onMessage(Message msg) 
	{
		try{
			
			txtArea.append(((TextMessage) msg).getText());
		}
		catch (JMSException e){
			
			System.out.println(e.getMessage());
		}
	}

}