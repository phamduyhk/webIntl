import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.net.*;
import javax.net.ssl.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;


public class PersonRelAnalyzer extends JFrame{
	
	
	// setting for Jtable
	static Object[][] data = {};
	static String[] columns = {};
	public PersonRelAnalyzer() {
		  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    setSize(720, 1080);

			JTable table = new JTable(data, columns);
			add(new JScrollPane(table));
	}
	

	
		/**
	     * CaboChaの係り受け解析を試す例
	     */
		
	public static String getPhrases(Chunk headChunk, String joshi) {
		String caseStr = "";
		List<Chunk> dependents = headChunk.getDependents();
		for (Chunk dependent: dependents) {
        	Morpheme headMorph = dependent.getHeadMorpheme();
        	if(headMorph.getPos().equals("助詞") &&
        			headMorph.getSurface().equals(joshi))
        	{
                for (int i = 0; i < dependent.size(); i++) {
                    caseStr += dependent.get(i).getSurface();
                }
//        		System.out.println("助詞の「の」がついた語句:"+caseStr);
        	}
        	else {
        		getPhrases(dependent, joshi);
        	}
        }
		return caseStr;
	}
	
	// 小課題0705-4
	/**
     * 与えられた文の集合から，表層格を抽出してみる
     * @param sentences 文の集合
     */
	public static void printCaseStr(List<Sentence> sentences) {
		 for (Sentence sentence : sentences) { // 文を1つずつ処理するループ
		     // 主辞（最後の文節）を取得
		     Chunk headChunk = sentence.getHeadChunk();
		     // 主辞に係る文節のリストを返す
		     List<Chunk> dependents = headChunk.getDependents();
		     for (Chunk dependent : dependents) {
		       // 文節dependentの主辞（最後の形態素）を取得
		       Morpheme headMorph = dependent.getHeadMorpheme();
		       // 助詞かどうか判定
		       if (headMorph.getPos().equals("助詞")) {
		         // 文節dependent内の助詞以外の形態素をつなげる
		         String caseStr = "";
		         for (int i = 0; i < dependent.size()-1; i++) {
		           caseStr += dependent.get(i).getSurface();
			 }
			 System.out.println(headMorph.getSurface()+"格: "+caseStr);
		       }
		     }
		 }
	}
	/**
     * 与えられたURLからHTML等のコンテンツを取得し，返す．
     * @param url 取得するコンテンツのURL
     * @param enc コンテンツの文字コード（UTF-8やEUC-JP, Shift_JISなど）
     * @return コンテンツ
     */
    public static String getWebContent(String url, String enc) {
	StringBuffer sb = new StringBuffer();
	try {
	    BufferedReader in = null;
	    if (url.startsWith("https")) {
		HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
	    } else {
		URLConnection conn = new URL(url).openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
	    }
	    for (String line = in.readLine(); line != null; line = in.readLine()) {
		sb.append(line);
		sb.append("\n");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return sb.toString();
    }
	/**
     * 文の中の人名を抽出する
     * @param sentences 文章
     * @return 人名のArrayList
     */
    public static List<String> getPersonName(List<Sentence> sentences) {
    	// 人名の出現頻度を表すリスト（重複可能）
    	List<String> personNameFreq = new ArrayList<String>();
    	// 文章に含まれている人物のリスト（重複不可）
    	Set<String> personNameList = new HashSet<String>();
    	for (final Sentence sentence : sentences) { 
	        for (final Chunk chunk : sentence) {
	        	for(Morpheme item:chunk) {
	        		if(item.posStr.contains("名詞,固有名詞,人名")) {
	        			personNameList.add(item.getSurface());
	        		}
	        	}
	        }
    	}
    	//　取得できなかった人物を指定
    	List<String> exceptionPersonName = new ArrayList<String>(Arrays.asList(
    			"ロイロット"));
    	for(String personName: exceptionPersonName) {
    		personNameList.add(personName);
    	}
    	for(final Sentence sentence:sentences) {
    		final Chunk headChunk = sentence.getHeadChunk(); 
	        final List<Chunk> dependents = headChunk.getDependents();
	        for(Chunk chunk:sentence) {
	        	for(Morpheme mor : chunk) {
	        		if(personNameList.contains(mor.getSurface())){
	        			personNameFreq.add(mor.getSurface());
	        		}
	        	}
	        }
    	}
    	return personNameFreq;
    }
    
	/**
     * 文の中の人名を抽出する
     * @param sentences 文章
     * @param countPersonFreq 上位n人の名前を標準出力に表示する
     * @return 人名のArrayList
     */
    
    //【課題１】
    public static Map<String, Integer> countPersonFreq(List<Sentence> sentences,int countPersonFreq,List<String> exceptNameList) {
    	List<String> personNames = getPersonName(sentences);
    	for(String exceptName:exceptNameList) {
    		personNames.removeAll(exceptNameList);
    	}
    	Map<String, Integer> map = new HashMap<>();
    	for(String name:personNames) {
    		if (!map.containsKey(name)) {
    			map.put(name, 1);
    		}
    		else {
    			map.put(name,map.get(name)+1);
    		}
    	}
         Map<String, Integer> sortedMap = 
        	     map.entrySet().stream()
        	    .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
        	    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
        	                              (e1, e2) -> e1, LinkedHashMap::new));;
        int count = 0;
        //　結果のマップ
        Map<String,Integer> result = new HashMap<>();
    	for (Entry<String, Integer> entry : sortedMap.entrySet()) {
    		count++;
    		if(count>countPersonFreq) {
    			break;
    		}
    		result.put(entry.getKey(), entry.getValue());
        }
		return result;
    }
    
