import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SBoard {
	public static String defaultcolor;
	public static int boardWidth, boardHeight, portNumber = 8000;
	public static ArrayList<String> colors = new ArrayList<String>();
	public static List<Note> notes = Collections.synchronizedList(new ArrayList<Note>());

	public static void main(String[] args) throws Exception {
		try {
			portNumber = Integer.parseInt(args[0]);
			boardWidth = Integer.parseInt(args[1]);
			boardHeight = Integer.parseInt(args[2]);
			defaultcolor = args[3];
			for (int i = 3; i < args.length; i++) {
				colors.add(args[i].toLowerCase());
			}
		} catch (Exception e) {
			System.out.println("Bad values in command line");
			System.exit(0);
		}

		ServerSocket listener = new ServerSocket(portNumber);
		try {
			while (true) {
				new Client(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class Client extends Thread {
		private Socket socket;

		public Client(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				String colors_out = "";
				for (int i = 0; i < colors.size(); i++) {
					colors_out += colors.get(i) + " ";
				}
				out.println(colors_out);

				while (true) {
					String input = in.readLine();
					if (input == null || input.equals("."))
						break;
					String message = null;
					try {
						message = requestType(input);
						int numLines = 0;
						for (int i = 0; i < message.length(); i++) {
							if (message.charAt(i) == '\n') {
								numLines++;
							}
						}
						out.println(numLines);
						out.println(message);
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

		private String requestType(String input) throws Exception {
			String output = "";
			if (input != null) {

				if (input.startsWith("GET")) {
					log("GET");
					List<Note> results = searchNotes(input);

					for (int i = 0; i < results.size(); i++) {
						output += "" + results.get(i).toString() + "\n";
					}
					log(output);
				} else if (input.startsWith("POST")) {
					log("POST");
					postNote(input);
				} else if (input.startsWith("PIN")) {
					log("PIN");
					pinLocation(input, 0);
				} else if (input.startsWith("UNPIN")) {
					log("UNPIN");
					pinLocation(input, 1);
				} else if (input.startsWith("CLEAR")) {
					log("CLEAR");
					clear();
				} else if (input.startsWith("DISCONNECT")) {
					log("DISCONNECT");
					disconnect();
				} else {
					log("NO INPUT TYPE");
					// throw error
				}
			} else {
				// throw error
			}
			return output;
		}

		private void postNote(String input) {
			String message = "", color = "";
			int[] xyCoords = parseXY(input);
			int[] wh = parseWH(input);
			int x = xyCoords[0], y = xyCoords[1];
			int w = wh[0], h = wh[1];
			// parse the string and set it to each variable
//			System.out.println(input);
			int index = input.indexOf("color=");
//			System.out.println(index);
			if (index == -1) {
				color = colors.get(0);
			} else {
				index += "color=".length();
				while (input.charAt(index) != ',') {
					color += input.charAt(index);
					index++;
				}
			}
//			System.out.println(x);
//			System.out.println(y);
//			System.out.println(w);
//			System.out.println(h);
			System.out.println("color: " + color);

			String[] messages = input.split("refersTo=");
			message = messages[1];
			Note newNote = new Note(x, y, w, h, color, message);
//			System.out.println(message);
			log("New Note");
			synchronized (notes) {
				notes.add(newNote);
			}
			log(notes.get(notes.size() - 1).toString());
//			for (Note note : notes) {
//				System.out.println("POSTING " + note.toString());
//			}

		}

		private void pinLocation(String input, int type) {
			// parse the input
			int[] xyCoords = parseXY(input);
			int x = xyCoords[0], y = xyCoords[1];

			synchronized (notes) {
				for (Note note : notes) {
					if (note.getXCoord() <= x && (x <= note.getXCoord() + note.getWidth())) {

						if (y >= note.getYCoord() && (y <= note.getYCoord() + note.getHeight())) {
							if (type == 0) {

								note.setPinStatus(true);
							} else {
								note.setPinStatus(false);
							}
						}
					}
				}
			}
			for (Note note : notes) {
				System.out.println("THINGY " + note.toString());
			}

		}

		private void clear() {
			synchronized (notes) {
				for (Note note : notes) {
					if (note.getStatus()) {
						note.setPinStatus(false);
					}
				}
			}
			for (Note note : notes) {
				System.out.println("THINGY " + note.toString());
			}
		}

		private void disconnect() throws Exception {
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println("Error closing connection");
			}
		}

		private int[] parseXY(String input) {
			int x = 0, y = 0;
			if (input.contains("xCoord=")) {
				int index = input.indexOf("xCoord=") + "xCoord=".length();
				while (input.charAt(index) != ',') {
					x = 10 * x + input.charAt(index) - '0';
					index++;
				}
			} else {
				x = -1;
			}

			if (input.contains("yCoord=")) {
				int index = input.indexOf("yCoord=") + "yCoord=".length();
				while (input.charAt(index) != ',') {
					y = 10 * y + input.charAt(index) - '0';
					index++;
				}
			} else {
				y = -1;
			}
			int[] xyCoords = { x, y };
			return xyCoords;
		}

		private int[] parseWH(String input) {
			int w = 0, h = 0;
			if (input.contains("width=")) {
				int index = input.indexOf("width=") + "width=".length();
				while (input.charAt(index) != ',') {
					w = 10 * w + input.charAt(index) - '0';
					index++;
				}
			} else {
				w = -1;
			}
			if (input.contains("height")) {
				int index = input.indexOf("height=") + "height=".length();
				while (input.charAt(index) != ',') {
					h = 10 * h + input.charAt(index) - '0';
					index++;
				}
			} else {
				h = -1;
			}
			int[] wh = { w, h };
			return wh;
		}

		List<Note> searchNotes(String input) {
			LinkedList<Note> results = new LinkedList<Note>();

			String message = "";
			int[] xyCoords = parseXY(input);
			int[] wh = parseWH(input);
			int x = xyCoords[0], y = xyCoords[1];
			int w = wh[0], h = wh[1];
			if (input.contains("refersTo=")) {
				String[] messages = input.split("refersTo=");
				message = messages[1];
			}
			String color = "";
			int index = input.indexOf("color=");
			if (index == -1) {
				color = colors.get(0);
			} else {
				index += "color=".length();
				while (input.charAt(index) != ',') {
					color += input.charAt(index);
					index++;
				}
			}

			if (color.length() > 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).color.equals(color) && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}

			if (x >= 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).xcoord == x && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}
			if (y >= 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).ycoord == y && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}
			if (w >= 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).width == w && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}
			if (h >= 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).height == h && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}
			if (message.length() > 0) {
				for (int i = 0; i < notes.size(); i++) {
					if (notes.get(i).refersTo.contains(message) && !results.contains(notes.get(i))) {
						results.add(notes.get(i));
					}
				}
			}

			return results;

		}

	}

	public static class Note {
		private int xcoord, ycoord, width, height;
		private String color, refersTo;
		private boolean isPinned = false;

		public Note(int xcoord, int ycoord, int width, int height, String color, String refersTo) {
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.width = width;
			this.height = height;
			this.color = color;
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

		public String getcolor() {
			return this.color;
		}

		public boolean getStatus() {
			return this.isPinned;
		}

		@Override
		public String toString() {
			return "xCoord=" + this.xcoord + ", yCoord=" + this.ycoord + ", width=" + this.width + ", height="
					+ this.height + ", color=" + this.color + ", refersTo=" + this.refersTo + ", isPinned="
					+ this.isPinned;
		}
	}

}
