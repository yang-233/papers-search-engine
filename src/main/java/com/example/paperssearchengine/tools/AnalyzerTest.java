package com.example.paperssearchengine.tools;

import com.hankcs.lucene.HanLPAnalyzer;
import com.hankcs.lucene.HanLPIndexAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AnalyzerTest {

    private static void doToken(TokenStream ts) throws IOException {//测试分词效果
        ts.reset();
        CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
        while (ts.incrementToken()) {
            System.out.print(cta.toString() + "|");
        }
        ts.end();
        ts.close();
    }
    public static void main(String args[]) {
        String cnText = "能解决能量物质化问题的国家为什么还要在地球上跟这种渣渣打跟原始人丢矛一样的世界大战？" +
                "无论是通过力场技术还是能量物质转化即使来解决问题，都意味着比现在人类技术至少高两个层次或者以上，" +
                "两个层次知道什么概念吗？不是技术水平，是层次，层次。举个最简单的例子就是，人类从隋唐时期确定火药的配方以后，" +
                "当时的土枪土炮其本质上和现代武器就已经处于同一个层次了，都是利用化学能的武器，期间甚至一直到二战都只有技术上的突破，" +
                "而没有基础上的革新。从公元500年到现在的武器在利用能量上没有本质区别，都是用化学能驱动武器，当然还有更原始的生物能时代，" +
                "早期化学能还没有生物能猛，正式超越已经是13、14世纪的事情了。如果人类接下来战争中能利用激光、电磁炮等这些东西作为主战装备，" +
                "才真正从化学能武器时代跨入了能量武器时代。然后距离能力物质化和力场技术至少还有一个时代……而且这个至少两个时代还是猜的，" +
                "因为没人知道能量物质化或者力场技术需要多少基础理论和相关知识的突破。说句不客气的话，现代的洲际导弹在进入能量武器时代以后是必然会被淘汰的，" +
                "先进的激光武器拦截速度和拦截效率都远远超过火药武器，即使你打过来核弹也可以在几分钟甚至几秒钟以内全部拦截，" +
                "到时候核弹自然会失效，甚至用不上所谓的“护盾技术”。核弹是核技术没错，但是你导弹是化学能时代的技术啊……拿护盾技术来挡核弹，" +
                "有点像古代人弄到了现代坦克装甲钢的冶炼技术和相关的制造厂——然后他拿坦克装甲钢给自己做了一套锁子甲，再去战场上砍人。" +
                "皇帝的金锄头就是这个意思。";
        try {
            List<Analyzer> list = new LinkedList<>();
            list.add(new StandardAnalyzer());
            list.add(new SimpleAnalyzer());
            list.add(new SmartChineseAnalyzer());
            list.add(new IKAnalyzer4Lucene7());
            list.add(new HanLPAnalyzer());
            list.add(new HanLPIndexAnalyzer());
//            doToken(list.get(4).tokenStream("content", cnText));
//            for(Analyzer i : list) {
//                TokenStream ts = i.tokenStream("content", cnText);
//                doToken(ts);
//                System.out.println();
//            }
            for(int i = 0; i < list.size(); ++i) {
                doToken(list.get(i).tokenStream("content", "缺血性心脏病是糖尿病患者的主要并发症。"));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
