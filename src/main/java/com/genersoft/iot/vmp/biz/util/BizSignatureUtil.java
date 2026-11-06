package com.genersoft.iot.vmp.biz.util;

import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import org.springframework.util.ObjectUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * 终端告警接入 AK/SK 签名校验
 */
public final class BizSignatureUtil {

    private static final long MAX_CLOCK_SKEW_MS = 5 * 60 * 1000L;
    private static final String HMAC_SHA256 = "HmacSHA256";

    private BizSignatureUtil() {
    }

    public static void verify(String accessKey, String timestamp, String signature, String body, String secretKey) {
        if (ObjectUtils.isEmpty(accessKey) || ObjectUtils.isEmpty(timestamp) || ObjectUtils.isEmpty(signature)) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "缺少接入签名参数");
        }
        long timestampValue;
        try {
            timestampValue = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "时间戳格式错误");
        }
        if (Math.abs(System.currentTimeMillis() - timestampValue) > MAX_CLOCK_SKEW_MS) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "请求时间偏差过大");
        }
        String content = accessKey + "\n" + timestamp + "\n" + body;
        String expected = hmacSha256Hex(secretKey, content);
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8))) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "签名校验失败");
        }
    }

    public static String hmacSha256Hex(String secretKey, String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] digest = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("签名计算失败", e);
        }
    }
}
