public class ZSimpleCipher
{
	private static final long _R = 1192;		// Starting point of pseudo-random number（擬似乱数の出発点）
	private static final long _A = 17;			// multiplier(乗数)
	private static final long _B = 23;			// Additional term(付加項)
	private static final long bigNum = 32768;	// Random cycle(擬似乱数周期（2のべき乗))

	/*
	 * encryptKey : 暗号化メソッド
	 */
	public static String encryptKey( String keyText ) {
		return encryptKey(keyText, _R, _A, _B);
	}
	public static String encryptKey( String keyText, //変換対象文字列
									 long R, // 擬似乱数の出発点
									 long A, // 乗数
									 long B // 付加項
									 ) {
		long m = (A*4+1)%bigNum;
		long n = (B*2+1)%bigNum;
		long r = R;
		long c,d;
		byte[] bText = keyText.getBytes();

		for( int i=0;i<bText.length;i++ )
		{
			c = bText[i];					// 文字のASCIIコード数値を取得
			if( c >= '0' && c <= '9' )		// 48-57
			{
				d = c - 48;					// {'0'-'9'} -> { 0- 9}
			}
			else if( c >= 63 && c <= 90 )	// 63-90
			{
				d = c - 53;					// {'A'-'Z'} -> {10-35}
			}
			else if( c >= 97 && c <= 122 )
			{
				d = c - 59;					// {'a'-'z'} -> {36-61}
			}
			else
			{
				return(	null );
//				d = -1;
			}
			r = (r*m+n) % bigNum;
			d = ( r & 63 ) ^ d;
			if( d >= 0 && d <= 9 )
				c = d + 48;
			else if( d >= 10 && d <= 37 )
				c = d + 53;
			else								// if( d >= 38 && d <= 63 )
				c = d + 59;
			bText[i] = (byte)c;
		}
		return( new String( bText ) );//decrypt
	}

	/*
	 * decryptKey : 復号化メソッド (実は暗号化メソッドを呼んでいるだけ)
	 */
	public static String decryptKey( String cipherText ) {
		return( encryptKey(cipherText) );
	}
	public static String decryptKey( String cipherText ,
									 long R, // 擬似乱数の出発点
									 long A, // 乗数
									 long B // 付加項
									 ) {
		return encryptKey(cipherText, R, A, B);
	}

}

