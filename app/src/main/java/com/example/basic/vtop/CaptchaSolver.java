package com.example.basic.vtop;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Utility to solve VTOP captcha using the same lightweight model as the Python client. */
public class CaptchaSolver {
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static double[][] WEIGHTS;
    private static double[] BIASES;
    static {
        try (InputStream is = new FileInputStream("vitap_vtop_client/resources/weights.json")) {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(json);
            JSONArray wArr = obj.getJSONArray("weights");
            int rows = wArr.length();
            int cols = wArr.getJSONArray(0).length();
            WEIGHTS = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                JSONArray row = wArr.getJSONArray(i);
                for (int j = 0; j < cols; j++) {
                    WEIGHTS[i][j] = row.getDouble(j);
                }
            }
            JSONArray bArr = obj.getJSONArray("biases");
            BIASES = new double[bArr.length()];
            for (int i = 0; i < bArr.length(); i++) {
                BIASES[i] = bArr.getDouble(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String solve(String base64) throws Exception {
        if (WEIGHTS == null || BIASES == null) return null;
        byte[] data = Base64.getDecoder().decode(base64);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
        if (img == null) return null;
        StringBuilder captcha = new StringBuilder();
        for (int idx = 0; idx < 6; idx++) {
            int x1 = (idx + 1) * 25 + 2;
            int y1 = 7 + 5 * (idx % 2) + 1;
            int x2 = (idx + 2) * 25 + 1;
            int y2 = 35 - 5 * ((idx + 1) % 2);
            double[] bw = new double[24 * 22];
            double sum = 0;
            int p = 0;
            for (int y = y1; y < y2; y++) {
                for (int x = x1; x < x2; x++) {
                    int rgb = img.getRGB(x, y);
                    int gray = (rgb >> 16 & 0xff) * 299 + (rgb >> 8 & 0xff) * 587 + (rgb & 0xff) * 114;
                    gray /= 1000;
                    sum += gray;
                    bw[p++] = gray;
                }
            }
            double avg = sum / bw.length;
            for (int i = 0; i < bw.length; i++) {
                bw[i] = bw[i] > avg ? 0 : 1;
            }
            int best = 0;
            double bestVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < BIASES.length; j++) {
                double v = BIASES[j];
                for (int i = 0; i < bw.length; i++) {
                    v += bw[i] * WEIGHTS[i][j];
                }
                if (v > bestVal) {
                    bestVal = v;
                    best = j;
                }
            }
            captcha.append(LETTERS.charAt(best));
        }
        return captcha.toString();
    }
}