    //【課題２】
    public static void getPredArgStructure(Sentence sentence) {
    	// 考慮する格を指定
		List<String> caseList = new ArrayList<String>(Arrays.asList(
				"が","に","を","は","から","まで","で","より","と","へ"));
    	PredArgs args = sentence.getPredArgs(caseList);
    	if(args.getSurface()!=null)
    		System.out.println(args.getSurface());
    }
    
    // 【課題３】
    public static void getKnowledgeGraph(List<Sentence> sentences) {
    	//　人名リストを取得
    	List<String> exceptNameList = new ArrayList<>(Arrays.asList("ワトソン","ホームズ","シャーロック"));
    	Map<String, Integer> personName = countPersonFreq(sentences,3,exceptNameList);
    	String[] nameArr = new String[3];
    	int i = 0;
    	for(String person:personName.keySet()) {
    		nameArr[i] = person;
    		i++;
    	}
    	String[] columnsTemp =  {nameArr[0],nameArr[1],nameArr[2]};
    	columns = columnsTemp;
    	// 考慮する格を指定（全ての格）
		List<String> caseList = new ArrayList<String>(Arrays.asList(
				"が","に","を","は","から","まで","で","より","と","へ"));
		// 登場人物が現れる述語項構造を表す表のためのデータ
    	ArrayList<Object[]> temp = new ArrayList<Object[]>();
    	for(Sentence sentence: sentences) {
    		// 登場人物の現れるかどうかを示すマップの初期値
    		Map<String,Boolean> personWithFlag = new HashMap<String,Boolean>();
    		for(String person:personName.keySet()) {
    			personWithFlag.put(person, false);
    		}
    		// 文の中に人物が現れるかどうかを検知
    		for(Chunk chunk:sentence) {
    			for(Morpheme mor:chunk) {
    				for(String person : personWithFlag.keySet()) {
    	    			if(mor.getSurface().contains(person)) {
    	    				personWithFlag.put(person,true);
    	    			}
    	    		}
    			}
    		}
    		if(personWithFlag.values().contains(true)) {
    			// 表のデータ処理
    			PredArgs args = sentence.getPredArgs(caseList);
    			String argsSurface = "";
    	      	if(args.getSurface()!=null)
    	      		argsSurface = args.getSurface();
    	      	//登場人物が含まれる文を標準出力
//            	System.out.println(sentence.getSurface());
    	      	String[] dataStr = new String[3];
    	      	i = 0;
    	      	for(Boolean value:personWithFlag.values()) {
    	      		if(value==true) {
    	      			dataStr[i] = argsSurface;
    	      		}
    	      		else {
    	      			dataStr[i] = "";
    	      		}
    	      		i++;
    	      	}
    	      	Object [] SentenceData = {
    	      			dataStr[0],dataStr[1],dataStr[2]
    	      	};
				temp.add(SentenceData);
    		}
    	}
    	Object[][] array = new Object[temp.size()][];
    	i = 0;
    	for(Object[] item:temp) {
    		array[i] = new Object[3];
    		array[i][0] = item[0];
    		array[i][1] = item[1];
    		array[i][2] = item[2];
    		i++;
    	}
    	data = array;
    	// 表を表示する
    	SwingUtilities.invokeLater(new Runnable() {
    	      @Override
    	      public void run() {
    	    	PersonRelAnalyzer app = new PersonRelAnalyzer();
    	        app.setVisible(true);
    	      }
    	    });
    }


