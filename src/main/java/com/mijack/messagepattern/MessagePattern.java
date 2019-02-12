
package com.mijack.messagepattern;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mi&Jack
 */
public class MessagePattern {

    private String pattern;

    private List<SubMessage> list = new ArrayList<>();

    public MessagePattern(String pattern) {
        this.pattern = pattern;
        boolean result = applyPattern(this.pattern);
        if (!result) {
            throw new IllegalArgumentException("There are some error in the pattern '" + pattern + "'");
        }
    }

    public static MessagePattern compile(String pattern) {
        return new MessagePattern(pattern);
    }

    public boolean applyPattern(String pattern) {
        boolean inArg = false;
        boolean isPre = false;
        int preArgStart = 0;
        int i;
        for (i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '{') {
                if (inArg) {
                    return false;
                }
                if (!isPre) {
                    String content = pattern.substring(preArgStart, i);
                    list.add(new SubMessage(content, null, true, -1));
                    preArgStart = i;
                    inArg = true;
                }
            } else if (c == '}') {
                if (!inArg) {
                    if (!isPre) {
                        return false;
                    }
                } else {
                    if (isPre) {
                        return false;
                    }
                    String substring = pattern.substring(preArgStart + 1, i);
                    int idx = -1;
                    String name = null;
                    if (substring.indexOf(':') != -1) {
                        String[] split = substring.split(":");
                        if (split.length == 1 || split[1].length() == 0) {
                            return false;
                        }
                        name = split[1];
                        try {
                            idx = Integer.valueOf(split[0]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    } else {
                        try {
                            idx = Integer.valueOf(substring);
                            name = null;
                        } catch (NumberFormatException e) {
                            idx = -1;
                            name = substring;
                        }
                    }
                    list.add(new SubMessage(null, name, false, idx));

                    preArgStart = i + 1;
                    inArg = false;
                }
            }

            isPre = c == '\\';
        }
        if (preArgStart != i) {
            list.add(new SubMessage(pattern.substring(preArgStart, i), null, true, -1));
        }

        return !inArg;

    }

    public String format(Object... args) {
        Map<Integer, Object> map = new HashMap<>(8);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                map.put(i, args[i]);
            }
        }
        return format(map);
    }

    public String format(Map<? extends Object, Object> args) {
        StringBuilder sb = new StringBuilder();
        for (SubMessage message : list) {
            if (message.isRawString()) {
                sb.append(message.getContent());
            } else {
                String value = null;
                if (args.containsKey(message.getName())) {
                    value = String.valueOf(args.get(message.getName()));
                } else if (args.containsKey(message.getIndex())) {
                    value = String.valueOf(args.get(message.getIndex()));
                }
                if (value == null) {
                    throw new IllegalStateException("参数" + message.getName() + "未在参数发现");
                }
                sb.append(value);
            }
        }
        return sb.toString();
    }


    public Matcher match(String str) {
        return null;
    }
}
