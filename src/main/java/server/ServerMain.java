package server;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.joda.time.DateTime;

public class ServerMain {
	private static Appender fh;
	private static final Logger log = Logger.getLogger(ServerMain.class);
	private static String logFilePath = "/var/log/server/MyLogFile.log";

	public static void main(String[] args) {
		int port = 9000;
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			fh = new FileAppender(new SimpleLayout(), logFilePath);
			log.addAppender(fh);
			fh.setLayout(new SimpleLayout());
			log.info(DateTime.now() + " - Servidor escutando na porta : " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				log.info(DateTime.now() + " - Novo Cliente conectado");
				try {
					new ServerThread(socket).start();
				} catch (Exception e) {
					log.error(DateTime.now() + " - " + e);
				}
			}

		} catch (Exception ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
