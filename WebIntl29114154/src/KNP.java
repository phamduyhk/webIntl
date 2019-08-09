import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/*
 * KNP解析結果を扱うクラス．
 */
public class KNP extends DepParser {
	static Process knpPrc;
	static PrintWriter knpOut;
	static BufferedReader knpIn;
	static String knpCmd = "/opt/cse/bin/knp --tab";
	//static String knpCmd = "C:/Program Files (x86)/knp/bin/knp.exe --tab";
	static String encoding = "UTF-8";

	public KNP() {
		super();
	}

	static void startKNP() {
		if (knpPrc != null) {
			knpPrc.destroy();
		}
		try {
			knpPrc = Runtime.getRuntime().exec(knpCmd);
			knpOut = new PrintWriter(new OutputStreamWriter(knpPrc.getOutputStream(), encoding));
			knpIn = new BufferedReader(new InputStreamReader(knpPrc.getInputStream(), encoding));
		} catch (IOException ex) {
			System.err.println("係り受け解析器knpを起動できませんでした");
			System.exit(-1);
		}
	}

	public Sentence parse(String sentenceStr) {
		if (knpOut == null) {
			startKNP();
		}
		knpOut.println(sentenceStr);
		knpOut.flush();
		Sentence sentence = new Sentence();
		Chunk chunk = null;
		try {
			for (String line = knpIn.readLine();
					line != null;
					line = knpIn.readLine()) {
				//System.out.println(line);
				if (line.equals("EOS")) {
					if (sentence.head == null) {
						sentence.head = chunk;
					}
					break;
				} else if (line.startsWith("*")) {
					chunk = new Chunk();
					sentence.add(chunk);
					//String[] tokens = line.split(" ");
					//int id = Integer.parseInt(tokens[1]);
					//chunk.setId(id);
					//if (tokens[2].endsWith("D")) {
					//    int dep = Integer.parseInt(tokens[2].substring(0, tokens[2].length()-1));
					//    chunk.setDependency(dep);
					//    if (dep == -1) {
					//        sentence.head = chunk;
					//    }
					//}
				} else {
					//Morpheme morph = new JumanMorpheme(line);
					//chunk.addMorpheme(morph);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("KNPの係り受け解析に失敗しました: 「"+sentenceStr+"」");
		}
		sentence.initDependency();
		return sentence;
	}



}
