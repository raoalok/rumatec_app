import { StyleSheet } from 'react-native';
import { ScrollView } from 'react-native';
import Login from './src/screens/login/login';

export default function App() {
  return (
    <ScrollView>
      <Login />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
