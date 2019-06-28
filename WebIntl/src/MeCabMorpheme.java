import java.io.*;
import java.util.*;

/**
 * MeCab解析結果の形態素を表すクラス．
 */
public class MeCabMorpheme extends Morpheme {
	String mecabLine;
	static Process mecabPrc;
	static PrintWriter mecabOut;
	static BufferedReader mecabIn;
	//static String mecabCmd = "/opt/cse/bin/mecab";
	static String mecabCmd = "C:/cygwin/usr/local/bin/mecab";
	static String encoding = "UTF-8";

	/**
	 * コンストラクタ
	 */
	public MeCabMorpheme(String line) {
		mecabLine = line;
		String[] arr = line.split("\t");
		surface = arr[0];
		String feat = arr[1];
		String[] tokens = feat.split(",");
		posStr = "";
		pos = new String[4];
		for (int i = 0; i <= 3; i++) {
			pos[i] = tokens[i];
			if (i > 0) {
				posStr += ",";
			}
			posStr += tokens[i];
		}
		conjType = tokens[4];
		conjForm = tokens[5];
		base = tokens[6];
		if (tokens.length > 7) {
			reading = tokens[7];
			pron = tokens[8];
		} else {
			reading = "";
			pron = "";
		}
	}

	/**
	 * 形態素解析の結果をMorphemeオブジェクトのリストにして返す
	 */
	static ArrayList<Morpheme> analyzeMorpheme(String str) {
		if (mecabPrc == null) {
			startMeCab();
		}
		mecabOut.println(str);    // MeCabに文字列を送る
		mecabOut.flush();
		ArrayList<Morpheme> morphs = new ArrayList<Morpheme>();
		try {
			for (String line = mecabIn.readLine(); line != null; line = mecabIn.readLine())  {
				// mecabから結果を受け取る
				if (line.equals("EOS")) {
					break;
				} else {
					morphs.add(new MeCabMorpheme(line));
				}
			}
		} catch (IOException e) {
			System.err.println("MeCabから形態素解析結果を受け取る際にIOExceptionが発生しました");
			e.printStackTrace();
		}
		return morphs;
	}

	/**
	 * 形態素解析器MeCabを開始する
	 */
	static void startMeCab() {
		try {
			mecabPrc = Runtime.getRuntime().exec(mecabCmd);
			mecabOut = new PrintWriter(new OutputStreamWriter(mecabPrc.getOutputStream(), encoding));
			mecabIn = new BufferedReader(new InputStreamReader(mecabPrc.getInputStream(), encoding));
		} catch (IOException e) {
			System.err.println("形態素解析器MeCabを起動できませんでした");
			System.exit(-1);
		}
	}



}
