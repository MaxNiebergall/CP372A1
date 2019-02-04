import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SBoard {
	public static String defaultColour;
	public static int boardWidth, boardHeight, portNumber = 8000;
	public static ArrayList<String> colours = new ArrayList<String>();
	public static List<Note> notes = Collections.synchronizedList(new ArrayList<Note>());

	public static void main(String[] args) throws Exception {
		try {
			portNumber = Integer.parseInt(args[0]);
			boardWidth = Integer.parseInt(args[1]);
			boardHeight = Integer.parseInt(args[2]);
			defaultColour = args[3];
			for (int i = 3; i < args.length; i++) {
				colours.add(args[i].toLowerCase());
			}
		} catch (Exception e) {
			System.out.println("Bad values in command line");
			System.exit(0);
		}

		ServerSocket listener = new ServerSocket(portNumber);
		try {
			while (true) {
				new Client(listener.accept(), boardWidth, boardHeight, colours).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class Client extends Thread {
		private Socket socket;

		public Client(Socket socket, int w, int h, List<String> colours) {
			this.socket = socket;
			System.out.println("HELLO");
		}

		@Override
		public void run() {
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				out.println(colours.toString());
				// for (int i = 0; i < colours.size(); i++) {
				// out.println(colours.get(i).toString() + "\n");
				// }

				while (true) {
					String input = in.readLine();
					if (input == null || input.equals("."))
						break;
					String message = null;
					try {
						message = requestType(input);
						System.out.println(message);
					} catch (Exception e) {
						// some
					}
					out.println(message);
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

		private String requestType(String input) {
			if (input != null) {
				String[] request = input.split(" ");
				String typeofreq = request[0];
				if (typeofreq != null) {
					String a = typeofreq.toUpperCase();
					if (a.equals("GET")) {

					} else if (a.equals("POST")) {
						postNote(input);
					} else if (a.equals("PIN")) {
						pinLocation(input, 0);
					} else if (a.equals("UNPIN")) {
						pinLocation(input, 1);
					} else if (a.equals("CLEAR")) {

					} else if (a.equals("DISCONNECT")) {

					}
				} else {
					// throw error
				}
			} else {
				// throw error
			}
			return "hello";
		}

		private void postNote(String input) {
			String message = "", colour = "";
			int[] xyCoords = parseXY(input);
			int[] wh = parseWH(input);
			int x = xyCoords[0], y = xyCoords[1];
			int w = wh[0], h = wh[1];
			// parse the string and set it to each variable

			int index = input.indexOf("color=") + "color=".length();
			if (index == -1) {
				colour = colours.get(0);
			} else {
				while (input.charAt(index) != ',') {
					colour += input.charAt(index);
					index++;
				}
			}
			System.out.println();
			Note newNote = new Note(x, y, w, h, colour, message);
			String[] messages = input.split("refersTo=");
			message = messages[1];

			log("New Note");
			synchronized (notes) {
				notes.add(newNote);
			}
			log(notes.get(notes.size() - 1).toString());

		}

		private void pinLocation(String input, int type) {
			// parse the input
			int[] xyCoords = parseXY(input);
			int x = xyCoords[0], y = xyCoords[1];
			synchronized (notes) {
				for (Note note : notes) {
					if (x >= note.getXCoord() && (x <= note.getWidth() + note.getXCoord()) && y <= note.getYCoord()
							&& (y >= (note.getYCoord() - note.getHeight()))) {
						if (type == 0) {
							note.setPinStatus(true);
						} else {
							note.setPinStatus(false);
						}
					}
				}
			}

		}

		private void clear() {

		}

		private void disconnect() throws Exception {
			try {
				socket.close();
			} finally {
				System.out.println("Error closing connection");
			}
		}

		private int[] parseXY(String input) {
			int x = 0, y = 0;
			int index = input.indexOf("xCoord=") + "xCoord=".length();
			while (input.charAt(index) != ',') {
				x = 10 * x + input.charAt(index) - '0';
				index++;
			}
			index = input.indexOf("yCoord=") + "yCoord=".length();
			while (input.charAt(index) != ',') {
				y = 10 * y + input.charAt(index) - '0';
				index++;
			}
			int[] xyCoords = { x, y };
			return xyCoords;
		}

		private int[] parseWH(String input) {
			int w = 0, h = 0;
			int index = input.indexOf("width=") + "width=".length();
			while (input.charAt(index) != ',') {
				w = 10 * w + input.charAt(index) - '0';
				index++;
			}
			index = input.indexOf("height=") + "height=".length();
			while (input.charAt(index) != ',') {
				h = 10 * h + input.charAt(index) - '0';
				index++;
			}
			int[] wh = { w, h };
			return wh;
		}

	}

	public static class Note {
		private int xcoord, ycoord, width, height;
		private String colour, refersTo;
		private boolean isPinned = false;

		public Note(int xcoord, int ycoord, int width, int height, String colour, String refersTo) {
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.width = width;
			this.height = height;
			this.colour = colour;
			this.refersTo = refersTo;
		}

		public void setPinStatus(boolean value) {
			this.isPinned = value;
		}

		public int getXCoord() {
			return this.xcoord;
		}

		public int getYCoord() {
			return this.ycoord;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public String getColour() {
			return this.colour;
		}
	}

}
