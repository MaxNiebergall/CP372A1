import java.awt.*;
import java.awt.geom.Point2D;
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

public class Server {
	public static String defaultcolor;
	public static int boardWidth, boardHeight, portNumber = 8000;
	public static ArrayList<String> colors = new ArrayList<String>();
	public static List<Note> notes = Collections.synchronizedList(new ArrayList<Note>());
	public static List<MyPoint> pins = Collections.synchronizedList(new ArrayList<MyPoint>());

	public static void main(String[] args) {
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
		try {
			ServerSocket listener = new ServerSocket(portNumber);

			try {
				while (true) {
					new Client(listener.accept()).start();
				}
			} finally {
				listener.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class MyPoint extends Point{
		MyPoint(int x, int y){
			super(x,y);
		}

		public String toString(){
			return " x: " + (int) this.getX() + " y: " + (int) this.getY();
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

						System.out.println("THIS IS THE MESSAGE:" + message);
//						out.println(numLines);

						if (message.length() > 0) {
							out.println(message);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
//					out.println(message);
				}
			} catch (IOException e) {
				// log("Error handling Client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				// log("Connection with Client# " + clientNumber + " closed");
			}
		}

		private void log(String message) {
			System.out.println(message); // server side responses
		}

		private String requestType(String input) throws Exception {
			String output = "";
			if (input != null) {

				if (input.startsWith("GET")) {
					if (input.startsWith("GET PINS")) {
						output = "RESULTS:";

						for (int i = 0; i < pins.size(); i++) {
							output += "" + pins.get(i).toString() + "   ";
						}
					} else {
						output = "RESULTS:";
						log("GET");
						List<Note> results = searchNotes(input);

						for (int i = 0; i < results.size(); i++) {
							output += "" + results.get(i).toString() + "   ";
						}
						log(output);
					}
				} else if (input.startsWith("POST")) {
					log("POST");
					try {
						postNote(input);
						output = "POST SUCCESS";
					} catch (Exception e) {
						e.printStackTrace();
						output = "POST ERROR Note doesnt fit on board. Board Height: " + boardHeight + " board width: "
								+ boardWidth;
					}

				} else if (input.startsWith("PIN")) {
					log("PIN");
					output = pinLocation(input, 0);
				} else if (input.startsWith("UNPIN")) {
					log("UNPIN");
					output = pinLocation(input, 1);
				} else if (input.startsWith("CLEAR")) {
					log("CLEAR");
					output = clear();

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

		private void postNote(String input) throws Exception {
			log(input);
			String message = "", color = "";
			int[] xyCoords = parseXY(input);
			int[] wh = parseWH(input);
			int x = xyCoords[0], y = xyCoords[1];
			int w = wh[0], h = wh[1];
			// parse the string and set it to each variable
			if (x + w > boardWidth || y + h > boardHeight || x < 0 || w < 0 || y < 0 || h < 0) {
				throw new Exception();
			}
			int index = input.indexOf("color=");
			if (index == -1) {
				color = defaultcolor;
			} else {
				index += "color=".length();
				while (input.charAt(index) != ',') {
					color += input.charAt(index);
					index++;
				}
			}

			String[] messages = input.split("refersTo=");
			message = messages[1];

			log("New Note");
			synchronized (notes) {
				notes.add(new Note(x, y, w, h, color, message));
			}
			log(notes.get(notes.size() - 1).toString());

		}

		private String pinLocation(String input, int type) {
			// parse the input
			int[] xyCoords = parseXY(input);
			int x = xyCoords[0], y = xyCoords[1];
			synchronized (pins){
				pins.add(new MyPoint(x,y));
			}

			synchronized (notes) {
				for (Note note : notes) {
					if (note.getXCoord() <= x && (x <= note.getXCoord() + note.getWidth())) {
						if (y >= note.getYCoord() && (y <= note.getYCoord() + note.getHeight())) {
							if (type == 0) {
								note.pinNote(x, y);
								return "PIN SUCCESS";
							} else {
								try {
									if (note.isPinned && note.getPin().getX() == x && note.getPin().getY() == y) {
										note.unpinNote(x, y);
									}else{
										return "PINNING ERROR - 1";
									}
								}catch(Exception e){
									e.printStackTrace();
								}

							}
						}
					}
				}
			}
			return "PINNING ERROR - 1";
		}

		private String clear() {
			synchronized (notes) {
				for(int i=0; i<notes.size(); i++) {
					if (!notes.get(i).isPinned()) {
						notes.remove(notes.get(i));
					}
				}
			}
			return "CLEAR SUCCESS";
		}

		private void disconnect() throws Exception {
			try {
				socket.close();
				colors.clear();
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
			// int[] wh = parseWH(input);
			int x = xyCoords[0], y = xyCoords[1];
			// int w = wh[0], h = wh[1];
			if (input.contains("refersTo=")) {
				String[] messages = input.split("refersTo=");
				message = messages[1];
			}
			String color = "";
			int index = input.indexOf("color=");
			if (index != -1) {
				index += "color=".length();
				while (index < input.length() && input.charAt(index) != ',') {
					color += input.charAt(index);
					index++;
				}
				if (color.equals("Default")) {
					color = defaultcolor;
				}
			}

			System.out.println(input);
			System.out.println("message =" + message);
			System.out.println("color = " + color);
			System.out.println("x = " + x);
			System.out.println("y = " + y);

				for (Note note : notes) {
                    results.add(note);
                }
				if (color.length() > 0) {
					for (int i = 0; i < notes.size(); i++) {
						if ((!notes.get(i).color.equals(color) && results.contains(notes.get(i)))) {
							results.remove(notes.get(i));
						}
					}
				}

				if (x >= 0) {
					for (int i = 0; i < notes.size(); i++) {
						if ((notes.get(i).xcoord > x || notes.get(i).xcoord + notes.get(i).width < x)
								&& results.contains(notes.get(i))) {
							results.remove(notes.get(i));
						}
					}
				}
				if (y >= 0) {
					for (int i = 0; i < notes.size(); i++) {
						if ((notes.get(i).ycoord > y || notes.get(i).ycoord + notes.get(i).height < y)
								&& results.contains(notes.get(i))) {
							results.remove(notes.get(i));
						}
					}
				}

				if (message.length() > 0) {
					for (int i = 0; i < notes.size(); i++) {
						if (!notes.get(i).refersTo.contains(message) && results.contains(notes.get(i))) {
							results.remove(notes.get(i));
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
		private int pinedAtX, pinedAtY;

		public Note(int xcoord, int ycoord, int width, int height, String color, String refersTo) {
			this.xcoord = xcoord;
			this.ycoord = ycoord;
			this.width = width;
			this.height = height;
			this.color = color;
			this.refersTo = refersTo;
		}

		public void pinNote(int pinX, int pinY) {
			this.pinedAtX=pinX;
			this.pinedAtY=pinY;
			this.isPinned = true;
		}


		public void unpinNote(int pinX, int pinY)throws Exception {
			if(this.isPinned && pinX==this.pinedAtX && pinY == this.pinedAtY){
			this.isPinned = false;}
			else {
				throw new Exception();
			}
		}

		public Point2D getPin() throws Exception{
			if(this.isPinned){
				return new Point(pinedAtX, pinedAtY);
			}
			else{
				throw new Exception();
			}
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

		public boolean isPinned() {
			return this.isPinned;
		}

		@Override
		public String toString() {
			return "xCoord=" + this.xcoord + ", yCoord=" + this.ycoord + ", width=" + this.width + ", height="
					+ this.height + ", color=" + this.color + ", isPinned=" + this.isPinned + ", refersTo="
					+ this.refersTo;
		}
	}

}
