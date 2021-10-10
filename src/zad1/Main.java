package zad1;

import javax.swing.JOptionPane;
import java.net.MalformedURLException;
import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import javax.swing.SwingUtilities;
import javax.jms.JMSException;


public class Main {
	
	private static boolean msg_queue=false;
	private static String lh_url="tcp://localhost:3035";
	private static String users_topic="Default_topic";
	
	
	public static void main(String[] args){
		
		chatInformations();
		connectInit();
		showChatClients();

	}
		
	private static void chatInformations(){
		
		String introMsg = "<html><center><h2>SimpleChat</h2></center><br>" + 
	"<center><i>Przejdź dalej by podać temat</i></center></html><br><br>";
		
		JOptionPane.showMessageDialog(null, introMsg);
		
		String input = (String) JOptionPane.showInputDialog("Proszę podać temat: ", users_topic);
		
		if (input!=null) {
			users_topic = input;
		}
		
		input = (String) JOptionPane.showInputDialog("Proszę podać adres URL:", lh_url);
		
		lh_url = input;
		
	}
	
	private static void showChatClients(){
		
		SwingUtilities.invokeLater(() -> new ChatClient("użytkownik 1", users_topic));
		SwingUtilities.invokeLater(() -> new ChatClient("użytkownik 2", users_topic));
		
	}
	
	private static void connectInit(){
		
		try {
			
			JmsAdminServerIfc servAdmin = AdminConnectionFactory.create(lh_url);
			
			if (servAdmin.destinationExists(users_topic)){
				
				System.out.println("użyty temat: "+users_topic);
			}
			else{
				
				if (!servAdmin.addDestination(users_topic, msg_queue)){
					
						System.out.println("Błąd przy tworzeniu tematu: "+users_topic+" na serwerze: " + lh_url);
				}
				else{	
					
						System.out.println("Dodano nowy temat: "+users_topic);
				}
			}
		}
		
		catch (MalformedURLException | JMSException e) {
			
			System.out.println(e.getMessage());
			
			System.exit(1);
		}
	}

}