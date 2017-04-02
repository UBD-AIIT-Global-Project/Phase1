import java.sql.Statement;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class FileServerMain implements Runnable{
	final static int PORT = 9998;   // 待受ポート番号
	final static int H_RENGTH = 42; // ヘッダ部レコード長

	//プロパティファイル名
	protected static final String PROPERTIES_NAME =
				File.separatorChar
					+ "home"
					+ File.separatorChar
					+ "vagrant"
					+ File.separatorChar
					+ "development"
					+ File.separatorChar
					+ "WEB"
					+ File.separatorChar
					+ "application"
					+ File.separatorChar
					+ "var"
					+ File.separatorChar
					+ "www"
					+ File.separatorChar
					+ "app"
					+ File.separatorChar
					+ "FileServerMain.properties";
        //バッチログ
        private ZBatchLog batchLog = null;
        private Socket clientSocket = null;
        private FileServerMain() {}

        public FileServerMain(Socket aClientSocket) {
                this.clientSocket = aClientSocket;
        }


        public void run() {
        	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm");
        	String targetDate    = fmt.format(new java.util.Date());
        	Properties properties =  new Properties();
        	batchLog = new ZBatchLog("FileServerMain");
        	batchLog.start();

                String outputFilepath = "tmp.out";              // 受信したファイルの保存先
                byte[] buffer      = new byte[512];             // ファイル受信時のバッファ
                BufferedReader in  = null;
                BufferedWriter out = null;

                /** データ名：直接契約データ */
                byte[] data_h  = null;
                byte[] data_r  = null;
                Socket client  = null;                          // ソケットをやり取りする為に使用する


                try {
			//プロパティファイルのロード
		    /*
		    InputStream inputStream = new FileInputStream(
				new File(PROPERTIES_NAME));
		    properties.load(inputStream);
		    String rssKind = properties.getProperty("RSS-KIND");
		    */
			
	       	    // クライアントソケットの準備
	            client  = this.clientSocket;; 		// コネクションの要求が来るまで待機する。

                    // Socketでのやり取りをテキストベースにする為に、BufferedWriterを使用します。受信する際に使用します。
                    out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    // Socketでのやり取りをテキストベースにする為に、BufferedReaderを使用します。送信する際に使用します。
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    //暗号キー受信
                    String str; str = in.readLine(); // クライアント側からの送信を１行読み込む
                    System.out.println("受信：" + str);

                    //暗号キーを複合化（最初12桁がMACアドレス。後12桁がタイムスタンプ）
                    String deckey = ZSimpleCipher.decryptKey( str );

                    String MacAddress     = deckey.substring(0,12);
                    String datatimeStamp  = deckey.substring(12);

                    /*
                    if((str != null) && (MacAddress != null) && (MacAddress.length()==12)){
                            outputFilepath = MacAddress+".jpg";
                    }
                    */
                    //複合キー
                    String responce = "Recved:"+deckey;

                    out.write(responce);
                    out.newLine();
                    out.flush(); // 書き込んだ文字を送信先に送信する。
                    System.out.println("返信：" + responce);

                    // ストリームの準備
                    InputStream  inputStream  = client.getInputStream();
                    OutputStream outputStream = new FileOutputStream(outputFilepath);

                    // ファイルをストリームで受信
                    int fileLength = 0;
                    System.out.println(fileLength);
                    String timeStamp = "";
                    String sensorID   = "";

                    while ((fileLength = inputStream.read(buffer)) > 0) {
                    	int [] ivalueArray   = new int [4];
                            String[][] dataArray = new String [4][2];
                            //ヘッダ部ストリーム
                            System.out.println(fileLength);
                            data_h = new byte [H_RENGTH];
                            for (int i = 0; i<H_RENGTH; i++){
                                    data_h[i] = buffer[i];
                            }
                            // ヘッダ部の値チェック（割り算・Lengthチェック）                                                     

                            // MACアドレス12桁切り出し
                            sensorID = (new String (data_h)).substring(12,24);
                            System.out.println("****"+sensorID);

                            // タイムスタンプ（YYYYMMDDHHMMSS)14桁
                            System.out.println(new String(data_h));
                            timeStamp = (new String (data_h)).substring(26,40);
                            System.out.println("****"+timeStamp);

                            //レコード部ストリーム
                            data_r = new byte [fileLength-H_RENGTH];
                            for (int i=0; i<fileLength-H_RENGTH; i++){
                                    data_r[i] = buffer[H_RENGTH+i];
                            }
                            String recData = (new String (data_r).substring(1,data_r.length));
                            System.out.println("***"+recData);

                            int offset = 32;
                            for (int i=0; i<4; i++){
                                    int lp = offset * i;
                                    dataArray[i][0] = sensorID + recData.substring(lp+8,lp+10);
                                    dataArray[i][1] = recData.substring(lp+10,lp+18);

                                    String ind = ".";
                                    String wk = dataArray[i][1];
                                    if(wk.indexOf(ind) > 0){
                                            wk = wk.substring(0,wk.indexOf(ind));
                                    }
                                    int iValue = Integer.parseInt(wk);
                                    ivalueArray[i] = iValue;

                                    System.out.println(wk);
                                    System.out.println("sensorID:"+dataArray[i][0]);
                                    System.out.println("value:"+dataArray[i][1]);
                                    System.out.println("ivalue:"+ivalueArray[i]);
                            }
                            //データ素性チェック

                            //DB登録
                            Connection connection = null;
                            PreparedStatement ps = null;
                            Statement statement = null;
                            ResultSet resultSet = null;

                            try {
                            	String driver   = "org.postgresql.Driver";
                                    //-----------------
                                    // 接続
                                    //-----------------
                                    connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/enpit", // "jdbc:postgresql://[場所(Domain)]:[ポート番号]/[DB名]"
                                    "vagrant", // ログインロール
                                    "enpit"); // パスワード
                                    statement = connection.createStatement();
                                    connection.setAutoCommit(false);
                                    //-----------------
                                    // SQLの発行
                                    //-----------------

                                    //実行するSQL文とパラメータを指定する
                                    String sql = "INSERT INTO tbl20001_sensor_datas " +
                                    "(sensor_id, str_timestamp,str_year, str_mon,str_day,str_hh,value,ivalue,timestamp) " +
                                            "values (?, ?, ?, ?, ?, ?, ?, ?,current_timestamp)";

                                    ps = connection.prepareStatement(sql);
                                    for (int i=0; i<4; i++){
                                            //ps.setString(1, "00000001");          //センサID
                                            ps.setString(1, dataArray[i][0]);       //センサID
                                            //ps.setString(2, "20170311120000");    //タイムスタンプ
                                            ps.setString(2, timeStamp);             //タイムスタンプ
                                            //ps.setString(3, "2017");              //年
                                            ps.setString(3, timeStamp.substring(0,4));//年
                                            //ps.setString(4, "03");                //月
                                            ps.setString(4, timeStamp.substring(4,6));//月
                                            //ps.setString(5, "11");                //日
                                            ps.setString(5, timeStamp.substring(6,8));//日
                                            //ps.setString(6, "12");                   //時間
                                            ps.setString(6, timeStamp.substring(8,10));//時間
                                            //ps.setString(7, "00027.97");          //値
                                            ps.setString(7, dataArray[i][1]);       //値
                                            //ps.setInt   (8, 27);                  //数値
                                            ps.setInt   (8, ivalueArray[i]);        //数値

                                            System.out.println(sql);

                                            //INSERT文を実行する
                                            int cnt = ps.executeUpdate();

                                            //処理件数を表示する
                                            System.out.println("結果：" + cnt);

                                            //コミット
                                            connection.commit();
                                    }

                            } catch (Exception e) {
                                    e.printStackTrace();

                            } finally {

                                    try{
                                            //接続を切断する
                                            if (resultSet != null) {
                                                    resultSet.close();
                                            }
                                            if (statement != null) {
                                                    statement.close();
                                            }
                                            if (connection != null) {
                                                    connection.close();
                                            }
                                    } catch (Exception e){
                                            e.printStackTrace();
                                    }

                            }
                            //Output ファイル作成（後で消す）
                            outputStream.write(buffer, 0, fileLength);
                            
                    }

                    // 終了処理
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    client.close();
                    //serverSocket.close();
                    
                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        try { // 終わった後の後始末。
                                if (in != null) {
                                        in.close();
                                }
                                if (out != null) {
                                        out.close();
                                }

                        } catch (Exception e) {
                                System.out.println(e.getMessage());
                        }
                }
        batchLog.end();
        }

        public static void main(String[] args) {

        }
	
}
