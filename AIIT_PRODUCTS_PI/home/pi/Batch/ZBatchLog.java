/******************************************************************************
 *	  ZBatchLogクラス
 ******************************************************************************
 * 　バッチ処理用のログ作成を行います。start()メソッドは最初に、public String end()
 * メソッドは必ず最後に使用してください。
 *
 *
 * ○コンストラクタ
 *	（推奨コンストラクタ）
 *	public ZBatchLog(String filename)

 *	//初期化を行います。
 *		引数
 *		  String filename：自分の業務名を登録してください。
 *		（ログ作成先ディレクトリは、このクラスのプロパティファイルで指定）
 *
 *		戻り値
 *		  エラーを返します。
 *
 *
 *	 public ZBatchLog(String filename, String ls)
 *	//初期化を行います。
 *		引数
 *		  String filename：自分の業務名を登録してください。
 *		  String ls		 ：　実際に書き込みをするディレクトリを指定してください。
 * 　　　　　　　　　　　　　　Windowsの場合￥マークは￥￥で書いてください。また最
 * 　　　　　　　　　　　　　　後に￥￥を忘れないでください。
 *		戻り値
 *		  エラーを返します。
 *
 *
 *
* ○メソッド
 *	  public int start()
 *	//ファイルオープンを行い、ファイルにバッチ処理開始と記入します
 *		引数　なし
 *		戻り値
 *		  エラーを返します。
 *
 *	  public int log(String com, int j)
 *	//ファイルに実行結果を記入します。
 *		引数
 *		  String com　：エラーの内容
 *		  int j　　　　：エラーの種類
 *					  L_NORMAL、	正常終了
 *					  L_ERROR、		異常終了
 *					  L_WARNING、	警告
 *					  L_REPORT、	報告
 *		戻り値
 *		  　エラーを返します。
 *
 *	  public int end()
 *	//終了を記入しファイルをクローズします。
 *		引数　なし
 *		戻り値
 *			エラーを返します。
 *
 *	  public int setLogFilename()
 *	// ログファイル名を設定します
 *	// 注)start()の前に実行してください
 *		引数
 *		  String filename　：ログファイル名
 *		戻り値
 *			エラーを返します。
 *
 * //使用例
	public static void main(String[] args){
	  ZBatchLog	batchLog = new ZBatchLog("xxxxxBat");
	//スタートの記入
	  batchLog.start();
	//正常終了の記入
	  batchLog.log("procExec 正常終了" ,ZBatchLog.L_NORMAL);
	//正常終了の記入
	  batchLog.log("procExec 異常終了(ErrorRturn) :" + ret ,ZBatchLog.L_ERROR);
	//終了の記入
	  batchLog.end();
	}

	batchLog.start(false)	を指定することにより、ログファイルを上書きモードで作成する。
	batchLog.start(true)	を指定することにより、ログファイルを追加モードで作成する。
	batchLog.start()		は、batchLog.start(true)と同等。

 */


import java.io.*;
import java.util.*;
import java.text.*;

public class ZBatchLog {

//フィールド
	/**
	 * ＬＯＧレベル定数・正常
	 */
	public static final int L_NORMAL = 1;
	/**
	 * ＬＯＧレベル定数・異常
	 */
	public static final int L_ERROR = 2;
	/**
	 * ＬＯＧレベル定数・警告
	 */
	public static final int L_WARNING = 3;
	/**
	 * ＬＯＧレベル定数・報告
	 */
	public static final int L_REPORT = 4;

  final String[] dirs	  = {"/home/pi/Batch/Log",		   //publicディレクトリ
	 "/home/pi/Log"};	

//  final String[] dirs	  = {"c:\\",		   //publicディレクトリ
//							 "c:\\ttt\\"};
/*
  private String filename = null;			   //時間付きファイルネーム格納用
*/
  private String files	  = null;			   //ファイルネーム格納
  private String dir	  = null;			   //ディレクトリ格納
  private OutputStream fw = null;			   //OutputStreamクラス
  private ByteArrayOutputStream bw	= null;	   //ByteArrayOutputStreamクラス
  Runtime runtime = null;
  Properties properties;
 DecimalFormat fmt = null;

