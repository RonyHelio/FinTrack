import React, { useState, useCallback } from "react";
import {
  View,
  Text,
  FlatList,
  ActivityIndicator,
  RefreshControl,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useFocusEffect } from "@react-navigation/native";
import { categoriaService } from "../../services/categoriaService";
import type { CategoriaResponse } from "../../types";

export default function CategoriasScreen() {
  const [categorias, setCategorias] = useState<CategoriaResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadCategorias = useCallback(async () => {
    try {
      const data = await categoriaService.listarMinhas();
      setCategorias(data);
    } catch (error) {
      console.error("Erro ao carregar categorias:", error);
    } finally {
      setIsLoading(false);
      setRefreshing(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      setIsLoading(true);
      loadCategorias();
    }, [loadCategorias])
  );

  function onRefresh() {
    setRefreshing(true);
    loadCategorias();
  }

  function renderItem({ item }: { item: CategoriaResponse }) {
    const isGlobal = item.usuarioId === null;
    return (
      <View className="bg-dark-card rounded-2xl p-4 mb-3 border border-dark-border flex-row items-center">
        <View className="w-12 h-12 rounded-xl bg-primary-950 items-center justify-center mr-4">
          <Text className="text-xl">{item.icone}</Text>
        </View>
        <View className="flex-1">
          <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_500Medium" }}>
            {item.nome}
          </Text>
          <Text className="text-dark-muted text-xs mt-0.5" style={{ fontFamily: "Inter_400Regular" }}>
            {isGlobal ? "Categoria global" : "Categoria personalizada"}
          </Text>
        </View>
        {isGlobal ? (
          <View className="w-8 h-8 rounded-lg bg-primary-950 items-center justify-center">
            <Ionicons name="globe-outline" size={16} color="#818CF8" />
          </View>
        ) : (
          <View className="w-8 h-8 rounded-lg bg-success-50 items-center justify-center">
            <Ionicons name="person-outline" size={16} color="#10B981" />
          </View>
        )}
      </View>
    );
  }

  if (isLoading && !refreshing) {
    return (
      <SafeAreaView className="flex-1 bg-dark-bg items-center justify-center" edges={["top"]}>
        <ActivityIndicator size="large" color="#6366F1" />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-1 px-6 pt-4">
        {/* ─── Header ────────────────────────────────────────────── */}
        <Text className="text-dark-text text-2xl mb-2" style={{ fontFamily: "Inter_700Bold" }}>
          Categorias
        </Text>
        <Text className="text-dark-muted text-sm mb-5" style={{ fontFamily: "Inter_400Regular" }}>
          {categorias.length} categoria{categorias.length !== 1 ? "s" : ""} disponíve{categorias.length !== 1 ? "is" : "l"}
        </Text>

        {/* ─── Resumo ────────────────────────────────────────────── */}
        <View className="flex-row gap-3 mb-5">
          <View className="flex-1 bg-dark-card rounded-xl p-4 border border-dark-border items-center">
            <Ionicons name="globe-outline" size={20} color="#818CF8" />
            <Text className="text-dark-text text-lg mt-1" style={{ fontFamily: "Inter_700Bold" }}>
              {categorias.filter((c) => c.usuarioId === null).length}
            </Text>
            <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_400Regular" }}>
              Globais
            </Text>
          </View>
          <View className="flex-1 bg-dark-card rounded-xl p-4 border border-dark-border items-center">
            <Ionicons name="person-outline" size={20} color="#10B981" />
            <Text className="text-dark-text text-lg mt-1" style={{ fontFamily: "Inter_700Bold" }}>
              {categorias.filter((c) => c.usuarioId !== null).length}
            </Text>
            <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_400Regular" }}>
              Personalizadas
            </Text>
          </View>
        </View>

        {/* ─── Lista ─────────────────────────────────────────────── */}
        {categorias.length === 0 ? (
          <View className="flex-1 items-center justify-center">
            <View className="bg-dark-card rounded-2xl p-8 items-center border border-dark-border w-full">
              <Text className="text-5xl mb-4">📂</Text>
              <Text className="text-dark-text text-lg mb-2" style={{ fontFamily: "Inter_600SemiBold" }}>
                Nenhuma categoria
              </Text>
              <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
                As categorias globais serão{"\n"}carregadas do backend
              </Text>
            </View>
          </View>
        ) : (
          <FlatList
            data={categorias}
            keyExtractor={(item) => item.id}
            renderItem={renderItem}
            showsVerticalScrollIndicator={false}
            refreshControl={
              <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#6366F1" />
            }
            contentContainerStyle={{ paddingBottom: 16 }}
          />
        )}
      </View>
    </SafeAreaView>
  );
}
