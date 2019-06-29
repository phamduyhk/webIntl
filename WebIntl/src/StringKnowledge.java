import java.util.*;
import java.util.Map.Entry;


public class StringKnowledge {
	
	 public static void main(final String[] args) {
	        final String text = "私はかもめです。タマは猫です。あの物体は魚です。私とタマは敵同士です。"; // 解析対象のテキスト
	        System.out.println("解析対象のテキスト: "+text);

	        // 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
	        final DepParser parser = new CaboCha();
	        final List<Sentence> sentences = parser.parseText(text);

	        HashMap<String, String> KnowledgeList = getMapOfKnowledge(sentences);
	        System.out.print("質問：");
	        Scanner scan = new Scanner(System.in);
	        String question = scan.next();
	        final List<Sentence> questions = parser.parseText(question);
	        for(final Sentence ques: questions) {
	        	String quesTopic = analysisQuestion(ques);
	        	if (KnowledgeList.containsKey(quesTopic)){
	        		final String answer = KnowledgeList.get(quesTopic);
	        		System.out.println(quesTopic+"は"+answer +"です。");
	        	}
	        	if(KnowledgeList.containsValue(quesTopic)) {
	        		String answer = "";
	        		for(Map.Entry<String, String> entry : KnowledgeList.entrySet()){
//	        			System.out.println(entry.getKey() + ":" + entry.getValue());
	        			if(entry.getValue()==quesTopic) {
	        				answer = entry.getKey();
	        			}
	        		 }
	        		System.out.println(quesTopic+"は"+answer+"です。");
	        	}
	        }
	 
	        
	    }
	 public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		    Set<T> keys = new HashSet<T>();
		    for (Entry<T, E> entry : map.entrySet()) {
		        if (Objects.equals(value, entry.getValue())) {
		            keys.add(entry.getKey());
		        }
		    }
		    return keys;
		}
	 
	 public static String analysisQuestion(Sentence sentence) {
	     String topic = "";
		 Chunk headChunk = sentence.getHeadChunk();
		 List<Chunk> dependents = headChunk.getDependents();
		 for(final Chunk dependent: dependents) {
             final Morpheme headMorph = dependent.getHeadMorpheme();
             if (headMorph.getPos().equals("助詞") && 
                     headMorph.getSurface().equals("は")) 
             {
                 // 文節dependent内の助詞以外の形態素をつなげる
                 for (int i = 0; i < dependent.size()-1; i++) {
                     topic += dependent.get(i).getSurface();
                 }
             }
		 }
		 return topic;
	 }
	 
	 
	 
	public static HashMap<String, String> getMapOfKnowledge(List<Sentence> sentences) {
	     final HashMap<String, String> KnowledgeList = new HashMap<String, String>();
		  for (final Sentence sentence : sentences) { // 文を1つずつ処理するループ
	          // 主辞（最後の文節）を取得
	          final Chunk headChunk = sentence.getHeadChunk(); 
	          // 主辞に係る文節のリストを返す
	          final List<Chunk> dependents = headChunk.getDependents();
	          for (final Chunk dependent : dependents) {
	              // 文節dependentの主辞（最後の形態素）を取得
	              final Morpheme headMorph = dependent.getHeadMorpheme();
	              // 助詞の「は」かどうか判定するif文
	              if (headMorph.getPos().equals("助詞") && 
	                      headMorph.getSurface().equals("は")) 
	              {
	                  // 文節dependent内の助詞以外の形態素をつなげる
	                  String topic = "";
	                  for (int i = 0; i < dependent.size()-1; i++) {
	                      topic += dependent.get(i).getSurface();
	                  }
//	                  System.out.println("主辞に係るハ格: "+topic);
	                  for(Morpheme item : headChunk) {
	                  	if(item.getPos().contains("名詞"))
	                  	KnowledgeList.put(item.getSurface(),topic);
	                  }
	              }              
	          }
	      }
		return KnowledgeList;
	}
	
}
