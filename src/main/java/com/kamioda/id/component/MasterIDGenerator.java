package com.kamioda.id.component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kamioda.id.model.SequenceNumber;
import com.kamioda.id.repository.SequenceNumberRepository;

@Component
public class MasterIDGenerator {
    private static int getIDFrontTextIndex(String id) {
        char ch = id.charAt(0);
        if (ch >= '0' && ch <= '9') return ch - '0';
        if (ch >= 'A' && ch <= 'Z') return 20 + (ch - 'A');
        if (ch >= 'a' && ch <= 'z') return 60 + (ch - 'a');
        return -1;
    }
    private final class Separate {
        private static final Pattern HEX256 = Pattern.compile("^[0-9a-fA-F]{256}$");
        private static final Pattern HEX512 = Pattern.compile("^[0-9a-fA-F]{512}$");
        private static final Pattern NUMTXT = Pattern.compile("^\\d+$");

        public static List<String> id(String str) {
            int len = str.length();
            int pLen = (len + 1) / 2; // ceil(len/2)
            List<String> out = new ArrayList<>(2);
            out.add(str.substring(0, pLen));
            out.add(str.substring(pLen));
            return out;
        }

        public static List<String> twoMergedSHA512Text_4(String str) {
            if (!HEX256.matcher(str).matches()) {
                throw new IllegalArgumentException("invalid two merged SHA512 text");
            }
            List<String> parts = chunk(str, 64);
            if (parts.size() != 4) throw new IllegalArgumentException("Split Error");
            return parts;
        }

        public static List<String> twoMergedSHA512Text_8(String str) {
            if (!HEX256.matcher(str).matches()) {
                throw new IllegalArgumentException("invalid two merged SHA512 text");
            }
            List<String> parts = chunk(str, 32);
            if (parts.size() != 8) throw new IllegalArgumentException("Split Error");
            return parts;
        }

        public static List<String> fourMergedSHA512Text_16(String str) {
            if (!HEX512.matcher(str).matches()) {
                throw new IllegalArgumentException("invalid four merged SHA512 text");
            }
            List<String> parts = chunk(str, 32);
            if (parts.size() != 16) throw new IllegalArgumentException("Split Error");
            return parts;
        }

        public static List<String> numTextToMax4Digit(String numStr) {
            if (!NUMTXT.matcher(numStr).matches()) {
                throw new IllegalArgumentException("invalid number text");
            }
            return chunk(numStr, 4);
        }

        private static List<String> chunk(String s, int size) {
            List<String> out = new ArrayList<>((s.length() + size - 1) / size);
            for (int i = 0; i < s.length(); i += size) {
                int end = Math.min(i + size, s.length());
                out.add(s.substring(i, end));
            }
            return out;
        }
    }
    private final class SwapAndMerge {
        private SwapAndMerge() {}

        public static String single(List<String> strs) {
            if (strs.size() != 2) throw new IllegalArgumentException("string array length is not 2");
            return strs.get(1) + strs.get(0);
        }

        public static List<String> multiple(List<String> strs) {
            if (strs.isEmpty()) throw new IllegalArgumentException("string array is empty");
            if (strs.size() % 2 != 0) throw new IllegalArgumentException("string array length is not even");

            List<String> out = new ArrayList<>(strs.size() / 2);
            for (int i = 0; i < strs.size(); i += 2) {
                out.add(single(List.of(strs.get(i), strs.get(i + 1))));
            }
            return out;
        }
    }
    private final class Serial {
        private static List<String> toSHA512(List<String> strs, String format) {
            if (strs == null || strs.isEmpty()) {
                throw new IllegalArgumentException("string array is empty");
            }
            return strs.stream()
                    .map(s -> HashUtils.toHash(s, "SHA-512", format))
                    .collect(Collectors.toList());
        }

        private static final Pattern allowedNumPattern = Pattern.compile("^[0-9]+$");
        private static final Pattern allowedHexPattern = Pattern.compile("^[0-9a-fA-F]+$");

        public static int calcCheckDigitWithCorpNumberCheckDigitAlgorithm(String numStr) {
            if (numStr == null || !allowedNumPattern.matcher(numStr).matches()) {
                throw new IllegalArgumentException("Invalid input");
            }
            // 末尾から重み(1,2,1,2,...)をかけて合計
            int total = 0;
            String reversed = new StringBuilder(numStr).reverse().toString();
            for (int i = 0; i < reversed.length(); i++) {
                char c = reversed.charAt(i);
                int digit = Character.digit(c, 10);
                total += digit * ((i % 2) + 1);
            }
            return 9 - (total % 9);
        }

