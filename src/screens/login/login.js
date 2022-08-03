import * as React from 'react'
import {
  StyleSheet,
  SafeAreaView,
  Keyboard,
  KeyboardAvoidingView,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from 'react-native'
import { Text, Button } from 'react-native-paper'
import { useForm, Controller } from 'react-hook-form'
import Api from '../../Api';
import { login } from '../../redux/actions';
import { useSelector } from 'react-redux';

const Login = () => {

  const auth = useSelector(state => state.auth);


  const {
    control,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm({ mode: 'onBlur' });

  React.useEffect(() => {
    if (auth.type && auth.user) {
      navigate('/');
    }
  }, [auth]);

  const onSubmit = async formValues => {
    try {
      console.log(formValues);
      await dispatch(login(formValues)).then(() => {
        navigate('/');
      });
    } catch (err) {
      errorNotification(err);
    }
  };



  return (
    <SafeAreaView>
      <KeyboardAvoidingView style={styles.containerView} behavior="padding">
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
          <View style={styles.loginScreenContainer}>
            <View style={styles.loginFormView}>
              <Text style={styles.logoText}>Rumatec Vetcare</Text>
              <TextInput
                placeholder="Email"
                placeholderColor="#c4c3cb"
                style={styles.loginFormTextInput}
              />
              <TextInput
                placeholder="Password"
                placeholderColor="#c4c3cb"
                style={styles.loginFormTextInput}
                secureTextEntry={true}
              />
              <Button
                mode="contained"
                style={styles.loginButton}
                onPress={() => onSubmit()}>
                Login
              </Button>
            </View>
          </View>
        </TouchableWithoutFeedback>
      </KeyboardAvoidingView>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  containerView: {
    flex: 1,
    alignItems: 'center',
  },
  loginScreenContainer: {
    flex: 1,
  },
  logoText: {
    fontSize: 30,
    fontWeight: '800',
    marginTop: 120,
    marginBottom: 30,
    textAlign: 'center',
  },
  loginFormView: {
    flex: 1,
  },
  loginFormTextInput: {
    height: 43,
    fontSize: 14,
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#eaeaea',
    backgroundColor: '#fafafa',
    paddingLeft: 10,
    marginTop: 5,
    marginBottom: 10,
  },
  loginButton: {
    backgroundColor: '#3897f1',
    borderRadius: 5,
    height: 45,
    marginTop: 10,
    width: 350,
    alignItems: 'center',
  },
})

export default Login
