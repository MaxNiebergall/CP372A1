import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

public class SBoard {
	public static String defaultColour;
	public static int boardWidth, boardHeight, portNumber;
	public static ArrayList<String> colours = new ArrayList<String>();
	public static ArrayList<Note> notes = new ArrayList<Note>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		client.GUI gui = new client.GUI();
		portNumber = Integer.parseInt(args[0]);
		boardWidth = Integer.parseInt(args[1]);
		gui.verifyXCoord(boardWidth);
		boardHeight = Integer.parseInt(args[2]);
		gui.verifyYCoord(boardHeight);
		defaultColour = args[3];
		for (int i = 3; i < args.length; i++) {
			colours.add(args[i]);
		}
		gui.setDefaultColor(defaultColour);
		ServerSocket listener = new ServerSocket(portNumber);
		try {
			while (true) {
				new Client(listener.accept(), boardWidth, boardHeight, colours, gui).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class Client extends Thread {
		private Socket socket;
		private client.GUI gui;

		public Client(Socket socket, int w, int h, ArrayList<String> colours, client.GUI gui) {
			this.socket = socket;
			this.gui = gui;
			this.gui.resultArea.append("Client successful connection. \nWidth: " + Integer.toString(w) + " Height: "
					+ Integer.toString(h) + "\n");
			this.gui.resultArea.append("The colours are: \n");
			for (String colour : colours) {
				this.gui.resultArea.append(colour + "\n");
			}
		}

		@Override
		public void run() {
			try {

				// Decorate the streams so we can send characters
				// and not just bytes. Ensure output is flushed
				// after every newline.

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				// Send a welcome message to the client.
				// out.println("Hello, you are client #" + clientNumber + ".");
				// out.println("Enter a line with only a period to quit\n");

				// Get messages from the client, line by line; return them
				// capitalized
				while (true) {
					String input = in.readLine();

				}
			} catch (IOException e) {
				// log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				// log("Connection with client# " + clientNumber + " closed");
			}
		}

		private void log(String message) {
			System.out.println(message); // server side responses
		}
	}

	public class Note implements Comparable {
		int xcoord, ycoord, width, height;
		String colour, refersTo;
		boolean isPinned;

		public Note(int xcoord, int ycoord, int width, int height, String colour, boolean isPinned, String refersTo) {
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.width = width;
			this.height = height;
			if (colour.equals(null)) {
				this.colour = defaultColour;
			}
		}

		@Override
		// default compare notes by string
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public class locationComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			Note note1 = (Note) o1;
			Note note2 = (Note) o2;
			// note1 comes before note2
			return (note1.refersTo.toLowerCase().compareTo(note2.refersTo.toLowerCase()));
		}

	}

	public class colourComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			Note note1 = (Note) o1;
			Note note2 = (Note) o2;
			return (note1.colour.toLowerCase().compareTo(note2.colour.toLowerCase()));
		}

	}

}
