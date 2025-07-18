// src/components/SummaryCard.tsx

import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  SafeAreaView,
  Animated,
  TouchableOpacity,
  StatusBar,
  Platform,
  ScrollView,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useNavigation } from '@react-navigation/native';
import { useUser } from '../context/UserContext';

const { width, height } = Dimensions.get('window');
const STATUS_BAR_HEIGHT = Platform.OS === 'android'
  ? StatusBar.currentHeight || 0
  : 0;

// Gradient card size
const GRADIENT_RATIO = 0.16;
const GRADIENT_HEIGHT = height * GRADIENT_RATIO + STATUS_BAR_HEIGHT;
const GRADIENT_WIDTH = width * 0.9;

// Menu card sizing
const CONTENT_PADDING = 24;
const CARD_WIDTH = width * 0.42;
const CARD_HEIGHT = 120;
const MENU_SPACING = (width - CARD_WIDTH * 2) / 3;
const MENU_GRID_PADDING = Math.max(MENU_SPACING - CONTENT_PADDING, 0);

const AnimatedLinearGradient = Animated.createAnimatedComponent(LinearGradient);
const AnimatedScrollView     = Animated.createAnimatedComponent(ScrollView);

// Data types
type ClassItem   = { course:string; faculty:string; start:string; end:string; room:string };
type MealItem    = { key:string; icon:string; label:string; menu:string; start:string };
type TaskItem    = { id:number; title:string; details:string; priority:'high'|'medium'|'low'; done:boolean };
type InsightItem = { id:number; title:string; subtitle:string; value:string; color:string };

// Meal & Insight data
const mealItems: MealItem[] = [
  { key:'breakfast', icon:'cafe-outline',    label:'Breakfast', menu:'Pancakes & Juice', start:'08:00' },
  { key:'lunch',     icon:'restaurant-outline',label:'Lunch',   menu:'Chicken Salad',    start:'12:30' },
  { key:'snacks',    icon:'ice-cream-outline',  label:'Snacks',  menu:'Fruit & Yogurt',   start:'16:00' },
  { key:'dinner',    icon:'moon-outline',       label:'Dinner',  menu:'Salmon & Veggies', start:'19:00' },
];
const insightItems: InsightItem[] = [
  { id:1, title:'Water Intake',    subtitle:'Great, 2 of 3 L done', value:'2 / 3 L', color:'#00B894' },
  { id:2, title:'Steps',           subtitle:'7,500 steps today',     value:'7.5 k',   color:'#6C5CE7' },
  { id:3, title:'Free Time',       subtitle:'Break 3:00–4:30 PM',    value:'1.5 h',   color:'#FF6B6B' },
  { id:4, title:'Tasks Completed', subtitle:'4 of 6 tasks done',     value:'4 / 6',   color:'#FFD93D' },
];

// Hue cycle presets
const HUE_PRESETS = [210, 120, 0, 60, 300];

