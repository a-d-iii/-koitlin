import React, { useEffect, useState, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Pressable,
 
  Animated,
 
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { LinearGradient } from 'expo-linear-gradient';
import Ionicons from '@expo/vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import type { Meal } from '../data/meals';
import { useNavigation } from '@react-navigation/native';
// Load the bundled menu as an offline fallback so the screen always has data
// even when network requests fail.
import localMenu from '../../monthly-menu-may-2025.json';
import fetchWithTimeout from '../utils/fetchWithTimeout';

const MENU_URL =
  'https://raw.githubusercontent.com/a-d-iii/app/main/monthly-menu-may-2025.json';

interface MonthlyMenu {
  [date: string]: Meal[];
}

export default function FoodMenuScreen() {
  // Start with the bundled menu so something is shown immediately
  const [menu, setMenu] = useState<MonthlyMenu>(localMenu as MonthlyMenu);
  const [loading, setLoading] = useState(true);
  const [likes, setLikes] = useState<Record<string, boolean>>({});
  const [currentTime, setCurrentTime] = useState(new Date());
  const navigation = useNavigation();
  const iconAnim = useRef(new Animated.Value(0)).current;
  const topIconAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(iconAnim, {
          toValue: 1,
          duration: 800,
          useNativeDriver: true,
        }),
        Animated.timing(iconAnim, {
          toValue: 0,
          duration: 800,
          useNativeDriver: true,
        }),
      ])
    ).start();
    Animated.loop(
      Animated.sequence([
        Animated.timing(topIconAnim, {
          toValue: 1,
          duration: 1000,
          useNativeDriver: true,
        }),
        Animated.timing(topIconAnim, {
          toValue: 0,
          duration: 1000,
          useNativeDriver: true,
        }),
      ])
    ).start();
  }, []);

  // Update current time every second so status labels refresh
  useEffect(() => {
    const id = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(id);
  }, []);

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

  const today = new Date();
  const todayKey = today.toISOString().slice(0, 10);
  const dayLabel = today.toLocaleDateString(undefined, {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
  });
  const meals = menu?.[todayKey];
  const mealColors = ['#ffffff', '#ffffff', '#ffffff', '#ffffff'];

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  if (!meals) {
    return (
      <View style={styles.centered}>
        <Text style={styles.message}>No menu found for today.</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={() => navigation.navigate('MonthlyMenuScreen' as never)}
        >
          <Text style={styles.buttonText}>View Full Month</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const toggleLike = (name: string) => {
    setLikes(prev => ({ ...prev, [name]: !prev[name] }));
  };

  const formatTime = (hour: number, minute: number) => {
    const d = new Date();
    d.setHours(hour, minute, 0, 0);
    return d.toLocaleTimeString(undefined, {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });
  };

  const mealStatus = (m: Meal) => {
    const start = new Date(today);
    start.setHours(m.startHour, m.startMinute, 0, 0);
    const end = new Date(today);
    end.setHours(m.endHour, m.endMinute, 0, 0);

    if (currentTime >= end) {
      return { icon: 'checkmark-circle', text: 'Done', ended: true } as const;
    }
    if (currentTime >= start) {
      return { icon: 'flame', text: 'Ongoing', ended: false } as const;
    }
    return {
      icon: 'time-outline',
      text: `Starts at ${formatTime(m.startHour, m.startMinute)}`,
      ended: false,
    } as const;
  };

  const mealIcon = (name: string) => {
    switch (name.toLowerCase()) {
      case 'breakfast':
        return 'sunny';
      case 'lunch':
        return 'fish';
      case 'snacks':
        return 'ice-cream';
      case 'dinner':
        return 'moon';
      default:
        return 'fast-food';
    }
  };

  const showSummary = () => {
    navigation.navigate('FoodSummaryScreen' as never);
  };

  return (
    <LinearGradient colors={['#69cbff', '#1cddfe']} style={{ flex: 1 }}>
    <SafeAreaView style={styles.safe}>
      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.titleRow}>
          <Text style={styles.heading}>Today's Menu</Text>
          <Animated.View
            style={[
              styles.topIcon,
              {
                transform: [
                  {
                    scale: topIconAnim.interpolate({
 
                      inputRange: [0, 1],
                      outputRange: [1, 1.15],
                    }),
                  },
                  // keep the icon scale animation but avoid rotation/offset
                  {
                    translateY: topIconAnim.interpolate({
                      inputRange: [0, 1],
                      outputRange: [0, 0],
                    }),
                  },
                ],
              },
            ]}
          >
          <Ionicons name="restaurant" size={24} color="#ff9800" />
          </Animated.View>
        </View>
        <View style={styles.dateChip}>
          <Text style={styles.dateChipText}>{dayLabel}</Text>
        </View>
        {meals.map((m, idx) => {
          const status = mealStatus(m);
          return (
            <View
              key={m.name}
              style={[
                styles.mealBlock,
                { backgroundColor: mealColors[idx % mealColors.length] },
                // Keep original card color even after the meal ends
                // status.ended && styles.mealBlockEnded,
              ]}
            >
              <View style={styles.mealHeader}>
                <View style={styles.mealTitleRow}>
                  <Animated.View
                    style={{
                  transform: [
                        {
                          scale: iconAnim.interpolate({
                            inputRange: [0, 1],
                            outputRange: [1, 1.2],
                          }),
                        }, 
                        {
                          rotate: iconAnim.interpolate({
                            inputRange: [0, 1],
                            outputRange: ['0deg', '8deg'],
                          }),
                        },
 
                        {
                          rotate: iconAnim.interpolate({
                            inputRange: [0, 1],
                            outputRange: ['0deg', '8deg'],
                          }),
                        },
                      ],
                    }}
                  >
                    <Ionicons
                      name={mealIcon(m.name)}
                      size={16}
                      color="#ff9800"
                      style={styles.mealIcon}
                    />
                  </Animated.View>
                  <Text style={styles.mealTitle}>{m.name}</Text>
                  <Text style={styles.timing}>{`${formatTime(
                    m.startHour,
                    m.startMinute
                  )} - ${formatTime(m.endHour, m.endMinute)}`}</Text>
                </View>
                <View style={styles.mealHeaderRight}>
                  <View style={styles.statusRow}>
                    <Ionicons
                      name={status.icon as any}
                      size={16}
                      color={status.ended ? 'green' : '#d00'}
                    />
                    <Text style={styles.statusText}>{status.text}</Text>
                  </View>
                  <Pressable onPress={() => toggleLike(m.name)} style={styles.likeBtn}>
                    <Ionicons
                      name={likes[m.name] ? 'heart' : 'heart-outline'}
                      size={20}
                      color={likes[m.name] ? 'red' : 'black'}
                    />
                  </Pressable>
                </View>
              </View>
              <Text style={styles.mealItems}>{m.items.join(', ')}</Text>
              {/* Rate button removed */}
            </View>
          );
        })}
        <TouchableOpacity style={styles.button} onPress={showSummary}>
          <Ionicons name="fast-food" size={16} color="#fff" style={{ marginRight: 6 }} />
          <Text style={styles.buttonText}>Food Summary</Text>
        </TouchableOpacity>
      </ScrollView>
      {/* Rating modal removed */}
      <View style={styles.monthBar}>
        <TouchableOpacity
          style={styles.monthButton}
          onPress={() => navigation.navigate('MonthlyMenuScreen' as never)}
        >
          <Ionicons name="calendar" size={16} color="#fff" />
          <Text style={styles.monthButtonText}>View Full Month</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  container: {
    padding: 16,
 
    paddingBottom: 58,
 
  },
  heading: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 4,
    textAlign: 'center',
  },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  dateChip: {
    alignSelf: 'center',
    backgroundColor: '#ffffff',
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: 20,
    marginBottom: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#ccc',
  },
  dateChipText: {
    color: '#333',
    fontWeight: '600',
  },
  mealBlock: {
    backgroundColor: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 20,
    borderRadius: 8,
    marginBottom: 16,
    elevation: 0,
  },
  mealHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  mealTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  mealIcon: {
    marginRight: 6,
  },
  mealTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    backgroundColor: '#ffffff',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: '#ccc',
  },
  mealHeaderRight: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  monthBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,

    backgroundColor: '#69cbff',

    borderTopColor: '#ddd',
    borderTopWidth: StyleSheet.hairlineWidth,
    paddingVertical: 8,
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'center',
  },
  monthButton: {
    flexDirection: 'row',

    paddingHorizontal: 16,
    paddingVertical: 8,
    alignItems: 'center',
  },
  monthButtonText: {
    color: '#fff',
    fontWeight: '600',
    marginLeft: 6,
  },
  statusRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusText: {
    marginLeft: 4,
    color: '#333',
    fontSize: 12, 
  },
  likeBtn: {
    marginLeft: 8,
  }, 
  button: {
    marginTop: 20,
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#69cbff',
    alignItems: 'center',
    flexDirection: 'row',
    alignSelf: 'center',
  },
  buttonText: {
    color: '#fff',
    fontWeight: '600',
  },
  message: {
    fontSize: 16,
    marginBottom: 12,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 4,
  },
  topIcon: {
    marginLeft: 8,
  },
  timing: {
    color: '#333',
    marginLeft: 8,
  },
});