  protected static final String PROPERTIES_NAME	  = File.separatorChar
												  + "home"
												  + File.separatorChar
												  + "pi"
												  + File.separatorChar
												  + "Batch"
												  + File.separatorChar
												  + "ZBatchLog.properties";

//コンストラクタ
  public ZBatchLog(String filename, int ls){
	  //ファイルネームの格納
		this.files = filename;
	  //作業ディレクトリとファイル名を連結する
		this.dir = dirs[ls].concat(createFileName(filename) + ".log");
  }


  public ZBatchLog(String filename, String ls){
	  //ファイルネームの格納
		this.files = filename;
	  //作業ディレクトリとファイル名を連結する
		this.dir = ls.concat(createFileName(filename) + ".log");
  }

  public ZBatchLog(String filename)	 {
	  //ファイルネームの格納
	  this.files = filename;
	  properties = new Properties();

	  try {
		// properties インスタンスにプロパティファイルの情報をloadする
		properties.load(new FileInputStream(PROPERTIES_NAME));
	  } catch (IOException e) {
		properties.put("LOGDIR", "/tmp");
	  }

	  //作業ディレクトリとファイル名を連結する
	  this.dir = properties.getProperty("LOGDIR") + File.separatorChar + createFileName(filename) + ".log";
  }


  private String createFileName(String filename){
		Calendar todaysCal = new GregorianCalendar();
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		String yyyymmdd = formater.format(todaysCal.getTime());

	  //ファイル名と時間を連結する
		return (filename.concat(yyyymmdd));
  }

//メソッド
  public int start(){
	return start(true);
  }
//メソッド
  public int start(boolean aFlg){
	int result = 0;			//エラー格納用
	try{
	//ファイルへの書き込み
	//	System.out.println("ZBatchLog files = " + this.dir);
	  fw = new FileOutputStream(this.dir,aFlg);
	  bw = new ByteArrayOutputStream();
	  runtime = Runtime.getRuntime();
	  fmt = new DecimalFormat("###,###,###,##0");
//	  logWrite("N:" + df.format(new Date()) + ":" + this.files + "バッチ処理開始\n");
	  log("バッチ処理開始",L_NORMAL);
	}catch(Exception e) {
		result = 1;
	}
	return result;
  }


  public int log(String com, int j){

	int result = 0;			//エラー格納用
//	Date day = new Date();	 //時刻の収得
	Locale locale = new Locale("ja", "JP");
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
	String Level;
	switch (j) {
		case L_NORMAL:
			Level = "N";
			break;
		case L_ERROR:
			Level = "E";
			break;
		case L_REPORT:
			Level = "R";
			break;
	  default:
			Level = "W";
	  }
	try{

	  logWrite(		Level + ":"
	  			+ 	df.format(new Date()) + ":"
	  			+ this.files + ":" + com + "\n");

	}catch(Exception e) {
		result = 1;
	}
	return result;
  }

  public int end(){
	int result = 0;			//エラー格納用
	try{
	  log("バッチ処理終了",L_NORMAL);
	  bw.close();
	  fw.close();
	}catch(Exception e) {
		result = 1;
	}
	return result;
  }
  public PrintStream getPrintStream() {
	return new PrintStream(fw);
  }
  private void logWrite(String logData){
//	System.out.println("ZBatchLog logWrite");

	try {
		byte[] s2 = logData.getBytes("UTF-8");
		bw.write(s2, 0, s2.length);
		bw.writeTo(fw);
		bw.reset();
		fw.flush();

	} catch (Exception e) {
		System.out.println("ZBatchLog Exception = " + e);
	}
	return;
  }

	// ログファイル名を設定します
	// 注)start()の前に実行してください
	public int setLogFilename(String filename){
		//ファイルネームの格納
		properties = new Properties();
		int result = 0;		//エラー格納用

		try {
			// properties インスタンスにプロパティファイルの情報をloadする
			properties.load(new FileInputStream(PROPERTIES_NAME));
		} catch (IOException e) {
			properties.put("LOGDIR", "/home/pi/Batch/Log");
			result = 1;
		}

		//作業ディレクトリとファイル名を連結する
		dir = properties.getProperty("LOGDIR") + File.separatorChar + filename;
		return result;
	}

}

