// src/navigation/AppNavigator.tsx

import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import Home from '../screens/Home';
import Planner from '../screens/Planner';
import Attendance from '../screens/Attendance';
import More from '../screens/More';
import FoodMenuScreen from '../screens/FoodMenuScreen';
import MonthlyMenuScreen from '../screens/MonthlyMenuScreen';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

function Tabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        tabBarStyle: { backgroundColor: '#fff' },
      }}
    >
      <Tab.Screen name="Home" component={Home} />
      <Tab.Screen name="Planner" component={Planner} />
      <Tab.Screen name="Attendance" component={Attendance} />
      <Tab.Screen name="More" component={More} />
    </Tab.Navigator>
  );
}

export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Tabs"
          component={Tabs}
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="FoodMenuScreen"
          component={FoodMenuScreen}
          options={{ title: 'Full Menu' }}
        />
        <Stack.Screen
          name="MonthlyMenuScreen"
          component={MonthlyMenuScreen}
          options={{ title: 'Monthly Menu' }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
