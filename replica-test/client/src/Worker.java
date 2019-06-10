import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {

	static final ConcurrentLinkedQueue<Long> resultQ = new ConcurrentLinkedQueue<>();

	private final ConcurrentLinkedQueue<Integer> taskQ;
	private final String hostname;
	private final int port;

	public Worker(ConcurrentLinkedQueue<Integer> taskQ, String hostname, int port) {
		this.taskQ = taskQ;
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public void run() {
		while (true) {
			Integer n = taskQ.poll();
			if (n == null) {
				break;
			}

			try (Socket socket = new Socket(hostname, port)) {
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);

				writer.println(n.toString());

				long startTime = System.currentTimeMillis();
				String response = reader.readLine();
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				System.out.printf("n = %4d, response = %5s, time = %4d\n", n, response, elapsedTime);
				
				resultQ.add(Long.valueOf(elapsedTime));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