        private static String convertHexadecimals(String str) {
            if (str == null || !allowedHexPattern.matcher(str).matches()) {
                throw new IllegalArgumentException("Invalid input");
            }
            StringBuilder sb = new StringBuilder(str.length() * 2);
            for (int i = 0; i < str.length(); i++) {
                int v = Character.digit(str.charAt(i), 16);
                // 0..15 を 2桁の10進数に（例: 0 -> "00", A(10) -> "10", F(15) -> "15"）
                sb.append(String.format("%02d", v));
            }
            return sb.toString();
        }

        private static List<String> merge16Numbers(List<Integer> nums) {
            if (nums == null || nums.size() != 16) {
                throw new IllegalArgumentException("Invalid input");
            }
            List<String> out = new ArrayList<>(4);
            for (int blockIndex = 0; blockIndex < 4; blockIndex++) {
                StringBuilder sb = new StringBuilder(12);
                for (int j = 0; j < 4; j++) {
                    int v = nums.get(blockIndex * 4 + j);
                    sb.append(String.format("%03d", v)); // 3桁ゼロ埋め
                }
                out.add(sb.toString());
            }
            return out;
        }

        private static int m10w4Total(String str) {
            if (str == null || !allowedNumPattern.matcher(str).matches()) {
                throw new IllegalArgumentException("Invalid input");
            }
            int total = 0;
            for (int i = 0; i < str.length(); i++) {
                int digit = Character.digit(str.charAt(i), 10);
                total += digit * ((i % 4) + 1); // 重み 1,2,3,4,1,2,3,4,...
            }
            return total;
        }

        public static int calc(String id) {
            Objects.requireNonNull(id, "id");

            // s1
            List<String> s1Result = Separate.id(id);

            // s2_3
            String s2_3Result = SwapAndMerge.single(toSHA512(s1Result, "hex"));

            // s4
            List<String> s4Result = Separate.twoMergedSHA512Text_4(s2_3Result);

            // s5
            String s5Result = String.join("", SwapAndMerge.multiple(s4Result));

            // s6
            List<String> s6Result = Separate.twoMergedSHA512Text_8(s5Result);

            // s7
            List<String> s7Result = SwapAndMerge.multiple(s6Result);

            // s8
            String s8Result = toSHA512(s7Result, "hex").stream().collect(Collectors.joining());

            // s9
            List<String> s9Result = Separate.fourMergedSHA512Text_16(s8Result);

            // s10
            List<String> s10Result = s9Result.stream()
                    .map(Serial::convertHexadecimals)
                    .collect(Collectors.toList());

            // s11
            List<Integer> s11Result = s10Result.stream()
                    .map(Serial::m10w4Total)
                    .collect(Collectors.toList());

            // s12
            List<String> s12Result = merge16Numbers(s11Result).stream()
                    .map(s -> {
                        int checkDigit = calcCheckDigitWithCorpNumberCheckDigitAlgorithm(s);
                        return checkDigit + s; // 先頭にチェックデジットを付加
                    })
                    .collect(Collectors.toList());

            // s13
            int sum = s12Result.stream()
                    .mapToInt(Integer::parseInt)   // Kotlin側に合わせて int で計算
                    .sum();
            List<String> s13Result = Separate.numTextToMax4Digit(Integer.toString(sum));

            // s14
            int s14Result = IntStream.range(0, s13Result.size())
                    .map(i -> Integer.parseInt(s13Result.get(i)) * (i + 1))
                    .sum();

            return s14Result % 1000;
        }
    }
    @Autowired
    private SequenceNumberRepository sequenceNumberRepository;
    public String generate(String id) {
        int frontIndex = getIDFrontTextIndex(id);
        int serial = Serial.calc(id);
        SequenceNumber sequenceNumber = sequenceNumberRepository.find(String.format("%02d", frontIndex), String.format("%03d", serial));
        Long sequenceNumberValue = 0L;
        if (sequenceNumber == null) sequenceNumberRepository.add(String.format("%02d", frontIndex), String.format("%03d", serial));
        else sequenceNumberValue = sequenceNumber.getSequenceNumber();
        sequenceNumberRepository.increment(String.format("%02d", frontIndex), String.format("%03d", serial));
        String BaseID = String.format("%02d%03d%06d", frontIndex, serial, sequenceNumberValue);
        return Serial.calcCheckDigitWithCorpNumberCheckDigitAlgorithm(BaseID) + BaseID;
    }
}
