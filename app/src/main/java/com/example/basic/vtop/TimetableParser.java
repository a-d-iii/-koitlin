package com.example.basic.vtop;

import com.example.basic.ClassEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Utility to parse the HTML timetable table returned by VTOP.
 * This closely mirrors the Python vitap-vtop-client parser.
 */
public class TimetableParser {
    public static Map<String, List<ClassEntry>> parse(String html) {
        Map<String, List<ClassEntry>> timetable = new LinkedHashMap<>();
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementById("timeTableStyle");
        if (table == null) return timetable;

        Elements rows = table.select("tr");
        if (rows.size() < 4) return timetable;

        // Parse timeslots from the first four rows
        List<String> theoryStarts = textFrom(rows.get(0).select("td").subList(2, rows.get(0).select("td").size()));
        List<String> theoryEnds = textFrom(rows.get(1).select("td").subList(1, rows.get(1).select("td").size()));
        if (theoryEnds.size() > theoryStarts.size()) theoryEnds = theoryEnds.subList(0, theoryStarts.size());

        List<String> labStarts = textFrom(rows.get(2).select("td").subList(2, rows.get(2).select("td").size()));
        List<String> labEnds = textFrom(rows.get(3).select("td").subList(1, rows.get(3).select("td").size()));
        if (labEnds.size() > labStarts.size()) labEnds = labEnds.subList(0, labStarts.size());

        List<String> theoryTimes = combineTimes(theoryStarts, theoryEnds);
        List<String> labTimes = combineTimes(labStarts, labEnds);

        Map<String, String> dayMap = Map.of(
                "MON", "Monday",
                "TUE", "Tuesday",
                "WED", "Wednesday",
                "THU", "Thursday",
                "FRI", "Friday",
                "SAT", "Saturday",
                "SUN", "Sunday"
        );

        for (int i = 4; i < rows.size(); i += 2) {
            Element theoryRow = rows.get(i);
            Element dayCell = theoryRow.selectFirst("td");
            if (dayCell == null) continue;
            String dayName = dayMap.getOrDefault(dayCell.text().trim(), dayCell.text().trim());
            timetable.putIfAbsent(dayName, new ArrayList<>());

            Elements tCells = theoryRow.select("td");
            List<String> theorySlots = new ArrayList<>();
            for (int j = 2; j < tCells.size() && j - 2 < theoryTimes.size(); j++) {
                theorySlots.add(tCells.get(j).text().trim());
            }

            List<String> labSlots = new ArrayList<>();
            if (i + 1 < rows.size()) {
                Elements labCells = rows.get(i + 1).select("td");
                for (int j = 1; j < labCells.size() && j - 1 < labTimes.size(); j++) {
                    labSlots.add(labCells.get(j).text().trim());
                }
            }

            int slot = 0;
            for (String info : theorySlots) {
                if (!skip(info) && slot < theoryTimes.size()) {
                    timetable.get(dayName).add(buildEntry(info, theoryTimes.get(slot)));
                }
                slot++;
            }
            slot = 0;
            for (String info : labSlots) {
                if (!skip(info) && slot < labTimes.size()) {
                    timetable.get(dayName).add(buildEntry(info, labTimes.get(slot)));
                }
                slot++;
            }
        }
        return timetable;
    }

    private static ClassEntry buildEntry(String courseInfo, String time) {
        String[] parts = courseInfo.split("-");
        String courseCode = parts.length > 1 ? parts[1].trim() : courseInfo;
        String course = parts.length > 0 ? parts[0].trim() : courseInfo;
        String venue = parts.length > 4 ? parts[3].trim() + "-" + parts[4].trim() : "";
        String start = "";
        String end = "";
        if (time.contains("-")) {
            String[] se = time.split("-");
            start = se[0].trim();
            end = se.length > 1 ? se[1].trim() : "";
        }
        return new ClassEntry(courseCode.isEmpty() ? course : courseCode, "", start, end, venue);
    }

    private static boolean skip(String info) {
        return info.equals("-") || info.equalsIgnoreCase("Lunch") || info.equals("CLUBS/ECS") || info.equals("ECS/CLUBS");
    }

    private static List<String> combineTimes(List<String> starts, List<String> ends) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < Math.min(starts.size(), ends.size()); i++) {
            String s = starts.get(i);
            String e = ends.get(i);
            if ("Lunch".equals(s) && "Lunch".equals(e)) res.add("Lunch");
            else res.add(s + " - " + e);
        }
        return res;
    }

    private static List<String> textFrom(List<Element> elements) {
        List<String> list = new ArrayList<>();
        for (Element e : elements) {
            list.add(e.text().trim());
        }
        return list;
    }
}
