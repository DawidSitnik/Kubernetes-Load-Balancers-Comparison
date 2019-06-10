package cdp1.kube.testserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

class ServerWorker implements Runnable {

	private final ConcurrentLinkedQueue<Socket> mSocketQ;

	public ServerWorker(ConcurrentLinkedQueue<Socket> socketQ) {
		this.mSocketQ = socketQ;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket polled = mSocketQ.poll();
				if (polled == null) {
					// System.out.println(Thread.currentThread().getName());
					Thread.sleep(20);
					continue;
				}

				try (Socket socket = polled) {
					InputStream input = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));

					OutputStream output = socket.getOutputStream();
					PrintWriter writer = new PrintWriter(output, true);

					String text;
					int n;
					long prime;

					text = reader.readLine();
					n = Integer.parseInt(text);
					prime = new ServerLogic(n).getNthPrimeNumber();
					writer.println(Long.toString(prime));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
