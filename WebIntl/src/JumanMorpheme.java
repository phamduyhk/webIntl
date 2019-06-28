import java.io.*;
import java.util.*;

/**
 * Juman解析結果の形態素を表すクラス．
 */
public class JumanMorpheme extends Morpheme {

	String jumanLine;
	static Process jumanPrc;
	static PrintWriter jumanOut;
	static BufferedReader jumanIn;
	//static String jumanCmd = "/opt/cse/bin/juman";
	static String jumanCmd = "C:/cygwin/usr/local/bin/juman";
	static String encoding = "UTF-8";

	/**
	 * コンストラクタ
	 */
	public JumanMorpheme(String line) {
		jumanLine = line;
		// FIXME
	}

	/**
	 * 形態素解析の結果をMorphemeオブジェクトのリストにして返す
	 */
	static ArrayList<Morpheme> analyzeMorpheme(String str) {
		if (jumanPrc == null) {
			startJuman();
		}
		jumanOut.println(str);    // Jumanに文字列を送る
		jumanOut.flush();
		ArrayList<Morpheme> morphs = new ArrayList<Morpheme>();
		try {
			for (String line = jumanIn.readLine(); line != null; line = jumanIn.readLine())  {
				// Jumanから結果を受け取る
				if (line.equals("EOS")) {
					break;
				} else {
					morphs.add(new JumanMorpheme(line));
				}
			}
		} catch (IOException e) {
			System.err.println("Jumanから形態素解析結果を受け取る際にIOExceptionが発生しました");
			e.printStackTrace();
		}
		return morphs;
	}

	/**
	 * 形態素解析器Jumanを開始する
	 */
	static void startJuman() {
		try {
			jumanPrc = Runtime.getRuntime().exec(jumanCmd);
			jumanOut = new PrintWriter(new OutputStreamWriter(jumanPrc.getOutputStream(), encoding));
			jumanIn = new BufferedReader(new InputStreamReader(jumanPrc.getInputStream(), encoding));
		} catch (IOException e) {
			System.err.println("形態素解析器Jumanを起動できませんでした");
			System.exit(-1);
		}
	}



}
