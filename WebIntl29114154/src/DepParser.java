import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DepParser {

	public DepParser() {
		// something to need
	}

	public abstract Sentence parse(String sentenceStr);

	public List<Sentence> parseText(String text) {
		List<String> sentenceStrs = splitSentences(text);
		List<Sentence> sentences = new ArrayList<Sentence>();
		for (String sentenceStr: sentenceStrs) {
			Sentence sentence = parse(sentenceStr);
			sentences.add(sentence);
		}
		return sentences;
	}


	/**
	 * 文に区切るためのセパレータ
	 */
	static List separators = Arrays.asList(new String[]{
			"。", "！", "!", "？", "?", "\n"});

	/**
	 * 文に区切る
	 */
	static List<String> splitSentences(String text) {
		List<String> sentences = new ArrayList<String>();
		while(text.length() > 0) {
			int i = -1;
			for (int k = 0; k < separators.size(); k++) {
				String sep = (String)separators.get(k);
				int j = text.indexOf(sep);
				if (j >= 0 && (i < 0 || j < i)) {
					i = j;
				}
			}
			if (i < 0 || i == text.length() - 1) {
				sentences.add(text);
				text = "";
			} else {
				sentences.add(text.substring(0, i + 1));
				text = text.substring(i + 1);
			}
		}
		return sentences;
	}

}
