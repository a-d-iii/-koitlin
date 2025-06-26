import React, { useRef, useState, useEffect } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Animated,
  useWindowDimensions,
  LayoutAnimation,
  Pressable,
  UIManager,
  Platform,
  StatusBar,
  PanResponder,
  GestureResponderEvent,
  PanResponderGestureState,
} from 'react-native';
import { WEEKLY_SCHEDULE, ClassEntry } from '../data/weeklySchedule';

const DAYS = Object.keys(WEEKLY_SCHEDULE);

const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

export default function Planner() {
  const [index, setIndex] = useState<number>(0);
  const day = DAYS[index];
  const classes: ClassEntry[] = WEEKLY_SCHEDULE[day];

  const { width: SCREEN_WIDTH } = useWindowDimensions();

  const slideAnim = useRef(new Animated.Value(0)).current;
  const dayScales = useRef(DAYS.map(() => new Animated.Value(1))).current;
  const pressScales = useRef<Animated.Value[]>(classes.map(() => new Animated.Value(1)));
  const prevIndex = useRef(index);

  useEffect(() => {
    if (Platform.OS === 'android' && UIManager.setLayoutAnimationEnabledExperimental) {
      UIManager.setLayoutAnimationEnabledExperimental(true);
    }
  }, []);

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

  useEffect(() => {
    const direction = index >= prevIndex.current ? -1 : 1;
    slideAnim.setValue(direction * SCREEN_WIDTH);
    Animated.spring(slideAnim, {
      toValue: 0,
      useNativeDriver: true,
    }).start();

    dayScales.forEach((v, i) => {
      Animated.spring(v, {
        toValue: i === index ? 1.1 : 1,
        useNativeDriver: true,
      }).start();
    });

    pressScales.current = classes.map(() => new Animated.Value(1));

    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    prevIndex.current = index;
  }, [index, SCREEN_WIDTH, slideAnim, dayScales, classes]);

  return (
    <SafeAreaView
      style={[
        styles.container,
        { paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight : 0 },
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
            activeOpacity={0.8}
          >
            <Animated.View
              style={[
                styles.dayButton,
                day === d && styles.dayButtonActive,
                { transform: [{ scale: dayScales[i] }] },
              ]}
            >
              <Text
                style={[styles.dayText, day === d && styles.dayTextActive]}
              >
                {d.slice(0, 3)}
              </Text>
            </Animated.View>
          </TouchableOpacity>
        ))}
      </ScrollView>

      <ScrollView
        contentContainerStyle={styles.scheduleContainer}
        {...pan.panHandlers}
      >
        <Animated.View style={{ width: SCREEN_WIDTH, transform: [{ translateX: slideAnim }] }}>
          {classes.map((cls, idx) => (
            <AnimatedPressable
              key={idx}
              onPressIn={() =>
                Animated.spring(pressScales.current[idx], {
                  toValue: 0.97,
                  useNativeDriver: true,
                }).start()
              }
              onPressOut={() =>
                Animated.spring(pressScales.current[idx], {
                  toValue: 1,
                  useNativeDriver: true,
                }).start()
              }
              style={{ transform: [{ scale: pressScales.current[idx] }] }}
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
            </AnimatedPressable>
          ))}
        </Animated.View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  heading: {
    fontSize: 22,
    fontWeight: '700',
    marginVertical: 16,
    textAlign: 'center',
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
    paddingVertical: 10,
    paddingHorizontal: 12,
    marginVertical: 6,
    elevation: 1,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 0.5 },
    shadowRadius: 2,
  },
  classLeft: { flex: 1 },
  courseText: { fontSize: 14, fontWeight: '600', color: '#333' },
  facultyText: { fontSize: 12, color: '#666', marginTop: 2 },
  classRight: { justifyContent: 'space-between', alignItems: 'flex-end' },
  timeText: { fontSize: 12, color: '#333' },
  roomText: { fontSize: 10, color: '#666', marginTop: 2 },
});
