package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import model.Requisicao;
import model.Usuario;

public class ServerThread extends Thread {
	private Socket socket;
	private Appender fh;
	private OutputStream output;
	private EntityManagerFactory emf;
	private EntityManager em;

	private final Logger log = Logger.getLogger(ServerThread.class);
	private static String logFilePath = "/var/log/server/MyLogFile.log";
	private static byte[] ack = new byte[] { 0x0A, 0x05, (byte) 0xA0, 0x28, 0x0D };

	public ServerThread(Socket socket) throws IOException {
		this.socket = socket;
		fh = new FileAppender(new SimpleLayout(), logFilePath);
		log.addAppender(fh);
		fh.setLayout(new SimpleLayout());
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			output = socket.getOutputStream();
			byte[] packet = new byte[1024];
			input.read(packet); //lê os bytes da request para dentro do array
			byte[] msg = new byte[packet[1]]; //aloca apenas os bytes da mensagem e ignora o resto de sujeira no array packet
			System.arraycopy(packet, 0, msg, 0, packet[1]);
			byte[] payload = Arrays.copyOfRange(msg, 3, msg.length - 2);
			byte[] c = Arrays.copyOfRange(msg, 1, msg.length - 2);
			Check(c);
			
			log.info(DateTime.now() + " : Nova requisição recebida");
			String mensagem2log = ""; 
			for(byte ax : msg) {
				mensagem2log += " "+ Integer.toHexString(ax);
			}
			log.info(DateTime.now() + "Mensagem: " + mensagem2log);				
			
			switch (msg[2]) {
			case (byte) 0xA1:
				TextMsg(payload);
				break;
			case (byte) 0xA2:
				UserInformation(payload);
				break;
			case (byte) 0xA3:
				DateTimeRequest(payload);
				break;
			default:
				break;
			}
			socket.close();
			input.close();
			output.close();
		} catch (IOException ex) {
			log.error(ex);
		}
	}

	private void TextMsg(byte[] payload) throws IOException {
		String msg = new String(payload);
		Requisicao req = new Requisicao();
		req.setDataRecebimento(DateTime.now().getMillis());
		req.setMensagem(msg);

		// persiste o objeto no banco de dados
		em.getTransaction().begin();
		em.persist(req);
		em.getTransaction().commit();
		output.write(ack);
	}

	private void UserInformation(byte[] payload) throws IOException {
		int idade = payload[0], peso = payload[1], altura = payload[2];

		byte[] nameBytes = Arrays.copyOfRange(payload, 4, payload[3]);
		String nome = new String(nameBytes);

		Usuario usuario = new Usuario();
		usuario.setAltura(altura);
		usuario.setIdade(idade);
		usuario.setNome(nome);
		usuario.setPeso(peso);

		// persiste o objeto no banco de dados
		em.getTransaction().begin();
		em.persist(usuario);
		em.getTransaction().commit();
		output.write(ack);
	}

	private void DateTimeRequest(byte[] payload) throws IOException {
		String msg = new String(payload);
		DateTime dt = new DateTime();
		dt.withZone(DateTimeZone.forID(msg));

		byte crc = Check(new byte[] { 0x0B, (byte) 0xA3, (byte) dt.getDayOfMonth(), (byte) dt.getMonthOfYear(),
				(byte) dt.getYearOfCentury(), (byte) dt.getHourOfDay(), (byte) dt.getMinuteOfHour(),
				(byte) dt.getSecondOfMinute() });

		byte[] resp = new byte[] { 0x0A, 0x0B, (byte) 0xA3, (byte) dt.getDayOfMonth(), (byte) dt.getMonthOfYear(),
				(byte) dt.getYearOfCentury(), (byte) dt.getHourOfDay(), (byte) dt.getMinuteOfHour(),
				(byte) dt.getSecondOfMinute(), crc, 0x0D };
		output.write(resp);
	}

	private static byte Check(byte[] c) {
		AlgoParams Crc8 = new AlgoParams("CRC-8", 8, 0x7, 0x0, false, false, 0x0, 0xF4);
		CrcCalculator calculator = new CrcCalculator(Crc8);
		long result = calculator.Calc(c, 0, c.length);
		return (byte) result;
	}
}