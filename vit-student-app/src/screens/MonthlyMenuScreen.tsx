import React, { useEffect, useState, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  SectionList,
  ActivityIndicator,
  Pressable,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { LinearGradient } from 'expo-linear-gradient';
import Ionicons from '@expo/vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import type { Meal } from '../data/meals';

// Fallback data bundled with the app for offline use
import localMenu from '../../monthly-menu-may-2025.json';
import fetchWithTimeout from '../utils/fetchWithTimeout';



const MENU_URL = 'https://raw.githubusercontent.com/a-d-iii/app/main/monthly-menu-may-2025.json';

const WEEK_COLORS = ['#f0e4d7', '#e7f0d7', '#d7e8f0', '#f0d7e8'];
const DAY_COLORS = ['#e5d7cb', '#dce5cb', '#cbdce5', '#e5cbdc'];


interface MonthlyMenu {
  [date: string]: Meal[];
}

type WeekSection = {
  title: string;
  color: string;
  dayColor: string;
  index: number;
  data: { date: string; meals: Meal[] }[];
};

export default function MonthlyMenuScreen() {

  // Use the bundled data initially so the monthly view works offline
  const [menu, setMenu] = useState<MonthlyMenu>(localMenu as MonthlyMenu);


  const [loading, setLoading] = useState(true);
  const [likes, setLikes] = useState<Record<string, boolean>>({});
  const [weekIndex, setWeekIndex] = useState(0);
  const listRef = useRef<SectionList<any>>(null);
  const scrollTarget = useRef<{ sectionIndex: number; itemIndex: number }>();

  useEffect(() => {
    const loadMenu = async () => {
      try {
        const cached = await AsyncStorage.getItem('monthlyMenu');
        if (cached) {
          setMenu(JSON.parse(cached));
        }
        const resp = await fetchWithTimeout(MENU_URL, {}, 5000);
        if (resp.ok) {
          const json = await resp.json();
          setMenu(json);
          await AsyncStorage.setItem('monthlyMenu', JSON.stringify(json));
        }
      } catch (e) {
        console.error('Failed to load menu', e);
      } finally {
        setLoading(false);
      }
    };
    loadMenu();
  }, []);

  // Scroll to current day once data is loaded
  useEffect(() => {
    if (loading) return;
    const todayKey = new Date().toISOString().slice(0, 10);
    const dates = Object.keys(menu).sort();
    const idx = dates.indexOf(todayKey);
    if (idx >= 0) {
      scrollTarget.current = {
        sectionIndex: Math.floor(idx / 7),
        itemIndex: idx % 7,
      };
      setTimeout(() => {
        if (scrollTarget.current) {
          listRef.current?.scrollToLocation({
            ...scrollTarget.current,
            animated: false,
            viewPosition: 0,
          });
        }
      }, 0);
    }
  }, [loading, menu]);

  const handleScrollToIndexFailed = () => {
    if (!scrollTarget.current) return;
    setTimeout(() => {
      listRef.current?.scrollToLocation({
        ...scrollTarget.current!,
        animated: false,
        viewPosition: 0,
      });
    }, 50);
  };

  const viewabilityConfig = useRef({ itemVisiblePercentThreshold: 50 }).current;
  const onViewableItemsChanged = useRef(({ viewableItems }: any) => {
    const first = viewableItems.find(
      (v: any) => v.section && typeof v.section.index === 'number'
    );
    if (first) {
      setWeekIndex(first.section.index);
    }
  }).current;

  const toWeeks = (): WeekSection[] => {
    if (!menu) return [];

    const dates = Object.keys(menu).sort();
    const weeks: WeekSection[] = [];
    for (let i = 0; i < dates.length; i += 7) {
      const slice = dates.slice(i, i + 7);
      const index = weeks.length;
      weeks.push({
        title: `Week ${index + 1}`,
        color: WEEK_COLORS[index % WEEK_COLORS.length],
        dayColor: DAY_COLORS[index % DAY_COLORS.length],
        index,
        data: slice.map((d) => ({ date: d, meals: menu[d] })),
      });
    }
    return weeks;
  };

  const today = new Date().toISOString().slice(0, 10);

  const toggleLike = (key: string) =>
    setLikes((prev) => ({ ...prev, [key]: !prev[key] }));

  const renderDay = (
    { date, meals }: { date: string; meals: Meal[] },
    index: number,
    section: WeekSection
  ) => {
    const isPast = date < today;
    return (
      <View
        style={[
          styles.dayBlock,
          { backgroundColor: section.dayColor },
          isPast && styles.pastDay,
          index === 0 && styles.firstDay,
          index === section.data.length - 1 && styles.lastDay,
        ]}
      >
        <View style={styles.dateChip}>
          <Text style={styles.dateText}>{date}</Text>
        </View>
        {meals.map((m) => {
          const key = `${date}-${m.name}`;
          return (
            <View key={key} style={styles.mealItem}>
              <View style={styles.mealHeader}>
                <Text style={styles.mealTitle}>{m.name}</Text>
                <View style={styles.mealActions}>
                  <Pressable
                    onPress={() => toggleLike(key)}
                    style={styles.iconButton}
                  >
                    <Ionicons
                      name={likes[key] ? 'heart' : 'heart-outline'}
                      size={16}
                      color={likes[key] ? 'red' : '#333'}
                    />
                  </Pressable>
                  <Pressable style={styles.iconButton}>
                    <Ionicons name="add" size={16} color="#333" />
                  </Pressable>
                </View>
              </View>
              <Text style={styles.mealItems}>{m.items.join(', ')}</Text>
            </View>
          );
        })}
      </View>
    );
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  const weeks = toWeeks();
  return (
    <LinearGradient colors={['#69cbff', '#1cddfe']} style={{ flex: 1 }}>
    <SafeAreaView style={[styles.safe, { backgroundColor: 'transparent' }]}>
      <SectionList
        ref={listRef}
        sections={weeks}
        keyExtractor={(item) => item.date}
        renderItem={({ item, index, section }) =>
          renderDay(item, index, section as WeekSection)
        }
        renderSectionHeader={({ section }) => (
          <View style={styles.weekHeaderContainer}>
            <View style={[styles.weekLabel, { backgroundColor: section.dayColor }]}>
              <Text style={styles.sectionHeader}>{section.title}</Text>
            </View>
          </View>
        )}
        onScrollToIndexFailed={handleScrollToIndexFailed}
        onViewableItemsChanged={onViewableItemsChanged}
        viewabilityConfig={viewabilityConfig}
        SectionSeparatorComponent={() => <View style={{ height: 12 }} />}
        stickySectionHeadersEnabled
        contentContainerStyle={styles.listContent}
      />
    </SafeAreaView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#f2f2f2' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  listContent: { padding: 12 },
  weekHeaderContainer: {
    marginHorizontal: 4,
    marginBottom: 4,
    padding: 8,
    alignItems: 'center',
  },
  weekLabel: {
    backgroundColor: '#000',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 50,
  },
  sectionHeader: {
    fontWeight: '600',
    fontSize: 26,
    color: '#fff',
  },
  dayBlock: {
    padding: 12,
    marginHorizontal: 4,
    borderBottomWidth: 1,
    borderBottomColor: '#555',
    borderStyle: 'dotted',
  },
  firstDay: { borderTopLeftRadius: 12, borderTopRightRadius: 12, marginTop: 4 },
  lastDay: {
    borderBottomLeftRadius: 12,
    borderBottomRightRadius: 12,
    marginBottom: 8,
  },
  dateChip: {
    alignSelf: 'flex-start',
    backgroundColor: '#c0c0c0',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 20,
    marginBottom: 6,
  },
  dateText: { fontWeight: '600' },
  mealItem: { marginBottom: 8 },
  mealHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 2,
  },
  mealActions: { flexDirection: 'row' },
  iconButton: { marginLeft: 8 },
  mealTitle: { fontWeight: '600' },
  mealItems: { color: '#555' },
  pastDay: { opacity: 0.5 },
});
