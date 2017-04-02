import java.net.*;

public class ImageServer00 {
	//final static int PORT	 = 8080;	// 待受ポート番号
	final static int PORT	 = 9999;	// 待受ポート番号

	public static void main(String[] argv) throws Exception {
    	// 受付ポート指定して、サーバソケットを作成
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
