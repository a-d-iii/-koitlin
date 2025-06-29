// src/screens/More.tsx

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';

export default function More() {
  return (
    <LinearGradient colors={['#69cbff', '#1cddfe']} style={{ flex: 1 }}>
      <View style={styles.container}>
        <Text style={styles.heading}>More</Text>
        {/* Add your “More” settings or utilities here */}
      </View>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  heading: { fontSize: 28, fontWeight: '700', color: '#333' },
});
