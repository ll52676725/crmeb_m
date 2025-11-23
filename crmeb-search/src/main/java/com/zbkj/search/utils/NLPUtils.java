package com.zbkj.search.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自然语言处理工具类
 * @author CRMEB
 * @since 2024-05-20
 */
@Component
public class NLPUtils {

    /**
     * 分词处理
     * @param text 输入文本
     * @return 分词结果
     */
    public List<String> segment(String text) {
        List<Term> termList = NLPTokenizer.segment(text);
        return termList.stream()
                .map(Term::word)
                .collect(Collectors.toList());
    }

    /**
     * 提取关键词
     * @param text 输入文本
     * @param topN 提取数量
     * @return 关键词列表
     */
    public List<String> extractKeywords(String text, int topN) {
        return HanLP.extractKeyword(text, topN);
    }

    /**
     * 提取时间信息
     * @param text 输入文本
     * @return 时间信息列表
     */
    public List<String> extractTime(String text) {
        List<Term> termList = NLPTokenizer.segment(text);
        List<String> timeList = new ArrayList<>();
        for (Term term : termList) {
            if (term.nature.startsWith("t")) {
                timeList.add(term.word);
            }
        }
        return timeList;
    }

    /**
     * 提取数字信息
     * @param text 输入文本
     * @return 数字信息列表
     */
    public List<String> extractNumbers(String text) {
        List<Term> termList = NLPTokenizer.segment(text);
        List<String> numberList = new ArrayList<>();
        for (Term term : termList) {
            if (term.nature.startsWith("m")) {
                numberList.add(term.word);
            }
        }
        return numberList;
    }

    /**
     * 提取订单号
     * @param text 输入文本
     * @return 订单号
     */
    public String extractOrderNumber(String text) {
        // 简单的订单号提取逻辑，可根据实际订单号格式调整
        List<Term> termList = NLPTokenizer.segment(text);
        for (Term term : termList) {
            String word = term.word;
            // 假设订单号是由字母和数字组成，长度在10-20之间
            if (word.matches("^[A-Za-z0-9]{10,20}$")) {
                return word;
            }
        }
        return null;
    }
}
