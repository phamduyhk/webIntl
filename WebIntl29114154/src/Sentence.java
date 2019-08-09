import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文を表すクラス．
 * 文節(Chunk)のリストであり，文節間の係り受け関係を保持する．
 */
class Sentence extends ArrayList<Chunk> {

	Chunk head;

	public static void main(String[] args) {
		String text = "ロイロットはヘレンの義理の父である。ロイロットはインドでヘレンの母と結婚した。"; // 解析対象のテキスト
		System.out.println("解析対象のテキスト: "+text);

		//
		DepParser parser = new CaboCha();
		// 対象テキストを文に分割した上で係り受け解析．
		List<Sentence> sents = parser.parseText(text);
		System.out.println(sents);
	}

	Sentence() {
		super();
	}

	Sentence(List<Chunk> chunks) {
		super(chunks);
		initDependency();
	}

	public void initDependency() {
		Iterator<Chunk> i = this.iterator();
		while (i.hasNext()) {
			Chunk chunk = i.next();
			int dependency = chunk.getDependency();
			if (dependency == -1) {
				head = chunk;
			} else {
				Chunk depChunk = this.get(dependency);
				depChunk.addDependentChunk(chunk);
				chunk.setDependencyChunk(depChunk);
			}
		}
	}





	/**
	 * この文に含まれる文節間の係り受け構造を表すXML風の文字列を返す
	 * @return XML風の文字列
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<文 主辞=\""+ head.getId() +"\">\n");
		for (Iterator<Chunk> i = iterator(); i.hasNext(); ) {
			sb.append(i.next().toString());
			sb.append("\n");
		}
		sb.append("</文>");
		return sb.toString();
	}

	/**
	 * 主辞の文節を返す
	 * @return 主辞の文節
	 */
	public Chunk getHeadChunk() {
		return head;
	}

	/**
	 * 指定した品詞・原型の形態素を主辞に持つ文節を探して返す
	 * @param pos 探しててる文節の主辞形態素の品詞
	 * @param baseform 探している文節の主辞形態素の原型
	 * @return 見つかった文節のリスト
	 */
	public List<Chunk> findChunkByHeads(String pos, String baseform) {
		List<Chunk> matches = new ArrayList<Chunk>();
		for (Iterator<Chunk> i = this.iterator(); i.hasNext(); ) {
			Chunk chunk = i.next();
			Morpheme head = chunk.getHeadMorpheme();
			if (pos.equals(head.getPos()) && baseform.equals(head.getBaseform())) {
				matches.add(chunk);
			}
		}
		return matches;
	}


	/**
	 * 動作主格の文節を返す
	 * @return 動作主格の文節
	 */
	public Chunk getAgentCaseChunk() {
		// 主辞に係る文節の中からガ格の文節を探す
		Chunk cand = findChunkByHead(head.getDependents(), "助詞", "が");
		if (cand != null) return cand;
		// 主辞に係る文節の中からハ格の文節を探す
		cand = findChunkByHead(head.getDependents(), "助詞", "は");
		if (cand != null) return cand;
		// 全ての文節の中からガ格の文節を探す
		cand = findChunkByHead(this, "助詞", "が");
		if (cand != null) return cand;
		// 全ての文節の中からハ格の文節を探す
		cand = findChunkByHead(this, "助詞", "は");
		return cand;
	}


	/**
	 * 指定した品詞・原型の形態素を主辞に持つ文節を文節リストchunksから探して返す
	 * @param chunks 文節リスト
	 * @param pos 探しててる文節の主辞形態素の品詞
	 * @param baseform 探している文節の主辞形態素の原型
	 * @return 見つかった文節
	 */
	public Chunk findChunkByHead(List<Chunk> chunks, String pos, String baseform) {
		for (Iterator<Chunk> i = chunks.iterator(); i.hasNext(); ) {
			Chunk chunk = i.next();
			Morpheme head = chunk.getHeadMorpheme();
			if (pos.equals(head.getPos()) && baseform.equals(head.getBaseform())) {
				return chunk;
			}
		}
		return null;
	}
	
	/**
	 * 解析した文を述語項構造を表すPredArgStructオブジェクトへ変換するメソッド
	 * @param caseList 考慮する格を指定するArrayList
	 * @return 述語項構造を返す
	 */
	
	public PredArgs getPredArgs(List<String> caseList) {
		PredArgs args = new PredArgs();
		for(Chunk element:this) {
    		int dependency = element.getDependency();
    		if(dependency<0) {
    			PredArgStruct predArg = new PredArgStruct();
    			Morpheme headMorpheme = element.getHeadMorpheme();
    			Morpheme predicate = element.get(0);
    			if(!predicate.getPos().contains("記号")) {
    				predArg.setPred(predicate);
    				List<Chunk> dependents = element.getDependents();
        			for(Chunk dependent:dependents) {
        				if(dependent.getHeadMorpheme().getPos().contains("助詞")) {
        					String joshi = dependent.getHeadMorpheme().getBaseform();
        					if(caseList.contains(joshi)) {
        						String caseStr = "";
            					for (int i = 0; i < dependent.size()-1; i++) {
         				           caseStr += dependent.get(i).getSurface();
            					}
            					String key = dependent.getHeadMorpheme().getReading()+"格";
            				    predArg.addArgs(key, caseStr);
        					}
        				}
        			}
    			}
    	  		args.addPredArgSruct(predArg);
    		}
    	}
		return args;
		
	}
	
	public String getSurface() {
		String str = "";
		for(Chunk chunk:this) {
			for(Morpheme mor:chunk) {
				str+=mor.getSurface();
			}
		}
		return str;
	}


}


