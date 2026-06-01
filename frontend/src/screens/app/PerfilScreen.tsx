import React from "react";
import { View, Text, TouchableOpacity, Alert } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useAuth } from "../../contexts/AuthContext";

export default function PerfilScreen() {
  const { user, logout } = useAuth();

  async function handleLogout() {
    Alert.alert(
      "Sair da conta",
      "Tem certeza que deseja sair?",
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Sair",
          style: "destructive",
          onPress: async () => {
            await logout();
          },
        },
      ]
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-1 px-6 pt-4">
        {/* ─── Header ────────────────────────────────────────────── */}
        <Text className="text-dark-text text-2xl mb-6" style={{ fontFamily: "Inter_700Bold" }}>
          Perfil
        </Text>

        {/* ─── Avatar e dados ────────────────────────────────────── */}
        <View className="bg-dark-card rounded-2xl p-6 border border-dark-border mb-6 items-center">
          <View className="w-20 h-20 rounded-full bg-primary-600 items-center justify-center mb-4">
            <Text className="text-white text-3xl" style={{ fontFamily: "Inter_700Bold" }}>
              {user?.nome?.charAt(0)?.toUpperCase() || "?"}
            </Text>
          </View>
          <Text className="text-dark-text text-xl" style={{ fontFamily: "Inter_600SemiBold" }}>
            {user?.nome || "Usuário"}
          </Text>
          <Text className="text-dark-muted text-sm mt-1" style={{ fontFamily: "Inter_400Regular" }}>
            {user?.email || "email@exemplo.com"}
          </Text>
        </View>

        {/* ─── Informações ───────────────────────────────────────── */}
        <View className="bg-dark-card rounded-2xl border border-dark-border mb-6">
          <View className="flex-row items-center p-4 border-b border-dark-border">
            <View className="w-10 h-10 rounded-xl bg-primary-950 items-center justify-center mr-4">
              <Ionicons name="person-outline" size={20} color="#818CF8" />
            </View>
            <View className="flex-1">
              <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_500Medium" }}>
                Nome
              </Text>
              <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_400Regular" }}>
                {user?.nome}
              </Text>
            </View>
          </View>

          <View className="flex-row items-center p-4 border-b border-dark-border">
            <View className="w-10 h-10 rounded-xl bg-primary-950 items-center justify-center mr-4">
              <Ionicons name="mail-outline" size={20} color="#818CF8" />
            </View>
            <View className="flex-1">
              <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_500Medium" }}>
                E-mail
              </Text>
              <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_400Regular" }}>
                {user?.email}
              </Text>
            </View>
          </View>

          <View className="flex-row items-center p-4">
            <View className="w-10 h-10 rounded-xl bg-primary-950 items-center justify-center mr-4">
              <Ionicons name="shield-checkmark-outline" size={20} color="#818CF8" />
            </View>
            <View className="flex-1">
              <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_500Medium" }}>
                ID do usuário
              </Text>
              <Text className="text-dark-text text-xs" style={{ fontFamily: "Inter_400Regular" }} numberOfLines={1}>
                {user?.id}
              </Text>
            </View>
          </View>
        </View>

        {/* ─── Botão logout ──────────────────────────────────────── */}
        <TouchableOpacity
          className="bg-danger-600 rounded-xl py-4 items-center flex-row justify-center"
          onPress={handleLogout}
          activeOpacity={0.8}
        >
          <Ionicons name="log-out-outline" size={20} color="#FFFFFF" />
          <Text className="text-white text-base ml-2" style={{ fontFamily: "Inter_600SemiBold" }}>
            Sair da conta
          </Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}
