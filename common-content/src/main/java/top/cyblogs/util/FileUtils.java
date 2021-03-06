package top.cyblogs.util;

import cn.hutool.core.util.IdUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载工具的公共参数和方法
 * 2019年7月13日
 */
public class FileUtils {

    private static final int FILE_NAME_MAX_LENGTH = 255;
    /**
     * 文件名匹配
     * 字母，数字，空格，部分英文符号，汉字，部分汉字符号
     */
    private static Pattern FILE_NAME_PATTERN = Pattern.compile("([\\w\\d\\s~!@#$%^&()_\\-.{}\\[\\]\\u4e00-\\u9fa5\\u3002\\uff1f\\uff01\\uff0c\\u3001\\uff1b\\uff1a\\u201c\\u201d\\u2018\\u2019\\uff08\\uff09\\u300a\\u300b\\u3008\\u3009\\u3010\\u3011\\u300e\\u300f\\u300c\\u300d\\ufe43\\ufe44\\u3014\\u3015\\u2026\\u2014\\uff5e\\ufe4f\\uffe5])+");

    /**
     * 得到可用的文件名（通用性一般不包含特殊的字符，因为范围广，并且太复杂 比如说\U000E字符）
     * 2020年2月8日已经更换匹配方式，从替换错误字符改为匹配正确字符
     *
     * @param fileName 文件名
     * @return 可用的文件名
     */
    public static String getPrettyFileName(String fileName) {
        // 无文件名随机生成一个
        if (fileName == null) {
            return IdUtil.fastSimpleUUID();
        }
        // 文件名过长的情况
        if (fileName.length() > FILE_NAME_MAX_LENGTH) {
            fileName = fileName.substring(0, 127);
        }
        StringBuilder sb = new StringBuilder();
        matchRightFileName(sb, fileName);
        return sb.toString().trim();
    }

    /**
     * 匹配正确的文件名
     */
    private static void matchRightFileName(StringBuilder sb, String fileName) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
        if (matcher.find()) {
            sb.append(matcher.group());
            matchRightFileName(sb, fileName.substring(matcher.end()));
        }
    }
}