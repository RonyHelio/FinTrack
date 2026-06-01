import React from "react";
import { View, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function CategoriasScreen() {
  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-1 px-6 pt-4">
        {/* ─── Header ────────────────────────────────────────────── */}
        <Text className="text-dark-text text-2xl mb-6" style={{ fontFamily: "Inter_700Bold" }}>
          Categorias
        </Text>

        {/* ─── Estado vazio ───────────────────────────────────────── */}
        <View className="flex-1 items-center justify-center">
          <View className="bg-dark-card rounded-2xl p-8 items-center border border-dark-border w-full">
            <Text className="text-5xl mb-4">📂</Text>
            <Text className="text-dark-text text-lg mb-2" style={{ fontFamily: "Inter_600SemiBold" }}>
              Categorias
            </Text>
            <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
              Suas categorias globais e{"\n"}personalizadas aparecerão aqui
            </Text>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}
