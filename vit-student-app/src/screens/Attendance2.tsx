import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export default function Attendance2() {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Attendance 2</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff' },
  text: { fontSize: 24, fontWeight: '700', color: '#333' },
});
