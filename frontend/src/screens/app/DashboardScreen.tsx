import React from "react";
import { View, Text, ScrollView } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useAuth } from "../../contexts/AuthContext";

export default function DashboardScreen() {
  const { user } = useAuth();

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <ScrollView className="flex-1 px-6 pt-4">
        {/* ─── Header ────────────────────────────────────────────── */}
        <View className="mb-6">
          <Text className="text-dark-muted text-sm" style={{ fontFamily: "Inter_400Regular" }}>
            Olá, 👋
          </Text>
          <Text className="text-dark-text text-2xl mt-1" style={{ fontFamily: "Inter_700Bold" }}>
            {user?.nome || "Usuário"}
          </Text>
        </View>

        {/* ─── Saldo Total ───────────────────────────────────────── */}
        <View className="bg-primary-600 rounded-2xl p-6 mb-6">
          <Text className="text-primary-200 text-sm" style={{ fontFamily: "Inter_500Medium" }}>
            Saldo total
          </Text>
          <Text className="text-white text-3xl mt-2" style={{ fontFamily: "Inter_700Bold" }}>
            R$ 0,00
          </Text>
        </View>

        {/* ─── Receitas e Despesas ────────────────────────────────── */}
        <View className="flex-row gap-4 mb-6">
          <View className="flex-1 bg-dark-card rounded-2xl p-5 border border-dark-border">
            <Text className="text-dark-muted text-xs mb-1" style={{ fontFamily: "Inter_500Medium" }}>
              Receitas
            </Text>
            <Text className="text-success-500 text-xl" style={{ fontFamily: "Inter_700Bold" }}>
              R$ 0,00
            </Text>
          </View>
          <View className="flex-1 bg-dark-card rounded-2xl p-5 border border-dark-border">
            <Text className="text-dark-muted text-xs mb-1" style={{ fontFamily: "Inter_500Medium" }}>
              Despesas
            </Text>
            <Text className="text-danger-500 text-xl" style={{ fontFamily: "Inter_700Bold" }}>
              R$ 0,00
            </Text>
          </View>
        </View>

        {/* ─── Últimas transações (placeholder para conectar na Etapa 7) ─── */}
        <View className="bg-dark-card rounded-2xl p-5 border border-dark-border mb-6">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Últimas transações
          </Text>
          <View className="items-center py-8">
            <Text className="text-4xl mb-3">📊</Text>
            <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
              Conecte ao backend para ver{"\n"}suas transações aqui
            </Text>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
