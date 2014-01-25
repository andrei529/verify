import java.io.*;
import java.net.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Calendar;

public class httpget {
	
	final int BUFFER_SIZE = 128000;
    File soundFile;
    AudioInputStream audioStream;
    AudioFormat audioFormat;
    SourceDataLine sourceLine;

	public String getHTML(String urlToRead) throws IOException{
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		url = new URL(urlToRead);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			result += line;
		}
		rd.close();
		return result;
	}

   public static void main(String args[]){
	httpget c = new httpget();
	String site = "site/index.html";
	String resp = null;
	try{
		resp = c.getHTML(site);
	}
	catch(Exception e){
		e.printStackTrace();
		System.exit(0);
	}
	int hash1=7;
	char[] r = resp.toCharArray();
	for (int i=0; i<r.length; i++) {
		hash1 = hash1*31+r[i];
	}
	while (true){
		Calendar cal = Calendar.getInstance();
		c = new httpget();
		System.out.println(site+"\tHora: "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));
		try{
			resp = c.getHTML(site);
		}
		catch(Exception e){
			e.printStackTrace();
			continue;
		}
		int hash=7;
		r = resp.toCharArray();
		for (int i=0; i<r.length; i++) {
			hash = hash*31+r[i];
		}
		System.out.println("Hash do site: "+hash);
		if (hash1 != hash){
			System.out.println("Site modificado");
			c.playSound("music.wav");
			break;
		}
		else{
			try{
				Thread.sleep(5*60000);
			}
			catch(Exception e){
				break;
			}
		}
	}
}

    public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
}
