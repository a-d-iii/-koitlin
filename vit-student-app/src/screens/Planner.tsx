import React, { useRef, useState, useEffect } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Platform,
  StatusBar,
  Animated,
  LayoutAnimation,
  Dimensions,
  UIManager,
  Pressable,
  PanResponder,
  GestureResponderEvent,
  PanResponderGestureState,
} from 'react-native';
import { WEEKLY_SCHEDULE, ClassEntry } from '../data/weeklySchedule';

const DAYS = Object.keys(WEEKLY_SCHEDULE);

const { width: SCREEN_WIDTH } = Dimensions.get('window');

const AnimatedScrollView = Animated.createAnimatedComponent(ScrollView);

export default function Planner() {
  const [index, setIndex] = useState<number>(0);
  const day = DAYS[index];
  const classes: ClassEntry[] = WEEKLY_SCHEDULE[day];

  const slideAnim = useRef(new Animated.Value(0)).current;
  const dayScale = useRef(new Animated.Value(1)).current;
  const prevIndex = useRef(index);

  useEffect(() => {
    if (
      Platform.OS === 'android' &&
      UIManager.setLayoutAnimationEnabledExperimental
    ) {
      UIManager.setLayoutAnimationEnabledExperimental(true);
    }
  }, []);

  useEffect(() => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    const dir = index > prevIndex.current ? 1 : -1;
    slideAnim.setValue(dir * SCREEN_WIDTH);
    Animated.timing(slideAnim, {
      toValue: 0,
      duration: 250,
      useNativeDriver: true,
    }).start();
    Animated.sequence([
      Animated.timing(dayScale, {
        toValue: 1.15,
        duration: 150,
        useNativeDriver: true,
      }),
      Animated.spring(dayScale, {
        toValue: 1,
        friction: 3,
        useNativeDriver: true,
      }),
    ]).start();
    prevIndex.current = index;
  }, [index]);

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
            style={styles.dayButtonWrapper}
          >
            <Animated.View
              style={[
                styles.dayButton,
                day === d && styles.dayButtonActive,
                day === d && { transform: [{ scale: dayScale }] },
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

      <AnimatedScrollView
        contentContainerStyle={styles.scheduleContainer}
        {...pan.panHandlers}
        style={{ transform: [{ translateX: slideAnim }] }}
      >
        {classes.map((cls, idx) => (
          <Pressable key={idx} style={({ pressed }) => [styles.classBox, pressed && styles.classBoxPressed]}>
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
          </Pressable>
        ))}
      </AnimatedScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  heading: {
    fontSize: 22,
    fontWeight: '700',
    marginVertical: 16,
    marginHorizontal: 16,
    textAlign: 'center',
    color: '#333',
  },
  dayRow: {
    flexGrow: 0,
  },
  dayButtonWrapper: { marginRight: 8 },
  dayButton: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 20,
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
  classBoxPressed: { opacity: 0.7 },
  classLeft: { flex: 1 },
  courseText: { fontSize: 14, fontWeight: '600', color: '#333' },
  facultyText: { fontSize: 12, color: '#666', marginTop: 2 },
  classRight: { justifyContent: 'space-between', alignItems: 'flex-end' },
  timeText: { fontSize: 12, color: '#333' },
  roomText: { fontSize: 10, color: '#666', marginTop: 2 },
});
