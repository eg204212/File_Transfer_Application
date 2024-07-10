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
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    int fileId = 0;
    JPanel jPanel = new JPanel();

    public ClientHandler(Socket socket, DataInputStream dataInputStream, ArrayList<MyFile> myFiles, JPanel jPanel) {
        this.socket = socket;
        this.dataInputStream= dataInputStream;
        this.myFiles = myFiles;
        this.jPanel = jPanel;
        }

    @Override
    public void run() {
        try {   
        	int userNameLength = dataInputStream.readInt();
            byte[] userNameBytes = new byte[userNameLength];
            dataInputStream.readFully(userNameBytes, 0, userNameBytes.length);
            String userName = new String(userNameBytes);

            int fileNameLength = dataInputStream.readInt();

            if (fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);

                int fileContentLength = dataInputStream.readInt();
                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                    JPanel jpFileRow = new JPanel();
					jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

					JLabel jFileName = new JLabel(userName + " sends " + fileName);
					jFileName.setFont(new Font("Arial", Font.BOLD, 20));
					jFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
					jFileName.setAlignmentX(Component.CENTER_ALIGNMENT);


					if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
						jpFileRow.setName(String.valueOf(fileId));
						jpFileRow.addMouseListener(getMyMouseListener());

						jpFileRow.add(jFileName);
						jpFileRow.add(jpFileRow);

						jpFileRow.validate();
					} else {
						jpFileRow.setName(String.valueOf(fileId));
						jpFileRow.addMouseListener(getMyMouseListener());

						jpFileRow.add(jFileName);
						jPanel.add(jpFileRow);

						jpFileRow.validate();
					}

					myFiles.add(new MyFile(fileId,fileName,fileContentBytes,getFileExtension(fileName)));

					fileId ++ ;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }
    
    public static MouseListener getMyMouseListener() 
	{

		return new MouseListener() 
		{

			@Override
			public void mouseClicked(MouseEvent e) {

				JPanel jPanel = (JPanel) e.getSource();
				int fileId =Integer.parseInt(jPanel.getName());

				for(MyFile myFile: myFiles) {
					if(myFile.getId()== fileId) {
						JFrame jfPreview = createFrame(myFile.getName(),myFile.getData(),myFile.getFileExtension());
						jfPreview.setVisible(true);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		};
	} 



	public static JFrame createFrame(String fileName,byte[] fileData, String fileExtension) {
		JFrame jFrame = new JFrame ("File Downloader");
		jFrame.setSize(400,400);

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));

		JLabel jlTitle = new JLabel("File Downloader");
		jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
		jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
		jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel jlPrompt = new JLabel("Are you sre you want to download" + fileName);
		jlPrompt.setFont(new Font("Arial", Font.BOLD, 25));
		jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
		jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton jbYes = new JButton("Yes");
		jbYes.setPreferredSize(new Dimension(150,75));
		jbYes.setFont(new Font("Arial", Font.BOLD, 20));

		JButton jbNo = new JButton("No");
		jbNo.setPreferredSize(new Dimension(150,75));
		jbNo.setFont(new Font("Arial", Font.BOLD, 20));

		JLabel jlFileContent = new JLabel();
		jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);


		JPanel jpButtons = new JPanel();
		jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
		jpButtons.add(jbYes);
		jpButtons.add(jbNo);

		if(fileExtension.equalsIgnoreCase("txt")) {
			jlFileContent.setText("<html>" + new String(fileData) + "<html>");
		} else {
			jlFileContent.setIcon(new ImageIcon(fileData));
		}

		jbYes.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent e) {
				File fileToDownload = new File(fileName);
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

					fileOutputStream.write(fileData);
					fileOutputStream.close();

					jFrame.dispose();
				} catch (IOException error) {
					error.printStackTrace();
				}
				jFrame.dispose();
			

			}

		});
		
		jbNo.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent e) {

				jFrame.dispose();
			}



		});
		jPanel.add(jlTitle);
		jPanel.add(jlPrompt);
		jPanel.add(jlFileContent);
		jPanel.add(jpButtons);

		jFrame.add(jPanel);

		return jFrame;



	}

	public static String getFileExtension(String fileName) 
{
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1);
		} else {
			return "No extension Found ";
		}
	}
}

