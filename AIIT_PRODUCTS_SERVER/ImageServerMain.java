import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class ImageServerMain implements Runnable {

	//final static int PORT	 = 8080;// 待受ポート番号
	final static int PORT  = 9999;	// 待受ポート番号

	private ImageServerMain() {}
	private Socket clientSocket = null;
	
	public ImageServerMain(Socket aClientSocket) {
		this.clientSocket = aClientSocket;	        
	 }

	public void run() {		
		//ServerSocket serverSocket = null; 	// サーバ用のソケット(親側に実装）
		Socket client  = null; 			// ソケットをやり取りする為に使用する
		BufferedReader in = null;
		BufferedWriter out = null;
		InputStream  inputStream  = null;
		OutputStream outputStream = null;

		String outputFilepath = "test.jpg";  	// 受信したファイル（最初に受信）
		byte[] buffer         = new byte[512]; 	// ファイル受信時のバッファ

		try {
			client  = this.clientSocket;; 		// コネクションの要求が来るまで待機する。

			// Socketでのやり取りをテキストベースにする為に、BufferedWriterを使用します。受信する際に使用します。
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			// Socketでのやり取りをテキストベースにする為に、BufferedReaderを使用します。送信する際に使用します。
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			//暗号キー受信
			String str; 
			str = in.readLine(); // クライアント側からの送信を１行読み込む
			System.out.println("受信：" + str);
				
			//暗号キーを複合化（最初12桁がMACアドレス。後12桁がタイムスタンプ）
			String deckey = ZSimpleCipher.decryptKey( str );
				
			String MacAddress = deckey.substring(0,12);
			String timeStamp  = deckey.substring(12);

			if((str != null) && (MacAddress != null) && (MacAddress.length()==12)){
				outputFilepath = MacAddress+".jpg";
			}

			//複合キー
			String responce = "Recved:"+deckey;
			out.write(responce); 
			out.newLine(); 
			out.flush(); 
			System.out.println("返信：" + responce);

			// 世代管理
			File file = new File(outputFilepath);				
			File dir = new File("./Old/"+outputFilepath+"_"+timeStamp);
			if(file.exists()){
				file.renameTo(dir);				
			}
				
			// ファイルをストリームで受信
			inputStream  = client.getInputStream();
			outputStream = new FileOutputStream(outputFilepath);

			int fileLength;
			while ((fileLength = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, fileLength);
			}

			// 終了処理
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			client.close();
				
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try { //必ずクローズ。（順序）
				//if (serverSocket != null) {
				//	serverSocket.close();
				//}
				if (client != null) {
					client.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	// テスト
	public static void main(String[] args) throws IOException {
	    // 受付ポートを指定して、サーバソケットを作成
		ServerSocket serverSocket = new ServerSocket(PORT);		
		while (true) {
			// クライアントからの接続を待つ
			Socket client = serverSocket.accept();
			// クライアントとの処理をスレッドで処理するようにする
			ImageServerMain serverMain = new ImageServerMain(client);
			new Thread(serverMain).start();
		}
	}
}

