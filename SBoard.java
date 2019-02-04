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

	//TODO dont through exception here - handle it
	public static void main(String[] args) throws Exception {

		//client.GUI gui = new client.GUI();
		portNumber = Integer.parseInt(args[0]);
		boardWidth = Integer.parseInt(args[1]);
		//gui.verifyXCoord(boardWidth);
		boardHeight = Integer.parseInt(args[2]);
		//gui.verifyYCoord(boardHeight);
		defaultColour = args[3];
		for (int i = 3; i < args.length; i++) {
			colours.add(args[i]);
		}
		//gui.setDefaultColor(defaultColour);
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
		//private client.GUI gui;

		public Client(Socket socket) {
			this.socket = socket;

//			this.gui.resultArea.append("Client successful connection. \nWidth: " + Integer.toString(w) + " Height: "
//					+ Integer.toString(h) + "\n");
//			this.gui.resultArea.append("The colours are: \n");
//			for (String colour : colours) {
//				this.gui.resultArea.append(colour + "\n");
//			}
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
					// from client "POST xCoord=" + x + ", yCoord=" + y + ", width=" + w + ", height=" + h + ", refersTo=" + message

					if(input.startsWith("POST")){
						int x=0, y=0, w=0, h=0;
						String message="";
						int index = input.indexOf("xCoord=") + "xCoord=".length();
						while(input.charAt(index) != ','){
							x = 10*x + input.charAt(index)-'0';
							index++;
						}
						index = input.indexOf("yCoord=") + "yCoord=".length();
						while(input.charAt(index) != ','){
							y = 10*y + input.charAt(index)-'0';
							index++;
						}
						index = input.indexOf("width=") + "width=".length();
						while(input.charAt(index) != ','){
							w = 10*w + input.charAt(index)-'0';
							index++;
						}
						index = input.indexOf("height=") + "height=".length();
						while(input.charAt(index) != ','){
							h = 10*h + input.charAt(index)-'0';
							index++;
						}
						
						String[] messages = input.split("refersTo=");
						message=messages[1];

						log("New Note");
						notes.add(new Note(x,y,w,h, null, false, message));
						log(notes.get(notes.size()-1).toString());
					}
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

	public static class Note implements Comparable {
		int xcoord, ycoord, width, height;
		String colour, refersTo;
		boolean isPinned;

		public Note(int xcoord, int ycoord, int width, int height, String colour, boolean isPinned, String refersTo) {
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.width = width;
			this.height = height;
			if (colour == null) {
				this.colour = defaultColour;
			}
		}

		@Override
		// default compare notes by string
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public String toString(){
			return "" + this.ycoord + this.xcoord + this.width + this.height + this.colour;
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
