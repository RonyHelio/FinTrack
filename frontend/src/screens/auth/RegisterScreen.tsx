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

type RegisterNavigationProp = NativeStackNavigationProp<AuthStackParamList, "Register">;

export default function RegisterScreen() {
  const navigation = useNavigation<RegisterNavigationProp>();
  const { register } = useAuth();

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleRegister() {
    if (!nome.trim() || !email.trim() || !senha.trim() || !confirmarSenha.trim()) {
      Alert.alert("Atenção", "Preencha todos os campos.");
      return;
    }

    if (senha !== confirmarSenha) {
      Alert.alert("Atenção", "As senhas não coincidem.");
      return;
    }

    if (senha.length < 6) {
      Alert.alert("Atenção", "A senha deve ter pelo menos 6 caracteres.");
      return;
    }

    setIsLoading(true);
    try {
      await register(nome.trim(), email.trim(), senha);
    } catch (error) {
      const apiError = error as ApiError;
      Alert.alert("Erro no registro", apiError.mensagem || "Não foi possível criar a conta.");
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
          <View className="items-center mb-10">
            <Text className="text-5xl mb-3">🚀</Text>
            <Text className="text-2xl font-bold text-dark-text" style={{ fontFamily: "Inter_700Bold" }}>
              Criar conta
            </Text>
            <Text className="text-dark-muted text-base mt-2" style={{ fontFamily: "Inter_400Regular" }}>
              Comece a organizar suas finanças
            </Text>
          </View>

          {/* ─── Formulário ──────────────────────────────────────────── */}
          <View className="bg-dark-card rounded-2xl p-6 border border-dark-border">
            {/* Nome */}
            <View className="mb-4">
              <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                Nome completo
              </Text>
              <TextInput
                className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base"
                style={{ fontFamily: "Inter_400Regular" }}
                placeholder="Seu nome"
                placeholderTextColor="#64748B"
                autoCapitalize="words"
                value={nome}
                onChangeText={setNome}
              />
            </View>

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
            <View className="mb-4">
              <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                Senha
              </Text>
              <TextInput
                className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base"
                style={{ fontFamily: "Inter_400Regular" }}
                placeholder="Mínimo 6 caracteres"
                placeholderTextColor="#64748B"
                secureTextEntry
                value={senha}
                onChangeText={setSenha}
              />
            </View>

            {/* Confirmar senha */}
            <View className="mb-6">
              <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
                Confirmar senha
              </Text>
              <TextInput
                className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base"
                style={{ fontFamily: "Inter_400Regular" }}
                placeholder="Repita a senha"
                placeholderTextColor="#64748B"
                secureTextEntry
                value={confirmarSenha}
                onChangeText={setConfirmarSenha}
              />
            </View>

            {/* Botão registrar */}
            <TouchableOpacity
              className={`rounded-xl py-4 items-center ${isLoading ? "bg-primary-800" : "bg-primary-500"}`}
              onPress={handleRegister}
              disabled={isLoading}
              activeOpacity={0.8}
            >
              {isLoading ? (
                <ActivityIndicator color="#FFFFFF" />
              ) : (
                <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
                  Criar conta
                </Text>
              )}
            </TouchableOpacity>
          </View>

          {/* ─── Link para login ─────────────────────────────────────── */}
          <TouchableOpacity
            className="mt-8 items-center"
            onPress={() => navigation.goBack()}
          >
            <Text className="text-dark-muted text-base" style={{ fontFamily: "Inter_400Regular" }}>
              Já tem conta?{" "}
              <Text className="text-primary-400" style={{ fontFamily: "Inter_600SemiBold" }}>
                Entrar
              </Text>
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
