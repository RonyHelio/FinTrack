import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { AuthStackParamList } from "../../navigation/AuthStack";

type NavProp = NativeStackNavigationProp<AuthStackParamList>;

export default function ForgotPasswordScreen() {
  const navigation = useNavigation<NavProp>();
  const [email, setEmail] = useState("");

  function handleReset() {
    if (!email) {
      if (Platform.OS === "web") {
        window.alert("Por favor, preencha seu e-mail.");
      } else {
        Alert.alert("Erro", "Por favor, preencha seu e-mail.");
      }
      return;
    }

    if (Platform.OS === "web") {
      window.alert("Sucesso! Instruções enviadas para " + email);
      navigation.goBack();
    } else {
      Alert.alert(
        "Sucesso",
        "Instruções enviadas para " + email,
        [{ text: "OK", onPress: () => navigation.goBack() }]
      );
    }
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top", "bottom"]}>
      <KeyboardAvoidingView
        className="flex-1"
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          contentContainerStyle={{ flexGrow: 1, padding: 24, justifyContent: "center" }}
          keyboardShouldPersistTaps="handled"
        >
          <TouchableOpacity
            className="w-10 h-10 rounded-full bg-dark-card items-center justify-center mb-8 border border-dark-border"
            onPress={() => navigation.goBack()}
          >
            <Ionicons name="arrow-back" size={20} color="#F8FAFC" />
          </TouchableOpacity>

          <View className="mb-10">
            <Text className="text-dark-text text-3xl mb-2" style={{ fontFamily: "Inter_700Bold" }}>
              Recuperar Senha
            </Text>
            <Text className="text-dark-muted text-base" style={{ fontFamily: "Inter_400Regular" }}>
              Digite seu e-mail para receber as instruções de recuperação.
            </Text>
          </View>

          <View className="space-y-4 mb-8">
            <View>
              <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                E-mail
              </Text>
              <View className="flex-row items-center bg-dark-card border border-dark-border rounded-xl px-4 h-14">
                <Ionicons name="mail-outline" size={20} color="#94A3B8" />
                <TextInput
                  className="flex-1 text-dark-text ml-3"
                  style={{ fontFamily: "Inter_400Regular" }}
                  placeholder="Seu e-mail cadastrado"
                  placeholderTextColor="#94A3B8"
                  value={email}
                  onChangeText={setEmail}
                  keyboardType="email-address"
                  autoCapitalize="none"
                />
              </View>
            </View>
          </View>

          <TouchableOpacity
            className="w-full h-14 bg-primary-600 rounded-xl items-center justify-center shadow-lg mb-4"
            onPress={handleReset}
            activeOpacity={0.8}
          >
            <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              Enviar Instruções
            </Text>
          </TouchableOpacity>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
