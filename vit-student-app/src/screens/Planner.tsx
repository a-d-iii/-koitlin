import React, { useRef, useState } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Platform,
  StatusBar,
  PanResponder,
  GestureResponderEvent,
  PanResponderGestureState,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { WEEKLY_SCHEDULE, ClassEntry } from '../data/weeklySchedule';

const DAYS = Object.keys(WEEKLY_SCHEDULE);

export default function Planner() {
  const [index, setIndex] = useState<number>(0);
  const day = DAYS[index];
  const classes: ClassEntry[] = WEEKLY_SCHEDULE[day];

  const pan = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => false,
      onMoveShouldSetPanResponder: (
        _evt: GestureResponderEvent,
        g: PanResponderGestureState
      ) => Math.abs(g.dx) > 20 && Math.abs(g.dx) > Math.abs(g.dy),
      onMoveShouldSetPanResponderCapture: (
        _evt: GestureResponderEvent,
        g: PanResponderGestureState
      ) => Math.abs(g.dx) > 20 && Math.abs(g.dx) > Math.abs(g.dy),
      onPanResponderRelease: (_e, g) => {
        if (g.dx < -20) {
          setIndex((i) => Math.min(i + 1, DAYS.length - 1));
        } else if (g.dx > 20) {
          setIndex((i) => Math.max(i - 1, 0));
        }
      },
      onPanResponderTerminationRequest: () => false,
      onShouldBlockNativeResponder: () => false,
    })
  ).current;

  return (
    <LinearGradient colors={['#69cbff', '#1cddfe']} style={{ flex: 1 }}>
      <SafeAreaView
        style={[
          styles.container,
          { paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight : 0, backgroundColor: 'transparent' },
        ]}
      >
      <Text style={styles.heading}>Weekly Timetable</Text>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.dayRow}
        contentContainerStyle={{ paddingHorizontal: 16 }}
      >
        {DAYS.map((d, i) => (
          <TouchableOpacity
            key={d}
            onPress={() => setIndex(i)}
            style={[styles.dayButton, day === d && styles.dayButtonActive]}
          >
            <Text
              style={[styles.dayText, day === d && styles.dayTextActive]}
            >
              {d.slice(0, 3)}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      <ScrollView
        contentContainerStyle={styles.scheduleContainer}
        {...pan.panHandlers}
      >
        {classes.map((cls, idx) => (
          <TouchableOpacity
            key={idx}
            style={styles.classBox}
            activeOpacity={0.7}
          >
            <View style={styles.classLeft}>
              <Text style={styles.courseText}>{cls.course}</Text>
              <Text style={styles.facultyText}>{cls.faculty}</Text>
            </View>
            <View style={styles.classRight}>
              <Text style={styles.timeText}>
                {cls.start} – {cls.end}
              </Text>
              <Text style={styles.roomText}>{cls.room}</Text>
            </View>
          </TouchableOpacity>
        ))}
      </ScrollView>
      </SafeAreaView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  heading: {
    fontSize: 28,
    fontWeight: '700',
    marginVertical: 16,
    marginHorizontal: 16,
    color: '#333',
  },
  dayRow: {
    flexGrow: 0,
  },
  dayButton: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 20,
    marginRight: 8,
    backgroundColor: '#e0e0e0',
  },
  dayButtonActive: {
    backgroundColor: '#6C5CE7',
  },
  dayText: { fontSize: 14, color: '#333' },
  dayTextActive: { color: '#fff', fontWeight: '700' },
  scheduleContainer: { padding: 16 },
  classBox: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
    paddingVertical: 10,
    paddingHorizontal: 12,
    marginVertical: 6,
    elevation: 0,
  },
  classLeft: { flex: 1 },
  courseText: { fontSize: 14, fontWeight: '600', color: '#333' },
  facultyText: { fontSize: 12, color: '#666', marginTop: 2 },
  classRight: { justifyContent: 'space-between', alignItems: 'flex-end' },
  timeText: { fontSize: 12, color: '#333' },
  roomText: { fontSize: 10, color: '#666', marginTop: 2 },
});
