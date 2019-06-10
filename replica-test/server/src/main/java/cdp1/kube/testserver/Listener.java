package cdp1.kube.testserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

class Listener implements Runnable {

	private final int mPort;
	private final int mThreadCount;
	private final ConcurrentLinkedQueue<Socket> mSocketQ = new ConcurrentLinkedQueue<Socket>();

	public Listener(int port, int threadCount) {
		mPort = port;
		mThreadCount = threadCount;
	}

	@Override
	public void run() {
		Thread[] workers = new Thread[mThreadCount];
		
		System.out.println("Server thread count: " + Integer.toString(mThreadCount));
		
		// Create workers
		for (int i = 0; i < mThreadCount; i++) {
			Thread t = new Thread(new ServerWorker(mSocketQ));
			t.setName("cdpk-server-worker-" + Integer.toString(i));
			t.setDaemon(true);
			t.start();
			workers[i] = t;
		}
		
		try (ServerSocket serverSocket = new ServerSocket(mPort)) {
			System.out.println("Server is listening on port " + mPort);

			while (true) {
				Socket socket = serverSocket.accept();
				mSocketQ.add(socket);
				System.out.println("New client connected");
			}

		} catch (Exception e) {
			System.err.println("Server exception: " + e.getMessage());
			System.err.flush();
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 0, threadCount = 1;

		System.out.println("Starting server...");

		// Get port number
		try {
			port = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Please provide the port number as a parameter!");
			System.err.flush();
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err.println("The port number is invalid!");
			System.err.flush();
			System.exit(1);
		}
		
		// Get thread count
		try {
			threadCount = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			;
		} catch (NumberFormatException e) {
			System.err.println("The thread count is invalid!");
			System.err.flush();
			System.exit(1);
		}

		Listener listener = new Listener(port, threadCount);
		listener.run();

		System.out.println("Closing server...");
	}

}
