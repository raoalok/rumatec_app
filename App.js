import { StyleSheet } from 'react-native';
import { ScrollView } from 'react-native';
import Login from './src/screens/login/login';
import { Provider } from 'react-redux';
import { legacy_createStore as createStore, compose, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import reducer from './src/redux/reducers';
import { NavigationContainer } from '@react-navigation/native';

const composeEnhance = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;
const store = createStore(reducer, composeEnhance(applyMiddleware(thunk)));

export default function App() {
  return (
    <Provider store={store}>
      <ScrollView>
        <NavigationContainer>
          <Login />
        </NavigationContainer>
      </ScrollView>
    </Provider>
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
