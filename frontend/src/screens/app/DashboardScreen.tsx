import React, { useState, useCallback } from "react";
import {
  View,
  Text,
  ScrollView,
  ActivityIndicator,
  RefreshControl,
  TouchableOpacity,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useAuth } from "../../contexts/AuthContext";
import { transacaoService } from "../../services/transacaoService";
import { formatCurrency, formatDate, getMonthName } from "../../utils/format";
import type { DashboardResponse, TransacaoResponse, GastoPorCategoria } from "../../types";
import type { RootStackParamList } from "../../navigation/RootNavigator";

type NavProp = NativeStackNavigationProp<RootStackParamList>;

export default function DashboardScreen() {
  const { user } = useAuth();
  const navigation = useNavigation<NavProp>();
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const mesAtual = new Date().getMonth() + 1;
  const anoAtual = new Date().getFullYear();

  const loadDashboard = useCallback(async () => {
    try {
      const data = await transacaoService.getDashboard();
      setDashboard(data);
    } catch (error) {
      console.error("Erro ao carregar dashboard:", error);
    } finally {
      setIsLoading(false);
      setRefreshing(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      setIsLoading(true);
      loadDashboard();
    }, [loadDashboard])
  );

  function onRefresh() {
    setRefreshing(true);
    loadDashboard();
  }

  if (isLoading && !refreshing) {
    return (
      <SafeAreaView className="flex-1 bg-dark-bg items-center justify-center" edges={["top"]}>
        <ActivityIndicator size="large" color="#6366F1" />
      </SafeAreaView>
    );
  }

  const saldo = dashboard?.saldoTotal ?? 0;
  const receitas = dashboard?.receitasMes ?? 0;
  const despesas = dashboard?.despesasMes ?? 0;
  const qtdTransacoes = dashboard?.quantidadeTransacoesMes ?? 0;
  const ultimas = dashboard?.ultimas5Transacoes ?? [];
  const gastos = dashboard?.gastosPorCategoria ?? [];

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <ScrollView
        className="flex-1 px-6 pt-4"
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#6366F1" />
        }
        showsVerticalScrollIndicator={false}
      >
        {/* ─── Header ────────────────────────────────────────────── */}
        <View className="flex-row items-center justify-between mb-6">
          <View>
            <Text className="text-dark-muted text-sm" style={{ fontFamily: "Inter_400Regular" }}>
              Olá, 👋
            </Text>
            <Text className="text-dark-text text-2xl mt-1" style={{ fontFamily: "Inter_700Bold" }}>
              {user?.nome || "Usuário"}
            </Text>
          </View>
          <TouchableOpacity
            className="w-12 h-12 rounded-full bg-primary-600 items-center justify-center"
            onPress={() => navigation.navigate("TransacaoForm")}
            activeOpacity={0.8}
          >
            <Ionicons name="add" size={26} color="#FFFFFF" />
          </TouchableOpacity>
        </View>

        {/* ─── Saldo Total ───────────────────────────────────────── */}
        <View className="bg-primary-600 rounded-2xl p-6 mb-4">
          <Text className="text-primary-200 text-sm" style={{ fontFamily: "Inter_500Medium" }}>
            Saldo total
          </Text>
          <Text className="text-white text-3xl mt-2" style={{ fontFamily: "Inter_700Bold" }}>
            {formatCurrency(saldo)}
          </Text>
          <Text className="text-primary-200 text-xs mt-2" style={{ fontFamily: "Inter_400Regular" }}>
            {getMonthName(mesAtual)} de {anoAtual} • {qtdTransacoes} lançamento{qtdTransacoes !== 1 ? "s" : ""}
          </Text>
        </View>

        {/* ─── Receitas e Despesas ────────────────────────────────── */}
        <View className="flex-row gap-4 mb-6">
          <View className="flex-1 bg-dark-card rounded-2xl p-5 border border-dark-border">
            <View className="flex-row items-center mb-2">
              <View className="w-8 h-8 rounded-lg bg-success-50 items-center justify-center mr-2">
                <Ionicons name="arrow-up" size={16} color="#10B981" />
              </View>
              <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_500Medium" }}>
                Receitas
              </Text>
            </View>
            <Text className="text-success-500 text-xl" style={{ fontFamily: "Inter_700Bold" }}>
              {formatCurrency(receitas)}
            </Text>
          </View>
          <View className="flex-1 bg-dark-card rounded-2xl p-5 border border-dark-border">
            <View className="flex-row items-center mb-2">
              <View className="w-8 h-8 rounded-lg bg-danger-50 items-center justify-center mr-2">
                <Ionicons name="arrow-down" size={16} color="#EF4444" />
              </View>
              <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_500Medium" }}>
                Despesas
              </Text>
            </View>
            <Text className="text-danger-500 text-xl" style={{ fontFamily: "Inter_700Bold" }}>
              {formatCurrency(despesas)}
            </Text>
          </View>
        </View>

        {/* ─── Gastos por Categoria ──────────────────────────────── */}
        <View className="bg-dark-card rounded-2xl p-5 border border-dark-border mb-6">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Despesas por categoria
          </Text>
          {gastos.length === 0 ? (
            <View className="items-center py-6">
              <Text className="text-3xl mb-2">📊</Text>
              <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
                Nenhuma despesa este mês
              </Text>
            </View>
          ) : (
            gastos.map((gasto: GastoPorCategoria) => {
              const percent = despesas > 0 ? (gasto.totalGasto / despesas) * 100 : 0;
              return (
                <View key={gasto.categoriaId} className="mb-4">
                  <View className="flex-row items-center justify-between mb-2">
                    <View className="flex-row items-center flex-1">
                      <Text className="text-lg mr-2">{gasto.categoriaIcone}</Text>
                      <Text
                        className="text-dark-text text-sm flex-1"
                        style={{ fontFamily: "Inter_500Medium" }}
                        numberOfLines={1}
                      >
                        {gasto.categoriaNome}
                      </Text>
                    </View>
                    <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_600SemiBold" }}>
                      {formatCurrency(gasto.totalGasto)}
                    </Text>
                  </View>
                  {/* Barra de progresso */}
                  <View className="h-2 bg-dark-surface rounded-full overflow-hidden">
                    <View
                      className="h-2 bg-primary-500 rounded-full"
                      style={{ width: `${Math.min(percent, 100)}%` }}
                    />
                  </View>
                  <Text className="text-dark-muted text-xs mt-1" style={{ fontFamily: "Inter_400Regular" }}>
                    {percent.toFixed(0)}% do total • {gasto.quantidadeTransacoes} lançamento{gasto.quantidadeTransacoes !== 1 ? "s" : ""}
                  </Text>
                </View>
              );
            })
          )}
        </View>

        {/* ─── Últimas 5 Transações ──────────────────────────────── */}
        <View className="bg-dark-card rounded-2xl p-5 border border-dark-border mb-8">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Últimas transações
          </Text>
          {ultimas.length === 0 ? (
            <View className="items-center py-6">
              <Text className="text-3xl mb-2">💸</Text>
              <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
                Nenhuma transação registrada
              </Text>
            </View>
          ) : (
            ultimas.map((t: TransacaoResponse, idx: number) => (
              <View
                key={t.id}
                className={`flex-row items-center py-3 ${
                  idx < ultimas.length - 1 ? "border-b border-dark-border" : ""
                }`}
              >
                <View
                  className={`w-10 h-10 rounded-xl items-center justify-center mr-3 ${
                    t.tipo === "receita" ? "bg-success-50" : "bg-danger-50"
                  }`}
                >
                  <Ionicons
                    name={t.tipo === "receita" ? "arrow-up" : "arrow-down"}
                    size={18}
                    color={t.tipo === "receita" ? "#10B981" : "#EF4444"}
                  />
                </View>
                <View className="flex-1">
                  <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_500Medium" }} numberOfLines={1}>
                    {t.descricao}
                  </Text>
                  <Text className="text-dark-muted text-xs mt-0.5" style={{ fontFamily: "Inter_400Regular" }}>
                    {t.categoriaNome} • {formatDate(t.data)}
                  </Text>
                </View>
                <Text
                  className={`text-sm ${t.tipo === "receita" ? "text-success-500" : "text-danger-500"}`}
                  style={{ fontFamily: "Inter_600SemiBold" }}
                >
                  {t.tipo === "receita" ? "+" : "-"}{formatCurrency(t.valor)}
                </Text>
              </View>
            ))
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
