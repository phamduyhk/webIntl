import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * 形態素を表す抽象クラス．
 */
abstract class Morpheme {

	// 表層形
	String surface;

	// 品詞文字列
	String posStr;

	// 品詞階層
	String[] pos;

	// 活用形
	String conjForm;

	// 活用型
	String conjType;

	// 原形
	String base;

	// 読み
	String reading;

	// 発音
	String pron;



	/**
	 * この形態素の表層的文字列を返す
	 */
	public String getSurface() {
		return surface;
	}

	/**
	 * この形態素の読みを返す
	 */
	public String getReading() {
		return reading;
	}

	/**
	 * この形態素の品詞（第一階層のみ）を返す
	 */
	public String getPos() {
		return pos[0];
	}

	/**
	 * この形態素の品詞第(i+1)階層を返す
	 */
	public String getPos(int i) {
		return pos[i];
	}

	/**
	 * この形態素の活用形を返す
	 */
	public String getConjugationForm() {
		return conjForm;
	}

	/**
	 * この形態素の活用形を返す
	 */
	public String getConjugationType() {
		return conjType;
	}


	/**
	 * この形態素の原形を返す
	 */
	public String getBaseform() {
		return base;
	}

	/**
	 * この形態素の発音を返す
	 */
	public String getPronunciation() {
		return pron;
	}

	/**
	 * この形態素が動詞ならtrue（真）を返し，そうでなければfalse（偽）を返す
	 */
	public boolean isVerb() {
		return pos[0].equals("動詞");
	}

	/**
	 * この形態素の情報を文字列にして返す
	 */
	public String toString() {
		return "<形態素 表層=\"" + surface + "\"" +
				" 品詞=\"" + posStr + "\"" +
				" 活用形=\"" + conjForm + "\"" +
				" 活用型=\"" + conjType + "\"" +
				" 原形=\"" + base + "\"" +
				" 読み=\"" + reading + "\"" +
				" 発音=\"" + pron + "\" />";
	}

}