export default function SummaryCard() {
  // Page background HSL
  const [hueIndex, setHueIndex] = useState(0);
  const [lightness, setLightness] = useState(95);
  const hue = HUE_PRESETS[hueIndex];
  const pageBg = `hsl(${hue},30%,${lightness}%)`;

  // Greeting header background HSL
  const [headerHueIndex, setHeaderHueIndex] = useState(0);
  const [headerLightness, setHeaderLightness] = useState(100);
  const headerHue = HUE_PRESETS[headerHueIndex];
  const headerBg = `hsl(${headerHue},30%,${headerLightness}%)`;

  const navigation = useNavigation();
  const { icon } = useUser();

  // Tasks state
  const [tasks, setTasks] = useState<TaskItem[]>([
    { id:1, title:'Finish report',    details:'Complete Q3 analysis', priority:'high',   done:false },
    { id:2, title:'Grocery shopping', details:'Buy veggies and milk',  priority:'medium', done:false },
    { id:3, title:'Call plumber',     details:'Fix kitchen sink leak',  priority:'low',    done:false },
  ]);

  // Scroll value (for future use)
  const scrollY = useRef(new Animated.Value(0)).current;

  // Card gradient animation
  const colorAnim = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    Animated.loop(Animated.timing(colorAnim,{
      toValue:1,duration:10000,useNativeDriver:false
    })).start();
  }, []);
  const bgStart = colorAnim.interpolate({
    inputRange:[0,0.5,1],
    outputRange:['#FFB366','#769BE0','#FFB366'],
  });
  const bgEnd = colorAnim.interpolate({
    inputRange:[0,0.5,1],
    outputRange:['#A060E0','#7D6EB0','#A060E0'],
  });

  // Dummy schedule
  const daySchedule: ClassItem[] = [
    { course:'DSA',        faculty:'Dr. Smith', start:'08:00', end:'09:00', room:'301' },
    { course:'Algorithms', faculty:'Prof. Lee',  start:'09:15', end:'10:15', room:'204' },
    { course:'Networks',   faculty:'Dr. Patel',  start:'10:30', end:'11:30', room:'Lab 2' },
  ];

  // Animations for timetable boxes
  const classAnims = useRef(daySchedule.map(() => new Animated.Value(0))).current;

  useEffect(() => {
    classAnims.forEach((anim, i) => {
      Animated.timing(anim, {
        toValue: 1,
        duration: 400,
        delay: i * 100,
        useNativeDriver: true,
      }).start();
    });
  }, [classAnims]);

  const toggleTask = (id:number) =>
    setTasks(prev => prev.map(t=> t.id===id?{...t,done:!t.done}:t));

  const priorityStyles = {
    high:   { bg:'#FFE5E5', border:'#FF6B6B', text:'#FF6B6B' },
    medium: { bg:'#FFF9E5', border:'#FFD93D', text:'#FFD93D' },
    low:    { bg:'#E6F9E5', border:'#6BCB77', text:'#6BCB77' },
  };

  // Color control handlers
  const cycleHue = () => setHueIndex(i=>(i+1)%HUE_PRESETS.length);
  const lighten = () => setLightness(l=>Math.min(l+5,100));
  const darken  = () => setLightness(l=>Math.max(l-5,0));

  // Greeting header color controls
  const cycleHeaderHue = () => setHeaderHueIndex(i=>(i+1)%HUE_PRESETS.length);
  const lightenHeader = () => setHeaderLightness(l=>Math.min(l+5,100));
  const darkenHeader  = () => setHeaderLightness(l=>Math.max(l-5,0));

  return (
    <SafeAreaView style={[styles.safe, { backgroundColor: pageBg }]}>
      <StatusBar translucent backgroundColor="transparent" barStyle="dark-content"/>

      {/* Greeting Header */}
      <View style={[styles.staticHeader,{ backgroundColor: headerBg }]}>
        <TouchableOpacity
          style={styles.profileButton}
          onPress={() => navigation.navigate('Profile')}
        >
          <View style={styles.profileCircle}>
            <Ionicons name={icon as any} size={32} color="#333" />
          </View>
        </TouchableOpacity>
        <View style={styles.headerTextContainer}>
          <Text style={styles.greetingText}>Hello, Adithyaa</Text>
          <Text style={styles.dateText}>Saturday, June 7, 2025</Text>
        </View>
        {/* Color controls */}
        <View style={styles.colorControls}>
          {/* Page controls */}
          <TouchableOpacity onPress={cycleHue} style={styles.controlButton}>
            <Text style={styles.controlText}>🎨</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={lighten} style={styles.controlButton}>
            <Text style={styles.controlText}>☀️</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={darken} style={styles.controlButton}>
            <Text style={styles.controlText}>🌙</Text>
          </TouchableOpacity>
          <View style={styles.groupSpacer}/>
          {/* Greeting controls */}
          <TouchableOpacity onPress={cycleHeaderHue} style={styles.controlButton}>
            <Text style={styles.controlText}>🎨</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={lightenHeader} style={styles.controlButton}>
            <Text style={styles.controlText}>☀️</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={darkenHeader} style={styles.controlButton}>
            <Text style={styles.controlText}>🌙</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Scrollable Content */}
      <AnimatedScrollView
        contentContainerStyle={styles.content}
        scrollEventThrottle={16}
        showsVerticalScrollIndicator={false}
      >
        {/* Weather Gradient Card */}
        <View style={styles.gradientBox}>
          <AnimatedLinearGradient
            colors={[bgStart, bgEnd]}
            start={[0,0]} end={[1,1]}
            style={[StyleSheet.absoluteFill, styles.gradientBg]}
          />
          <View style={styles.topRow}>
            <View style={styles.iconDesc}>
              <Ionicons name="cloudy" size={64} color="#fff" />
              <Text style={styles.descText}>Cloudy</Text>
            </View>
            <View style={styles.tempContainer}>
              <Text style={styles.tempText}>25°</Text>
              <Text style={styles.feelsText}>Feels like 27°</Text>
            </View>
          </View>
          <View style={styles.bottomRow}>
            <View style={styles.infoBox}>
              <Text style={styles.infoValue}>60%</Text>
              <Text style={styles.infoLabel}>Humidity</Text>
            </View>
            <View style={styles.infoBox}>
              <Text style={styles.infoValue}>10 km/h</Text>
              <Text style={styles.infoLabel}>Wind Sp</Text>
            </View>
            <View style={styles.infoBox}>
              <Text style={styles.infoValue}>28°</Text>
              <Text style={styles.infoLabel}>Max</Text>
            </View>
          </View>
        </View>

        {/* Today's Timetable */}
        <View style={styles.sectionHeader}>
          <View style={styles.headerLeft}>
            <Ionicons name="time-outline" size={20} color="#333"/>
            <Text style={styles.sectionTitle}>Today's Timetable</Text>
          </View>
          <TouchableOpacity>
            <Text style={styles.viewAll}>View All</Text>
          </TouchableOpacity>
        </View>
        {daySchedule.map((cls, idx) => {
          const [h1,m1] = cls.start.split(':').map(Number);
          const [h2,m2] = cls.end.split(':').map(Number);
          const s = new Date(); s.setHours(h1,m1);
          const e = new Date(); e.setHours(h2,m2);
          const hrs = ((e.getTime()-s.getTime())/3600000).toFixed(1);
          const tstr = s.toLocaleTimeString('it-IT',{hour:'2-digit',minute:'2-digit'});
          return (
            <Animated.View
              key={idx}
              style={[
                styles.classBox,
                {
                  opacity: classAnims[idx],
                  transform: [
                    {
                      translateY: classAnims[idx].interpolate({
                        inputRange: [0, 1],
                        outputRange: [20, 0],
                      }),
                    },
                  ],
                },
              ]}
            >
              <View style={styles.classLeft}>
                <Text style={styles.courseText}>{cls.course}</Text>
                <Text style={styles.facultyText}>{cls.faculty}</Text>
                <Text style={styles.timeText}>{tstr}</Text>
              </View>
              <View style={styles.classRight}>
                <Text style={styles.hoursText}>{hrs}h</Text>
                <Text style={styles.roomText}>{cls.room}</Text>
              </View>
            </Animated.View>
          );
        })}

        {/* Today's Menu */}
        <View style={[styles.sectionHeader,{marginTop:24}]}>
          <View style={styles.headerLeft}>
            <Ionicons name="fast-food-outline" size={20} color="#333"/>
            <Text style={styles.sectionTitle}>Today's Menu</Text>
          </View>
          <TouchableOpacity>
            <Text style={styles.viewAll}>View All</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.menuGrid}>
          {mealItems.map(mi => (
            <View key={mi.key} style={styles.menuBox}>
              <Ionicons name={mi.icon as any} size={24} color="#FF6A00"/>
              <Text style={styles.menuLabel}>{mi.label}</Text>
              <Text style={styles.menuText}>{mi.menu}</Text>
              <Text style={styles.menuTime}>{mi.start}</Text>
            </View>
          ))}
        </View>

        {/* Tasks & Reminders */}
        <View style={[styles.sectionHeader,{marginTop:24}]}>
          <View style={styles.headerLeft}>
            <Ionicons name="checkmark-done-outline" size={20} color="#333"/>
            <Text style={styles.sectionTitle}>Tasks & Reminders</Text>
          </View>
          <TouchableOpacity>
            <Text style={styles.viewAll}>View All</Text>
          </TouchableOpacity>
        </View>
        {tasks.map(t => {
          const ps = priorityStyles[t.priority];
          return (
            <View key={t.id} style={[styles.taskBox,{ backgroundColor: ps.bg, borderLeftColor: ps.border }]}>
              <TouchableOpacity onPress={() => toggleTask(t.id)} style={styles.taskCheck}>
                <Ionicons
                  name={t.done ? 'checkbox-outline' : 'square-outline'}
                  size={20}
                  color={ps.border}
                />
              </TouchableOpacity>
              <View style={styles.taskContent}>
                <Text style={[
                  styles.taskTitle,
                  t.done && { textDecorationLine:'line-through', color:'#999' }
                ]}>
                  {t.title}
                </Text>
                <Text style={styles.taskDetails}>{t.details}</Text>
                <View style={[styles.priorityBadge,{ borderColor:ps.border }]}>
                  <Text style={[styles.priorityText,{ color:ps.text }]}>
                    {t.priority.charAt(0).toUpperCase() + t.priority.slice(1)} Priority
                  </Text>
                </View>
              </View>
            </View>
          );
        })}

        {/* Daily Insights */}
        <View style={[styles.insightsContainer,{marginTop:24}]}>
          <View style={styles.sectionHeader}>
            <View style={styles.headerLeft}>
              <Ionicons name="bar-chart-outline" size={20} color="#333"/>
              <Text style={styles.sectionTitle}>Daily Insights</Text>
            </View>
            <TouchableOpacity>
              <Text style={styles.viewAll}>View All</Text>
            </TouchableOpacity>
          </View>
          {insightItems.map(item => (
            <View key={item.id} style={[styles.insightBox,{ borderColor:item.color }]}>
              <View style={[styles.insightSquare,{ backgroundColor:item.color }]}/>
              <View style={styles.insightContent}>
                <Text style={styles.insightTitle}>{item.title}</Text>
                <Text style={styles.insightSubtitle}>{item.subtitle}</Text>
              </View>
              <Text style={styles.insightValue}>{item.value}</Text>
            </View>
          ))}
        </View>
      </AnimatedScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex:1 },

  staticHeader: {
    backgroundColor: '#fff',
    paddingTop: STATUS_BAR_HEIGHT + 12,
    paddingHorizontal:24,
    paddingBottom:12,
  },
  profileButton: {
    position:'absolute',
    top: STATUS_BAR_HEIGHT,
    right:16,
    width:56,
    height:56,
    borderRadius:28,
    overflow:'hidden',
  },
  profileCircle: { flex:1, backgroundColor:'#ccc', justifyContent:'center', alignItems:'center' },
  headerTextContainer: { marginLeft:0 },
  greetingText: { color:'#333', fontSize:26, fontWeight:'700' },
  dateText:   { color:'#666', fontSize:12, fontWeight:'700', marginTop:4 },

  colorControls: { flexDirection:'row', position:'absolute', bottom:12, right:24 },
  groupSpacer:{ width:12 },
  controlButton: { marginLeft:12 },
  controlText:   { fontSize:18 },

  content: { padding:24, paddingTop:0 },

  gradientBox: {
    height: GRADIENT_HEIGHT,
    width: GRADIENT_WIDTH,
    alignSelf:'center',
    marginTop:12,
    marginBottom:24,
    borderRadius:50,
    padding:16,
    // allow content to shift freely
    justifyContent:'flex-start',
    // floating shadow
    elevation: 0,
  },
  gradientBg: {
    borderRadius:50,
  },
  topRow:        { flexDirection:'row', justifyContent:'space-between', alignItems:'center' },
  iconDesc:      { flexDirection:'row', alignItems:'center' },
  descText:      {
    color:'#fff',
    fontSize:32,
    marginLeft:12,
    fontWeight:'700',
    // allow caller to control vertical placement
    marginTop:0,
  },
  tempContainer: {
    alignItems:'flex-end',
    // let temperature position be customized
    marginTop:0,
  },
  tempText:  { color:'#fff', fontSize:52, fontWeight:'700' },
  feelsText: {
    color:'#fff',
    fontSize:12,
    fontWeight:'700',
    // no default offset
    marginTop:0,
  },
  // Removed fixed translation offsets so the text can be freely positioned

  bottomRow:     { flexDirection:'row', justifyContent:'space-around', paddingHorizontal:24, marginTop:'auto' },
  infoBox:       { alignItems:'center' },
  infoValue:     { color:'#fff', fontSize:20, fontWeight:'700' },
  infoLabel:     { color:'#fff', fontSize:12, fontWeight:'700', marginTop:2 },

  sectionHeader: { flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:12 },
  headerLeft:    { flexDirection:'row', alignItems:'center' },
  sectionTitle:  { fontSize:18, fontWeight:'600', color:'#333', marginLeft:8 },
  viewAll:       { fontSize:14, color:'#6C5CE7' },

  classBox:      {
    flexDirection:'row',
    backgroundColor:'#fff',
    borderRadius:12,
    paddingVertical:8,
    paddingHorizontal:12,
    marginVertical:6,
    marginHorizontal:16,
    elevation:0,
  },
  classLeft:   { flex:1 },
  courseText:  { fontSize:14, fontWeight:'600', color:'#333' },
  facultyText: { fontSize:12, color:'#666', marginTop:2 },
  timeText:    { fontSize:10, color:'#999', marginTop:2 },
  classRight:  { justifyContent:'space-between', alignItems:'flex-end' },
  hoursText:   { fontSize:12, fontWeight:'600', color:'#333' },
  roomText:    { fontSize:10, color:'#999', marginTop:2 },

  menuGrid: {
    flexDirection:'row',
    flexWrap:'wrap',
    justifyContent:'space-between',
    paddingHorizontal: MENU_GRID_PADDING,
  },
  menuBox:  {
    width: CARD_WIDTH,
    height: CARD_HEIGHT,
    backgroundColor:'#fff',
    borderRadius:12,
    borderWidth:1,
    borderColor:'#ddd',
    padding:12,
    marginVertical:8,
    alignItems:'center',
    justifyContent:'center',
    elevation:0,
  },
  menuLabel:{ fontSize:14, fontWeight:'600', color:'#333', marginTop:4, textAlign:'center' },
  menuText: { fontSize:12, color:'#666', marginTop:4, textAlign:'center' },
  menuTime: { fontSize:10, color:'#999', marginTop:4, textAlign:'center' },

  taskBox:    {
    flexDirection:'row',
    borderLeftWidth:4,
    borderRadius:12,
    padding:12,
    marginVertical:6,
    marginHorizontal:16,
    elevation:0,
  },
  taskCheck:{ marginRight:12, justifyContent:'center' },
  taskContent:{ flex:1 },
  taskTitle:{ fontSize:14, fontWeight:'600', color:'#333' },
  taskDetails:{ fontSize:12, color:'#666', marginTop:2 },
  priorityBadge:{ borderWidth:1, borderRadius:12, paddingHorizontal:8, paddingVertical:2, marginTop:4, alignSelf:'flex-start' },
  priorityText:{ fontSize:10, fontWeight:'600' },

  insightsContainer:{ backgroundColor:'#FFF9F5', padding:16, borderRadius:12 },
  insightBox:       {
    flexDirection:'row',
    alignItems:'center',
    borderWidth:1,
    borderRadius:12,
    padding:12,
    marginBottom:12,
    backgroundColor:'#fff',
  },
  insightSquare:{ width:20, height:20, borderRadius:2, marginRight:12 },
  insightContent:{ flex:1 },
  insightTitle:{ fontSize:14, fontWeight:'600', color:'#333' },
  insightSubtitle:{ fontSize:12, color:'#666', marginTop:2 },
  insightValue:{ fontSize:16, fontWeight:'700', color:'#333' },
});
