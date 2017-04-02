import java.net.*;

public class FileServer00 {
	//final static int PORT	 = 8080;	// 待受ポート番号
	final static int PORT 	 = 9998;	// 待受ポート番号
	
	public static void main(String[] argv) throws Exception {
    	// 受付ポート指定して、サーバソケットを作成
		ServerSocket serverSocket = new ServerSocket(PORT);
		
		while (true) {
			// クライアントからの接続を待つ
			Socket client = serverSocket.accept();
			// クライアントとの処理をスレッドで処理するようにする
			FileServerMain serverMain = new FileServerMain(client);
			new Thread(serverMain).start();
		}
	}
}