	//【小課題0705-2】
    public static String getTextFromUrl(String url) {
    	String html = getWebContent(url, "Shift_JIS"); 
        Pattern mainTextPat = Pattern.compile("<div class=\"main_text\">(.+?)</div>", Pattern.DOTALL | Pattern.MULTILINE); 
        String text = "";
        Matcher mainTextMat = mainTextPat.matcher(html); 
        if (mainTextMat.find()) {
            String mainText = mainTextMat.group(1);  // 本文を取り出す
            // 【小課題0705-1】 HTMLタグ全般にマッチする正規表現
            String strReg = "<[^>]*>";
            mainText = mainText.replaceAll(strReg, "");
            text = mainText.replace("\n", "").replace("\r", "");
        }
        return text;
    }
    
    public static void main(final String[] args) {
    	String url = "https://www.aozora.gr.jp/cards/000009/files/50717_36994.html"; // 「まだらのひも」のURL
    	String url2 = "https://www.aozora.gr.jp/cards/000022/files/4874_14008.html"; // [昔の女 三島霜川]
        String text = getTextFromUrl(url);
        String test_text = "ボブが彼に殴られた";

//        System.out.println("解析対象のテキスト: "+text);

        //【小課題0705-3】
        // 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
        final DepParser parser = new CaboCha();
//        final List<Sentence> sentences = parser.parseText(text);
        final List<Sentence> sentences = parser.parseText(text);
//        System.out.println("係り受け解析結果:\n" + sentences);
        
          
        for (final Sentence sentence : sentences) { 
            // 主辞（最後の文節）を取得
            final Chunk headChunk = sentence.getHeadChunk(); 
//            System.out.println(headChunk);
            // 主辞に係る文節のリストを返す
            final List<Chunk> dependents = headChunk.getDependents();
            for (final Chunk dependent : dependents) {
                // 文節dependentの主辞（最後の形態素）を取得
                final Morpheme headMorph = dependent.getHeadMorpheme();
                
                // 【小課題0705-4】 表層格を取得
//                if(headMorph.getPos().equals("助詞")) { 
//            		String caseStr = ""; 
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println(headMorph.getSurface()+ "格:" +caseStr);
//                }
                
                // 助詞の「は」かどうか判定するif文
//                if (headMorph.getPos().equals("助詞") && 
//                        headMorph.getSurface().equals("は")) 
//                {
//                    // 文節dependent内の助詞以外の形態素をつなげる
//                    String caseStr = "";
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println("主辞に係るハ格: "+caseStr);
//                }
                
//                // ヲ格
//                if (headMorph.getPos().equals("助詞") && 
//                        headMorph.getSurface().equals("を")) 
//                {
//                    // 文節dependent内の助詞以外の形態素をつなげる
//                    String caseStr = "";
//                    for (int i = 0; i < dependent.size()-1; i++) {
//                        caseStr += dependent.get(i).getSurface();
//                    }
//                    System.out.println("主辞に係るヲ格: "+caseStr);
//                }                      
            }
            
//            getPhrases(headChunk,"の");
        }
        
       //小課題0705-4
//        printCaseStr(sentences);
        
      //【小課題0705-5】人名のリストを抽出
        List<String> personNames = new ArrayList<String>();
    	personNames = getPersonName(sentences);
//    	System.out.println(personNames);
        
      	//【課題１】上位人名
    	System.out.println("========課題1=======");
    	System.out.println("上位3人の名前:");
    	List<String> exceptNameList = new ArrayList<>(Arrays.asList("シャーロック"));
    	Map<String, Integer> countPersonFreg = countPersonFreq(sentences,3,exceptNameList);
    	System.out.println(countPersonFreg.keySet());
    	System.out.println("上位3人の名前とその名前が現れる回数:");
       	System.out.println(countPersonFreg);
      	
      	//【課題２】
      	System.out.println("========課題2=======");
      	System.out.println("文章の全ての述語項構造：");
      	for (Sentence sentence:sentences) {
      		getPredArgStructure(sentence);
      	}
      	//【課題３】
      	System.out.println("========課題3=======");
      	getKnowledgeGraph(sentences);
    }
}