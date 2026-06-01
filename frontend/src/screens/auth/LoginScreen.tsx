import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../contexts/AuthContext";
import type { AuthStackParamList } from "../../navigation/AuthStack";
import type { ApiError } from "../../types";

type LoginNavigationProp = NativeStackNavigationProp<AuthStackParamList, "Login">;

export default function LoginScreen() {
  const navigation = useNavigation<LoginNavigationProp>();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  async function handleLogin() {
    if (!email.trim() || !senha.trim()) {
      Alert.alert("Atenção", "Preencha todos os campos.");
      return;
    }

    setIsLoading(true);
    try {
      await login(email.trim(), senha);
    } catch (error) {
      const apiError = error as ApiError;
      Alert.alert("Erro no login", apiError.mensagem || "Verifique suas credenciais.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <KeyboardAvoidingView
      className="flex-1 bg-dark-bg"
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView
        contentContainerStyle={{ flexGrow: 1, justifyContent: "center" }}
        keyboardShouldPersistTaps="handled"
      >
        <View className="flex-1 justify-center px-8">
          {/* ─── Header ──────────────────────────────────────────────── */}
          <View className="items-center mb-12">
            <Text className="text-5xl mb-3">💰</Text>
            <Text className="text-3xl font-bold text-dark-text" style={{ fontFamily: "Inter_700Bold" }}>
              FinTrack
            </Text>
            <Text className="text-dark-muted text-base mt-2" style={{ fontFamily: "Inter_400Regular" }}>
              Controle financeiro inteligente
            </Text>
          </View>

          {/* ─── Formulário ──────────────────────────────────────────── */}
          <View className="bg-dark-card rounded-2xl p-6 border border-dark-border">
            <Text className="text-dark-text text-lg mb-6" style={{ fontFamily: "Inter_600SemiBold" }}>
              Entrar na conta
            </Text>

            {/* E-mail */}
            <View className="mb-4">
              <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                E-mail
              </Text>
              <TextInput
                className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base"
                style={{ fontFamily: "Inter_400Regular" }}
                placeholder="seu@email.com"
                placeholderTextColor="#64748B"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                value={email}
                onChangeText={setEmail}
              />
            </View>

            {/* Senha */}
            <View className="mb-6">
              <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                Senha
              </Text>
              <View className="flex-row items-center bg-dark-surface border border-dark-border rounded-xl">
                <TextInput
                  className="flex-1 px-4 py-3.5 text-dark-text text-base"
                  style={{ fontFamily: "Inter_400Regular" }}
                  placeholder="••••••••"
                  placeholderTextColor="#64748B"
                  secureTextEntry={!showPassword}
                  value={senha}
                  onChangeText={setSenha}
                />
                <TouchableOpacity
                  className="px-4 py-3.5"
                  onPress={() => setShowPassword(!showPassword)}
                >
                  <Text className="text-primary-400 text-sm" style={{ fontFamily: "Inter_500Medium" }}>
                    {showPassword ? "Ocultar" : "Mostrar"}
                  </Text>
                </TouchableOpacity>
              </View>
            </View>

            {/* Botão login */}
            <TouchableOpacity
              className={`rounded-xl py-4 items-center ${isLoading ? "bg-primary-800" : "bg-primary-500"}`}
              onPress={handleLogin}
              disabled={isLoading}
              activeOpacity={0.8}
            >
              {isLoading ? (
                <ActivityIndicator color="#FFFFFF" />
              ) : (
                <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
                  Entrar
                </Text>
              )}
            </TouchableOpacity>
          </View>

          {/* ─── Link para registro ──────────────────────────────────── */}
          <TouchableOpacity
            className="mt-8 items-center"
            onPress={() => navigation.navigate("Register")}
          >
            <Text className="text-dark-muted text-base" style={{ fontFamily: "Inter_400Regular" }}>
              Não tem conta?{" "}
              <Text className="text-primary-400" style={{ fontFamily: "Inter_600SemiBold" }}>
                Criar conta
              </Text>
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
