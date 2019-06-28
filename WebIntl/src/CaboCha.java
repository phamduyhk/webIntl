import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * CaboCha解析結果を扱うクラス．
 */
public class CaboCha extends DepParser {

	static Process caboChaPrc;
	static PrintWriter caboChaOut;
	static BufferedReader caboChaIn;
	static String cabochaCmd = "/opt/cse/bin/cabocha -f1";
	//static String cabochaCmd = "C:/Program Files (x86)/CaboCha/bin/cabocha.exe -f1";
	static String encoding = "UTF-8";

	public CaboCha() {
		super();
	}

	static void startCaboCha() {
		if (caboChaPrc != null) {
			caboChaPrc.destroy();
		}
		try {
			caboChaPrc = Runtime.getRuntime().exec(cabochaCmd);
			caboChaOut = new PrintWriter(new OutputStreamWriter(caboChaPrc.getOutputStream(), encoding));
			caboChaIn = new BufferedReader(new InputStreamReader(caboChaPrc.getInputStream(), encoding));
		} catch (IOException ex) {
			System.err.println("係り受け解析器CaboChaを起動できませんでした");
			System.exit(-1);
		}
	}

	public Sentence parse(String sentenceStr) {
		if (caboChaOut == null) {
			startCaboCha();
		}
		caboChaOut.println(sentenceStr);
		caboChaOut.flush();
		Sentence sentence = new Sentence();
		Chunk chunk = null;
		try {
			for (String line = caboChaIn.readLine();
					line != null;
					line = caboChaIn.readLine()) {
				//System.out.println(line);
				if (line.equals("EOS")) {
					if (sentence.head == null) {
						sentence.head = chunk;
					}
					break;
				} else if (line.startsWith("*")) {
					chunk = new Chunk();
					sentence.add(chunk);
					String[] tokens = line.split(" ");
					int id = Integer.parseInt(tokens[1]);
					chunk.setId(id);
					if (tokens[2].endsWith("D")) {
						int dep = Integer.parseInt(tokens[2].substring(0, tokens[2].length()-1));
						chunk.setDependency(dep);
						if (dep == -1) {
							sentence.head = chunk;
						}
					}
				} else {
					Morpheme morph = new MeCabMorpheme(line);
					chunk.addMorpheme(morph);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("CaboChaの係り受け解析に失敗しました: 「"+sentenceStr+"」");
		}
		sentence.initDependency();
		return sentence;
	}



}
