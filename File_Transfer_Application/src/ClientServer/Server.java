package ClientServer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server {
	static ArrayList<MyFile> myFiles = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		int fileId = 0;
		JFrame jFrame = new JFrame("Server");
		jFrame.setSize(400, 400);
		jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

		JScrollPane jScrollPane = new JScrollPane(jPanel);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JLabel jlTitle = new JLabel("File receiver");
		jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
		jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
		jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		jFrame.add(jlTitle);
		jFrame.add(jScrollPane);
		jFrame.setVisible(true);

		ServerSocket serverSocket =  new ServerSocket(1234);
		try {

			while (true) {
				Socket socket = serverSocket.accept();

				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				
				
				Thread t =new ClientHandler(socket,dataInputStream, myFiles, jPanel);
				
				t.start();

							}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


		
}