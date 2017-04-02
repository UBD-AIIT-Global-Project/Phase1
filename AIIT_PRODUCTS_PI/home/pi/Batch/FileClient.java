import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class FileClient {
	final static String HOST = "210.129.18.29"; 	// 接続先アドレス
	final static int    PORT = 9998;       		 // 接続先ポート番号

	public static void main(String[] args) {
                Socket socket = null;
                BufferedReader in = null;
                BufferedWriter out = null;
                OutputStream outputStream = null;

                String keyStr = args[0];
                String MacAddress = keyStr.substring(0,12);
                String timeStamp  = keyStr.substring(12);

                //String key = "b827eb5b1cec"+"201703170622";   // 引数（Macアドレス＋タイムスタンプ）
                String key = MacAddress+timeStamp;              // ワンタイムパスワード発行
                String enckey = ZSimpleCipher.encryptKey( key );
                System.out.println(timeStamp);

		String filepath = "SENSORDATA.log";   // 送信するファイルのパス
		File   file     = new File(filepath); // 送信するファイルのオブジェクト
		byte[] buffer   = new byte[512];      // ファイル送信時のバッファ

		try {
                        socket = new Socket();

                        // 送信先サーバのIPアドレスとポート番号を定義
                        socket.connect(new InetSocketAddress(HOST, PORT));
                        outputStream = socket.getOutputStream();
                        out = new BufferedWriter(
                        new OutputStreamWriter(outputStream));
                        in = new BufferedReader(new InputStreamReader(
                                        socket.getInputStream()));

                        // 暗号キー送信
                        String request = enckey;
                        out.write(request);
                        out.newLine();
                        out.flush();
                        System.out.println("Send：" + request);
                        String response = in.readLine();
                        System.out.println("Recv：" + response);


			// ストリームの準備
			InputStream  inputStream  = new FileInputStream(file);

			// ファイルをストリームで送信
			int fileLength;
			while ((fileLength = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, fileLength-3);
			}

			// 終了処理
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
