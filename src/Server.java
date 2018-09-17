import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

	public static final int registryPort = 3000;
	
	public static void main(String[] args) {
		try{
			TTTClass ttt = new TTTClass();
			
//			final Registry reg = LocateRegistry.createRegistry(registryPort);
//			reg.rebind("rmi://localhost:"+registryPort+"/tictactoe",ttt);

			LocateRegistry.createRegistry(registryPort);
			Naming.rebind("rmi://localhost:"+registryPort+"/tictactoe",ttt);

			System.err.println("Server up");
			
	        System.in.read();
			
		}
		catch (IOException e){ e.printStackTrace();	} 
	}

}
